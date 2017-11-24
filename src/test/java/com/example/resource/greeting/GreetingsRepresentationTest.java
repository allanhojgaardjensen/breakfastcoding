package com.example.resource.greeting;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.openapitools.jackson.dataformat.hal.HALLink;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GreetingsRepresentationTest {

    @Test
    public void testGreetingsRepresentation() {
        Map<String, GreetingRepresentation> initialGreetings = new ConcurrentHashMap<>();
        HALLink self = new HALLink.Builder("/greetings/hallo")
                .title("Dansk Hilsen Hallo")
                .seen(Instant.now())
                .name("Danish Greeting Hallo")
                .templated(false)
                .hreflang("da")    
                .type("application/hal+json;concept=greeting")
                .build();
        initialGreetings.put("hallo_da", 
                new GreetingRepresentation("Hallo!", "Dansk", "Danmark", 
                    new GreetingNativeRepresentation("Dansk", "Danmark"), self));
        
        self = new HALLink.Builder("/greetings/hallo")
                .title("Danish Greeting Hallo")
                .seen(Instant.now())
                .name("Danish Greeting Hallo")
                .templated(false)
                .hreflang("en")    
                .type("application/hal+json;concept=greeting")
                .build();
        initialGreetings.put("hallo_en", 
                new GreetingRepresentation("Hallo!", "Dansk", "Danmark", 
                    new GreetingNativeRepresentation("Danish", "Denmark"), self));
        
        self = new HALLink.Builder("/greetings/hello")
                .title("Engelsk Hilsen Hello")
                .seen(Instant.now())
                .name("English Greeting Hello")
                .templated(false)
                .hreflang("da")    
                .type("application/hal+json;concept=greeting")
                .build();
        initialGreetings.put("hello_da", 
                new GreetingRepresentation("Hello!", "English", "England", 
                    new GreetingNativeRepresentation("Engelsk", "England"), self));
        
        self = new HALLink.Builder("/greetings/hello")
                .title("English Greeting Hello")
                .seen(Instant.now())
                .name("English Greeting Hello")
                .templated(false)
                .hreflang("en")    
                .type("application/hal+json;concept=greeting")
                .build();
        initialGreetings.put("hello_en", 
                new GreetingRepresentation("Hello!", "English", "England", 
                    new GreetingNativeRepresentation("English", "England"), self));

        Collection<GreetingRepresentation> greetings = initialGreetings.values();
        
        GreetingsRepresentation gr = new GreetingsRepresentation("List of Greetings", greetings);
        assertNotNull(gr);
        assertEquals("List of Greetings", gr.getInfo());
        assertEquals("/greetings", gr.getSelf().getHref());
        assertEquals(4, gr.getGreetings().size());
        assertEquals(4, gr.getEmbeddedGreetings().size());
        assertTrue(gr.getEmbeddedGreetings().contains(initialGreetings.get("hallo_da")));
        GreetingRepresentation g = gr.getEmbeddedGreetings()
                .stream()
                .peek(h-> System.out.println(h.getGreeting()))
                .filter(h -> h.getGreeting().equals("Hallo!"))
                .findAny()
                .orElse(null);
        assertNotNull(g);
        assertEquals("/greetings/hallo", g.getSelf().getHref());
        assertTrue(g.getSelf().getTitle().contains("Hallo"));
    }
}
