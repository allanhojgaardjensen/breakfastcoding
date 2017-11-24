package com.example.resource.greeting;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.openapitools.jackson.dataformat.hal.HALLink;
import io.openapitools.jackson.dataformat.hal.HALMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class GreetingRepresentationTest {

    @Test
    public void testCreateRepresentation() throws JsonProcessingException {
        ObjectMapper mapper = new HALMapper();
        GreetingNativeRepresentation gnr = new GreetingNativeRepresentation("English", "England");
        HALLink self = new HALLink.Builder("/greetings/hello")
                .title("Hello")
                .build();
        GreetingRepresentation greeting = new GreetingRepresentation("Hello", "English", "England", gnr, self);
        String halString = greeting.toHAL().trim();
        String objMapped = mapper.writeValueAsString(greeting).trim();
        assertTrue(objMapped.contains("\"greeting\":\"Hello\","));
        assertTrue(objMapped.contains("\"language\":\"English\","));
         assertTrue(objMapped.contains("\"country\":\"England\","));
        assertTrue(objMapped.contains("\"native\":{\"language\":\"English\",\"country\":\"England\"}"));
        assertTrue(objMapped.contains("\"_links\":{\"self\":{\"href\":"));
        assertTrue(objMapped.contains("greetings/hello\","));
        assertTrue(objMapped.contains("\"title\":\"Hello\""));
        assertTrue(objMapped.contains("/greetings/hello"));
    }

    @Test
    public void testCreateJSONRepresentation() throws IOException {
        ObjectMapper mapper = new HALMapper();
        GreetingNativeRepresentation gnr = new GreetingNativeRepresentation("English", "England");
        HALLink self = new HALLink.Builder("/greetings/hello")
                .title("Hello")
                .build();
        GreetingRepresentation greetingObj = new GreetingRepresentation("Hello", "English", "England", gnr, self);
        String json = mapper.writeValueAsString(greetingObj);
        GreetingRepresentation greeting = mapper.readValue(json, GreetingRepresentation.class);
        assertEquals(greetingObj, greeting);
    }

    @Test
    public void testEquals() throws IOException {
        GreetingNativeRepresentation nativ = new GreetingNativeRepresentation("English", "England");
        HALLink self = new HALLink.Builder("/greetings/hello")
                .title("Hello")
                .build();
        GreetingRepresentation original = new GreetingRepresentation("Hello", "English", "England", nativ, self);
 
        GreetingNativeRepresentation otherNative = new GreetingNativeRepresentation("English", "England");
        assertEquals(nativ, otherNative);
        assertEquals(otherNative, nativ);

        GreetingNativeRepresentation yaanotherNative = new GreetingNativeRepresentation("English", "England");
        assertEquals(nativ, yaanotherNative);
        yaanotherNative = new GreetingNativeRepresentation("English", "Englond");
        assertFalse(nativ.equals(yaanotherNative));
        yaanotherNative = new GreetingNativeRepresentation("Englosh", "Englond");
        assertFalse(nativ.equals(yaanotherNative));
        assertFalse(nativ.equals(null));
        assertEquals(nativ, nativ);
        assertFalse(nativ.equals("\"language\";\"English\",\"country\":\"England\""));
        
        HALLink otherSelf = new HALLink.Builder("/greetings/hello")
                .title("Hello")
                .build();
        GreetingRepresentation other = new GreetingRepresentation("Hello", "English", "England", otherNative, otherSelf);
        assertEquals(original.hashCode(), other.hashCode());
        assertEquals(original, other);

        assertFalse(original.equals(null));
        assertFalse(original.equals(nativ));
        assertFalse(otherNative.equals(other));
        
        HALLink anotherSelf = new HALLink.Builder("/greetings/hollo")
                .title("Hollo")
                .build();
        GreetingRepresentation another = new GreetingRepresentation("Hollo", "English", "England", otherNative, anotherSelf);
        assertFalse(other.equals(another));
        GreetingRepresentation yaaanother = new GreetingRepresentation("Hello", "English", "England", otherNative, anotherSelf);
        assertFalse(other.equals(yaaanother));
        
        GreetingNativeRepresentation anotherNative = new GreetingNativeRepresentation("Englishs", "England");
        another = new GreetingRepresentation("Hollo", "English", "England", anotherNative, otherSelf);
        assertFalse(other.equals(another));

        GreetingRepresentation yaaaanother = new GreetingRepresentation("Hello", "English", "England", anotherNative, anotherSelf);
        assertFalse(other.equals(yaaaanother));

        GreetingRepresentation yaaaaanother = new GreetingRepresentation("Hello", "English", "Englond", anotherNative, anotherSelf);
        assertFalse(other.equals(yaaaaanother));

        GreetingRepresentation yaaaaaanother = new GreetingRepresentation("Hello", "Englosh", "Englond", anotherNative, anotherSelf);
        assertFalse(other.equals(yaaaaanother));
    }

}
