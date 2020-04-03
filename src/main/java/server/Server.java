package server;

import com.artem.server.api.HttpHandler;
import com.artem.server.api.HttpSession;
import server.request.Request;
import server.request.RequestReader;
import server.response.Response;
import server.response.ResponseWriter;
import server.sessions.Session;
import server.sessions.SessionRemover;
import utils.CommonConstants;
import utils.HTTPUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final String INFO_ABOUT_REQUEST = "got request, method: %s, requested resource: %s \r\n";
    private static final String PATH_TO_FILE_WITH_STOP_KEY = "StopServer.txt";
    private static final String MISSING_FILE_WITH_STOP_KEY = "File with Stop Key not found";
    private static final String UNKNOWN_ERROR_MESSAGE = "Unknown error";
    private static final String USER_ID_KEY = "user_id";
    private static final String PATH_TO_CATALOG_WITH_URL_MODULE = "deploy\\";
    private static final String PORT_KEY = "port";
    private static final String BUFFER_SIZE_KEY = "bufferSize";
    private static final String THREAD_POOL_CAPACITY_KEY = "threadPoolCapacity";
    private static final String SESSION_LIFE_TIME_KEY = "sessionLifeTime";
    private static final String TIMER_START_INTERVAL_KEY = "timerStartInterval";
    private static final String HOST_KEY = "host";
    private static final String PATH_TO_RESOURCES_KEY = "pathToResources";
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 1488;
    private static final int DEFAULT_BUFFER_SIZE = 10485760;
    private static final int DEFAULT_THREAD_POOL_CAPACITY = 20;
    private static final int MAX_CONNECTIONS_COUNT = DEFAULT_THREAD_POOL_CAPACITY;
    private static final int TIMER_START_DELAY = 0;
    private static final long DEFAULT_SESSION_LIFE_TIME = 600000;
    private static final long DEFAULT_TIMER_START_INTERVAL = 300000;
    private UUID stopServerKey;
    private Integer port;
    private Integer bufferSize;
    private Map<String, HttpSession> sessions = new HashMap<>();
    private ExecutorService threadPool;
    private Map<String, HttpHandler> urlHandlers = new HashMap<>();
    private Properties properties;
    private boolean serverIsAlive;

    public Server(Properties properties)
            throws NoSuchMethodException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        this.properties = properties;
        ApplicationLoader applicationLoader = new ApplicationLoader();
        urlHandlers.putAll(applicationLoader.load(PATH_TO_CATALOG_WITH_URL_MODULE));
        this.port = Optional.ofNullable(properties.getProperty(PORT_KEY)).map(Integer::parseInt).orElse(DEFAULT_PORT);
        this.bufferSize = Optional.ofNullable(properties.getProperty(BUFFER_SIZE_KEY)).map(Integer::parseInt).orElse(DEFAULT_BUFFER_SIZE);
        threadPool = Executors.newFixedThreadPool(Optional.ofNullable(properties.getProperty(THREAD_POOL_CAPACITY_KEY)).map(Integer::parseInt)
                .orElse(DEFAULT_THREAD_POOL_CAPACITY));
        serverIsAlive = true;
        Timer sessionRemoveTimer = new Timer();
        sessionRemoveTimer.scheduleAtFixedRate(new SessionRemover(sessions, Optional.ofNullable(properties.getProperty
                (SESSION_LIFE_TIME_KEY)).map(Long::parseLong).orElse(DEFAULT_SESSION_LIFE_TIME)), TIMER_START_DELAY, Optional.ofNullable
                (properties.getProperty(TIMER_START_INTERVAL_KEY)).map(Long::parseLong).orElse(DEFAULT_TIMER_START_INTERVAL));
    }

    public void start() throws IOException {
        String serverAddress = Optional.ofNullable(properties.getProperty(HOST_KEY)).orElse(DEFAULT_HOST);
        InetAddress address = InetAddress.getByName(serverAddress);
        ServerSocket serverSocket = new ServerSocket(port, MAX_CONNECTIONS_COUNT, address);
        try(FileWriter fileWriter = new FileWriter(PATH_TO_FILE_WITH_STOP_KEY)){
            stopServerKey = UUID.randomUUID();
            fileWriter.write(serverAddress + CommonConstants.COLON_SYMBOL + port + CommonConstants.SLASH + stopServerKey);
        }
        catch (IOException e) {
            LOGGER.log(Level.INFO, MISSING_FILE_WITH_STOP_KEY);
        }
        while (serverIsAlive) {
            acceptAndProcess(serverSocket);
        }
    }


    private void acceptAndProcess(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        threadPool.submit(() -> {
            try {
                process(socket);
            } catch (IOException e) {
                LOGGER.log(Level.INFO, UNKNOWN_ERROR_MESSAGE);
            }
        });
    }

    private void process(Socket socket) throws IOException {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream(), bufferSize);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream(), bufferSize)) {
            RequestReader requestReader = new RequestReader();
            Request request = requestReader.readRequest(bufferedInputStream);
            checkForStopKey(request.getResource());
            LOGGER.log(Level.INFO, String.format(INFO_ABOUT_REQUEST, request.getMethod(), request.getResource()));
            Response response = new Response();
            request.setSession(getSession(request, response));
            HttpHandler httpHandler = getHttpHandler(urlHandlers, request);
            if (isGetRequest(request)) {
                httpHandler.doGet(request, response);
            } else {
                httpHandler.doPost(request, response);
            }
            ResponseWriter responseWriter = new ResponseWriter();
            responseWriter.write(response, bufferedOutputStream, bufferSize);
        } catch (SocketException e) {
            LOGGER.log(Level.WARNING, UNKNOWN_ERROR_MESSAGE, e);
        }
    }

    private boolean isGetRequest(Request request) {
        return request.getMethod().equals(HTTPUtils.GET);
    }

    private HttpSession getSession(Request request, Response response) {
        if (sessionDoesNotExists(request)) {
            Session session = new Session();
            session.setLastRequestTime(new Date());
            String userID = UUID.randomUUID().toString();
            response.setCookie(USER_ID_KEY,  userID + CommonConstants.SEMICOLON_SYMBOL + CommonConstants.SPACE);
            sessions.put(userID, session);
            return session;
        }
        Session session = (Session) sessions.get(request.getCookie(USER_ID_KEY).split(CommonConstants.SEMICOLON_SYMBOL)[0]);
        session.setLastRequestTime(new Date());
        return session;
    }

    private boolean sessionDoesNotExists(Request request) {
        return !(request.getCookie(USER_ID_KEY) != null && sessions.containsKey(request.getCookie(USER_ID_KEY).
                split(CommonConstants.SEMICOLON_SYMBOL)[0]));
    }

    private HttpHandler getHttpHandler(Map<String, HttpHandler> urlHandlers, Request request) {
        if (urlHandlers.containsKey(request.getResource())) {
            return urlHandlers.get(request.getResource());
        } else {
            return new DefaultHttpHandler(properties.getProperty(PATH_TO_RESOURCES_KEY));
        }
    }

    private void checkForStopKey(String requestURL){
        if (requestURL.equals(String.valueOf(stopServerKey))){
            serverIsAlive = false;
        }
    }

}
