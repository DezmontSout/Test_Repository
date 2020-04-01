package utils;

import java.util.Map;

import static utils.CommonConstants.*;

public final class HTTPUtils {
    public static final int FIRST_LINE_INDEX = 0;
    public static final int TWO_LINE_DIVIDE = 2;
    public static final int METHOD_NAME_INDEX = 0;
    public static final int RESOURCE_NAME_INDEX = 1;
    public static final int COOKIE_HEADER_LENGTH = 8;
    public static final int END_OF_LINE_HEADER = 2;
    public static final String GET_REQUEST_PARAMETERS_SEPARATOR = BACK_SLASH + QUESTION_MARK;
    public static final String EMPTY_RESOURCE = "";
    public static final String EMPTY_PARAMETERS = "";
    public static final String REQUEST_HEADERS_END_LINE = "\r\n";
    public static final String HEADERS_KEY_VALUE_SEPARATOR = ": ";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String COOKIE_KEY = "Cookie";
    public static final String SET_COOKIE_HEADER = "Set-Cookie";
    public static final String HTTP_VERSION = "HTTP/1.1 ";
    public static final String DATA_HEADER = "Date";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";


    public static final int NOT_FOUND_STATUS = 404;
    public static final int OK_STATUS = 200;
    private static final String COMMENT_FOR_STATUS_OK = "OK";
    private static final String COMMENT_FOR_STATUS_NOT_FOUND = "NOT FOUND";
    public static final Map<Integer, String> STATUS_TO_COMMENT = Map.of(NOT_FOUND_STATUS, COMMENT_FOR_STATUS_NOT_FOUND, OK_STATUS, COMMENT_FOR_STATUS_OK);
}
