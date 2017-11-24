package com.example.service.patch;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JAX-RS reader interceptor supporting simple PATCH .
 */
public class PatchInterceptor implements ReaderInterceptor {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final Logger LOGGER = Logger.getLogger(PatchInterceptor.class.getName());

    @SuppressWarnings("unchecked")
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext readerInterceptorContext) throws IOException {
        if (!"application/patch+json".equals(readerInterceptorContext.getMediaType().toString())) {
            return readerInterceptorContext.proceed();
        }
        String body = "{\"error\":\"input could not be parsed\"}";
        try {
            body = getString(readerInterceptorContext.getInputStream());
            String input = convertInput(body);
            readerInterceptorContext.setInputStream(new ByteArrayInputStream(input.getBytes(CHARSET)));
        } catch (JsonMappingException ex) {
            LOGGER.log(Level.WARNING, "Unable to parse input from stream into a PATCH Object", ex);
            readerInterceptorContext.setInputStream(new ByteArrayInputStream(body.getBytes(CHARSET)));
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Unable to read input from stream", ex);
        }
        return readerInterceptorContext.proceed();
    }

    String convertInput(String body) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JSONPatchContainer[] json = mapper.readValue(body, JSONPatchContainer[].class);
            return mapper.writeValueAsString(Arrays.toString(json));
        } catch (JsonProcessingException jpe) {
            JSONPatchContainer json = mapper.readValue(body, JSONPatchContainer.class);
            return mapper.writeValueAsString(json);
        }
    }

    String getString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input, CHARSET))) { 
            return buffer.lines().collect(Collectors.joining());
        } 
    }
}
