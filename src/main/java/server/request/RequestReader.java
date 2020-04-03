package server.request;

import utils.CommonConstants;
import utils.HTTPUtils;
import utils.ServerUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestReader {

    private static final int SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_RESOURCE_IN_FIRST_LINE = 1;
    private static final int START_OF_PARAMS = 1;

    public Request readRequest(BufferedInputStream bufferedInputStream) throws IOException {
        List<String> metadata = new ArrayList<>();
        String firstLine = ServerUtils.readLine(bufferedInputStream);
        String headersLine = ServerUtils.readLine(bufferedInputStream);
        String resourcesLine = firstLine.split(CommonConstants.SPACE)[SUBSTRING_NUMBER_IN_A_SPLIT_STRING_FOR_RESOURCE_IN_FIRST_LINE];
        String cookiesLine = null;
        metadata.add(firstLine);
        while (!headersLine.equals(HTTPUtils.REQUEST_HEADERS_END_LINE)) {
            if (!headersLine.startsWith(HTTPUtils.COOKIE_KEY)) {
                metadata.add(headersLine);
            }
            else {
                cookiesLine = headersLine;
            }
            headersLine = ServerUtils.readLine(bufferedInputStream);
        }
        String params = (firstLine.startsWith(HTTPUtils.POST) ? ServerUtils.readParametersFromPost(bufferedInputStream)
                : Optional.of(resourcesLine).map(url -> url.contains(CommonConstants.QUESTION_MARK)).filter(hasParams -> hasParams)
                .map(hasParams -> resourcesLine.substring(resourcesLine.indexOf(CommonConstants.QUESTION_MARK) + START_OF_PARAMS))
                .orElse(HTTPUtils.EMPTY_PARAMETERS));
        return new Request(metadata, params, cookiesLine);
    }
}
