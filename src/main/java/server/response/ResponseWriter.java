package server.response;


import utils.CommonConstants;
import utils.HTTPUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;


public class ResponseWriter {

    public void write(Response response, BufferedOutputStream bufferedOutputStream, int bufferSize) throws IOException {
        bufferedOutputStream.write((HTTPUtils.HTTP_VERSION + response.getStatus() + CommonConstants.SPACE + response.getCommentForResponseStatus()
                + HTTPUtils.REQUEST_HEADERS_END_LINE).
                getBytes(Charset.defaultCharset()));
        writeHeaders(response, bufferedOutputStream);
        try (BufferedInputStream resourceInputStream = new BufferedInputStream(response.getResource(), bufferSize)) {
            int symbol = resourceInputStream.read();
            while (symbol != -1) {
                bufferedOutputStream.write(symbol);
                symbol = resourceInputStream.read();
            }
            bufferedOutputStream.flush();
        }
    }

    private void writeHeaders(Response response, BufferedOutputStream bufferedOutputStream) throws IOException {
        Map<String, String> headers = response.getHeaders();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            bufferedOutputStream.write((entry.getKey() + HTTPUtils.HEADERS_KEY_VALUE_SEPARATOR + entry.getValue() +
                    HTTPUtils.REQUEST_HEADERS_END_LINE).getBytes(Charset.defaultCharset()));
        }
        Map<String, String> cookies = response.getAllCookies();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            bufferedOutputStream.write((HTTPUtils.SET_COOKIE_HEADER + HTTPUtils.HEADERS_KEY_VALUE_SEPARATOR + entry.getKey()
                    + CommonConstants.EQUAL_SYMBOL + entry.getValue() + HTTPUtils.REQUEST_HEADERS_END_LINE).getBytes(Charset.defaultCharset()));
        }
        bufferedOutputStream.write(HTTPUtils.REQUEST_HEADERS_END_LINE.getBytes(Charset.defaultCharset()));
    }

}
