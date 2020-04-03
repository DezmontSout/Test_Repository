import server.Server;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static final String ADDRESS_PROPERTIES = "C:\\Users\\Dezmont\\IdeaProjects\\MyServer\\src\\main\\resources\\server.properties";
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(ADDRESS_PROPERTIES)) {
            properties.load(fileInputStream);
            Server server = new Server(properties);
            server.start();
        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Cant start server, check logs", e);
        }
    }
}
