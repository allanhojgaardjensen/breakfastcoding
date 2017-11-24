package com.example.service.patch;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.resource.greeting.GreetingRepresentation;
import com.example.resource.greeting.GreetingNativeRepresentation;
import io.openapitools.jackson.dataformat.hal.HALLink;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JSONPatchContainerTest {

    @Test
    public void testConstruct() {
        JSONPatchContainer jc = new JSONPatchContainer("operation", "path", "value");
        assertEquals("operation", jc.getOperation());
        assertEquals("path", jc.getPath());
        assertEquals("value", jc.getValue());

        ObjectMapper mapper = new ObjectMapper();
        try {
            JSONPatchContainer pjc = mapper.readValue(jc.toString(), JSONPatchContainer.class);
        } catch (IOException e) {
            fail("Should be able to parse itself as json");
        }
    }
       
    @Test
    public void testReplaceValueinGreeting() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        HALLink self = new HALLink.Builder("/greetings/hej").title("Greeting Hej").build();
        GreetingRepresentation gr = new GreetingRepresentation("Hej!", "Dansk", "Danmark", new GreetingNativeRepresentation("Danish", "Denmark"), self);
        JSONPatchContainer jc = new JSONPatchContainer("replace", "language", "volapyk");  
        boolean set = jc.replaceValue(gr);
        assertTrue(set);
        assertEquals(gr.getLanguage(), "volapyk");
        
        gr = new GreetingRepresentation("Hej!", "Dansk", "Danmark", new GreetingNativeRepresentation("Danish", "Denmark"), self);
        jc = new JSONPatchContainer("replace", "/language", "polavyk");  
        set = jc.replaceValue(gr);
        assertTrue(set);
        assertEquals(gr.getLanguage(), "polavyk");  

        jc = new JSONPatchContainer("replace", "_links/self/href", "/links-self-href");  
        set = jc.replaceValue(gr);
        assertTrue(set);
        assertEquals(gr.getSelf().getHref(), "/links-self-href");  

        jc = new JSONPatchContainer("replace", "_links/self", "/notSelf");  
        set = jc.replaceValue(gr);
        assertTrue(!set);
    }
}
