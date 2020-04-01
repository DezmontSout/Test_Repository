package server.response;

import com.artem.server.api.HttpResponse;
import utils.HTTPUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Response implements HttpResponse {
    private static final String START_OF_A_GOOD_RESPONSE_STATUS = "2";
    private Map<String, String> headers = new HashMap<>();
    private InputStream responseResource;
    private String commentForResponseStatus;
    private Integer responseStatus;
    private Map<String, String> cookies = new HashMap<>();

    @Override
    public void setStatus(int responseStatus) {
        this.responseStatus = responseStatus;
        commentForResponseStatus = HTTPUtils.STATUS_TO_COMMENT.get(responseStatus);
    }

    @Override
    public void setResource(InputStream responseResource) {
        this.responseResource = responseResource;
    }

    @Override
    public void setHeaders(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public void setCookie(String cookieKey, String cookieValue) {
        cookies.put(cookieKey, cookieValue);
    }

    @Override
    public int getStatus() {
        return responseStatus;
    }

    @Override
    public String getCommentForResponseStatus() {
        return commentForResponseStatus;
    }

    public InputStream getResource() {
        return responseResource;
    }

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
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
    public boolean hasOkStatus() {
        return this.responseStatus.toString().startsWith(START_OF_A_GOOD_RESPONSE_STATUS);
    }
}
