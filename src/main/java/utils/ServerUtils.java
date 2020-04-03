package utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class ServerUtils {

    private static final int END_OF_REQUEST = -1;
    private static final String AMPERSAND = "&";
    private static final String EQUALLY_SIGN = "=";

    private ServerUtils() {
    }

    public static String readLine(BufferedInputStream bufferedInputStream) throws IOException {
        StringBuilder line = new StringBuilder();
            int symbol = bufferedInputStream.read();
            while (symbol != END_OF_REQUEST && (char) symbol != HTTPUtils.REQUEST_HEADER_END_LINE) {
                line.append((char) symbol);
                symbol = bufferedInputStream.read();
            }
            if (symbol != END_OF_REQUEST) {
                line.append((char) symbol);
            }
        return line.toString();
    }

    public static String readParametersFromPost(BufferedInputStream bufferedInputStream) throws IOException {
        StringBuilder postParameters = new StringBuilder();
        int symbol = bufferedInputStream.read();
        while (bufferedInputStream.available() != 0) {
            postParameters.append((char) symbol);
            symbol = bufferedInputStream.read();
        }
        postParameters.append((char) symbol);
        return postParameters.toString();
    }

    public static boolean isFileExists(File file, String requestResource) {
        return file.exists() && !requestResource.equals(HTTPUtils.EMPTY_RESOURCE);
    }

    public static Map<String, String> parseParameters(String parametersInString){
        Map<String, String> parameters = new HashMap<>();
        if (!parametersInString.equals(HTTPUtils.EMPTY_PARAMETERS)) {
            String[] parametersInStringArray = parametersInString.split(AMPERSAND);
            for (String s : parametersInStringArray) {
                parameters.put(s.split(EQUALLY_SIGN)[0], s.split(EQUALLY_SIGN)[1]);
            }
        }
        return parameters;
    }


}
