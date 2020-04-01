package server.request;


import com.artem.server.api.HttpRequest;
import com.artem.server.api.HttpSession;
import utils.CommonConstants;
import utils.HTTPUtils;
import utils.ServerUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Request implements HttpRequest {
    private static final int SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_COOKIE_KEY = 0;
    private static final int SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_COOKIE_VALUE = 1;
    private static final int SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_RESOURCE = 0;
    private static final int SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_HEADER_KEY = 0;
    private static final int SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_HEADER_VALUE = 1;
    private static final int START_OF_HEADER_VALUE = 0;
    private static final int START_OF_REQUEST_RESOURCE = 1;
    private String resource;
    private String method;
    private Map<String, String> headers = new HashMap<>();
    private HttpSession session;
    private Map<String, String> parameters;
    private Map<String, String> cookies = new HashMap<>();

    private void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public void setSession(HttpSession session) {
        this.session = session;
    }

    @Override
    public Map<String, String> setCookies(String cookiesLine) {
        Map<String, String> cookies = new HashMap<>();
        String[] cookiesInArrayOfStrings = Arrays.stream(cookiesLine.substring(HTTPUtils.COOKIE_HEADER_LENGTH,
                cookiesLine.length() - HTTPUtils.END_OF_LINE_HEADER).split(CommonConstants.SEMICOLON_SYMBOL)).map(String::trim).toArray(String[]::new);
        for (String s : cookiesInArrayOfStrings) {
            cookies.put(s.split(CommonConstants.EQUAL_SYMBOL)[SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_COOKIE_KEY], s.split(CommonConstants.EQUAL_SYMBOL)[SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_COOKIE_VALUE]);
        }
        return cookies;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getHeaders(String key) {
        return headers.get(key);
    }

    @Override
    public String getCookie(String cookieKey) {
        return cookies.get(cookieKey);
    }

    @Override
    public Map<String, String> getAllCookies() {
        return cookies;
    }

    @Override
    public HttpSession getSession() {
        return session;
    }

    @Override
    public String getParameters(String key) {
        return parameters.get(key);
    }

    public Request(List<String> metadata, String parametersLine, String cookiesLine) {
        String firstLine = metadata.get(HTTPUtils.FIRST_LINE_INDEX);
        String[] dividedFirstLine = firstLine.split(CommonConstants.SPACE);
        setMethod(dividedFirstLine[HTTPUtils.METHOD_NAME_INDEX]);
        setResource(dividedFirstLine[HTTPUtils.RESOURCE_NAME_INDEX].substring(START_OF_REQUEST_RESOURCE)
                .split(HTTPUtils.GET_REQUEST_PARAMETERS_SEPARATOR)[SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_RESOURCE]);
        parameters = ServerUtils.parseParameters(parametersLine);
        metadata.stream().skip(1).forEach((s) -> {
            String[] dividedHeadersLine = s.split(HTTPUtils.HEADERS_KEY_VALUE_SEPARATOR, HTTPUtils.TWO_LINE_DIVIDE);
            headers.put(dividedHeadersLine[SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_HEADER_KEY], dividedHeadersLine[SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_HEADER_VALUE]
                    .substring(START_OF_HEADER_VALUE, dividedHeadersLine[SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_HEADER_VALUE].length() - HTTPUtils.END_OF_LINE_HEADER));
        });
        if (cookiesLine != null) {
            cookies = setCookies(cookiesLine);
        }
    }
}
