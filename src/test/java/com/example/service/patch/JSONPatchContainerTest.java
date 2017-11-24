package com.example.service.patch;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import com.example.resource.greeting.GreetingRepresentation;

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
            fail("Should be able to parse tself as json");
        }
    }
       
    @Test
    public void testReplaceValueinGreeting() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        GreetingRepresentation gr = new GreetingRepresentation("Hej!", "Dansk", "Danmark", "Danish", "Denmark", "/greetings/hej", "Greeting Hej");
        JSONPatchContainer jc = new JSONPatchContainer("replace", "language", "volapyk");  
        boolean set = jc.replaceValue(gr);
        assertTrue(set);
        assertEquals(gr.getLanguage(), "volapyk");
        
        gr = new GreetingRepresentation("Hej!", "Dansk", "Danmark", "Danish", "Denmark", "/greetings/hej", "Greeting Hej");
        jc = new JSONPatchContainer("replace", "/language", "polavyk");  
        set = jc.replaceValue(gr);
        assertTrue(set);
        assertEquals(gr.getLanguage(), "polavyk");  

        jc = new JSONPatchContainer("replace", "links/self/href", "/links-self-href");  
        set = jc.replaceValue(gr);
        assertTrue(set);
        assertEquals(gr.getLinks().getSelf().getHref(), "/links-self-href");  

        jc = new JSONPatchContainer("replace", "links/self", "/notSelf");  
        set = jc.replaceValue(gr);
        assertTrue(!set);
    }
}
