package server;

import com.artem.server.api.HttpRequest;
import com.artem.server.api.HttpResponse;
import utils.HTTPUtils;
import utils.ServerUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultHttpHandler implements com.artem.server.api.HttpHandler {

    public static final String DEFAULT_HTML_FILE = "FileNotFound.html";
    public static final String FILE_NOT_FOUND_MESSAGE = "File not found \r\n";
    private String addressResources;
    private static final Logger LOGGER = Logger.getLogger(DefaultHttpHandler.class.getName());

    DefaultHttpHandler(String addressResources) {
        this.addressResources = addressResources;
    }



    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        File file = new File(addressResources + request.getResource());
        if (ServerUtils.isFileExists(file, request.getResource())) {
            response.setStatus(HTTPUtils.OK_STATUS);
        } else {
            response.setStatus(HTTPUtils.NOT_FOUND_STATUS);
            file = new File(addressResources + DEFAULT_HTML_FILE);
        }
        try {
            response.setResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, FILE_NOT_FOUND_MESSAGE);
        }
        response.setHeaders(HTTPUtils.DATA_HEADER, DateTimeFormatter.BASIC_ISO_DATE.format(LocalDateTime.now()));
        response.setHeaders(HTTPUtils.CONTENT_LENGTH_HEADER, String.valueOf(file.length()));
        response.setHeaders(HTTPUtils.CONTENT_TYPE_HEADER, HTTPUtils.CONTENT_TYPE_TEXT_HTML);
    }


    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        File file = new File(addressResources + request.getResource());
        if (ServerUtils.isFileExists(file, request.getResource())) {
            response.setStatus(HTTPUtils.OK_STATUS);
        } else {
            response.setStatus(HTTPUtils.NOT_FOUND_STATUS);
            file = new File(addressResources + DEFAULT_HTML_FILE);
        }
        try {
            response.setResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, FILE_NOT_FOUND_MESSAGE);
        }
        response.setHeaders(HTTPUtils.DATA_HEADER, DateTimeFormatter.BASIC_ISO_DATE.format(LocalDateTime.now()));
        response.setHeaders(HTTPUtils.CONTENT_LENGTH_HEADER, String.valueOf(file.length()));
        response.setHeaders(HTTPUtils.CONTENT_TYPE_HEADER, HTTPUtils.CONTENT_TYPE_TEXT_HTML);
    }
}
