package com.example.service.patch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ReaderInterceptorContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class PatchInterceptorTest {

    @Test
    public void testGetStringJSON() throws IOException {
        JSONPatchContainer jc = new JSONPatchContainer("replace", "language", "Tysk");
        PatchInterceptor pi = new PatchInterceptor();
        String input = jc.toString();
        InputStream is = new ByteArrayInputStream(input.getBytes());
        assertEquals(input, pi.getString(is));
    }

    @Test
    public void testGetStringJsonString() throws IOException {
        PatchInterceptor pi = new PatchInterceptor();
        String input = "{\"op\":\"replace\",\"path\":\"/language\",\"value\":\"German\"}";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        assertEquals(input, pi.getString(is));
    }

    @Test
    public void testGetStringJsonPrettyString() throws IOException {
        PatchInterceptor pi = new PatchInterceptor();
        String input = "{\n\"op\":\"replace\",\n\"path\":\"/language\"\n,\"value\":\"German\"\n}";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        String expected = "{\"op\":\"replace\",\"path\":\"/language\",\"value\":\"German\"}";
        assertEquals(expected, pi.getString(is));
    }

    @Test
    public void testGetStringEmptyString() throws IOException {
        PatchInterceptor pi = new PatchInterceptor();
        String input = "";
        InputStream is = new ByteArrayInputStream(input.getBytes());
        assertEquals(input, pi.getString(is));
    }

    @Test
    public void testGetStringEmptyStringClosedStream() throws IOException {
        PatchInterceptor pi = new PatchInterceptor();
        InputStream is = null;
        try {
            pi.getString(is);
        } catch (NullPointerException npe) {
            //expected
        }
    }

    @Test
    public void testConvertJSONArrayInput() throws IOException {
        PatchInterceptor pi = new PatchInterceptor();
        String input = "[{\"op\":\"replace\",\"path\":\"/language\",\"value\":\"German\"}]";
        String result = pi.convertInput(input);
        assertTrue(result.contains("replace"));
        assertTrue(result.contains("language"));
        assertTrue(result.contains("German"));
    }

    @Test
    public void testConvertJSONObjectInput() throws IOException {
        PatchInterceptor pi = new PatchInterceptor();
        String input = "{\"op\":\"replace\",\"path\":\"/language\",\"value\":\"German\"}";
        String result = pi.convertInput(input);
        assertTrue(result.contains("replace"));
        assertTrue(result.contains("language"));
        assertTrue(result.contains("German"));
    }

    @Test
    public void testConvertNonJSONInput() throws IOException {
        PatchInterceptor pi = new PatchInterceptor();
        String input = "\"op\":\"replace\",\"path\":\"/language\",\"value\":\"German\"";
        try {
            String result = pi.convertInput(input);
            fail("should not pass non json");
        } catch (JsonProcessingException jpe) {
            //expected ecxeption
        }
    }   
    
    @Test
    public void testGetAndConvertString() throws IOException {
        JSONPatchContainer jc = new JSONPatchContainer("replace", "language", "Tysk");
        PatchInterceptor pi = new PatchInterceptor();
        String input = jc.toString();
        InputStream is = new ByteArrayInputStream(input.getBytes());
        assertEquals(input, pi.getString(is));
        input = "[" + input +"]";
        String result = pi.convertInput(input);
        assertTrue(result.contains("replace"));
        assertTrue(result.contains("language"));
        assertTrue(result.contains("Tysk"));
    }
    
    @Test
    public void testAroundReadFromNotPatchContent() throws IOException {
        PatchInterceptor pi = new PatchInterceptor();
        ReaderInterceptorContext readerInterceptorContext = mock(ReaderInterceptorContext.class);
        when(readerInterceptorContext.getMediaType()).thenReturn(new MediaType("application", "json"));
        pi.aroundReadFrom(readerInterceptorContext);
        verify(readerInterceptorContext,times(1)).proceed();
    }

    @Test
    public void testAroundReadFromInvalidPatchContent() throws IOException {
        PatchInterceptor pi = new PatchInterceptor();
        ReaderInterceptorContext readerInterceptorContext = mock(ReaderInterceptorContext.class);
        when(readerInterceptorContext.getMediaType()).thenReturn(new MediaType("application", "patch+json"));
        when(readerInterceptorContext.getInputStream()).thenReturn(new ByteArrayInputStream("not parsable".getBytes("UTF8")));
        pi.aroundReadFrom(readerInterceptorContext);
        verify(readerInterceptorContext,times(1)).proceed();
       }
 
    @Test
    public void testAroundReadFromInvalidPatchInputContent() throws IOException {
        PatchInterceptor pi = new PatchInterceptor();
        ReaderInterceptorContext readerInterceptorContext = mock(ReaderInterceptorContext.class);
        when(readerInterceptorContext.getMediaType()).thenReturn(new MediaType("application", "patch+json"));
        when(readerInterceptorContext.getInputStream()).thenReturn(new ByteArrayInputStream("".getBytes("UTF8")));
        pi.aroundReadFrom(readerInterceptorContext);
        verify(readerInterceptorContext,times(1)).proceed();
       }

    @Test
    public void testAroundReadFromValidPatchContent() throws IOException {
        PatchInterceptor pi = new PatchInterceptor();
        ReaderInterceptorContext readerInterceptorContext = mock(ReaderInterceptorContext.class);
        when(readerInterceptorContext.getMediaType()).thenReturn(new MediaType("application", "patch+json"));
        when(readerInterceptorContext.getInputStream())
                .thenReturn(new ByteArrayInputStream("{\"op\":\"replace\",\"path\":\"/language\",\"value\":\"German\"}".getBytes("UTF8")));
        pi.aroundReadFrom(readerInterceptorContext);
        verify(readerInterceptorContext,times(1)).proceed();
       }

    

}
