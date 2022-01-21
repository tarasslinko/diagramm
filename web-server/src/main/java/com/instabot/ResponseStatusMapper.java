package com.instabot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ResponseStatusMapper implements ResponseExceptionMapper<Exception> {

    @Override
    public Exception toThrowable(Response response) {
        String msg = getBody(response);
        String message = "[" + response.getStatus() + "] " + msg;
        log.warn("some error during request, message: {}", message);
        return new Exception(message);
    }

    private String getBody(Response response) {
        try {
            ByteArrayInputStream is = (ByteArrayInputStream) response.getEntity();
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Cannot parse body: " + e.getMessage());
            return e.getMessage();
        }
    }
}
