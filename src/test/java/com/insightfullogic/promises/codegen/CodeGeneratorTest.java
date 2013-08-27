package com.insightfullogic.promises.codegen;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;

public class CodeGeneratorTest {

    private static final ClassInspector generator = new ClassInspector(EventBus.class, Api.INST.pkg);
    
    @Test
    public void classRenaming() {
        assertEquals("com.insightfullogic.promises.EventBus", generator.getGeneratedName());
    }

    @Test
    public void requiresConversion() {
        assertTrue(generator.requiresConversion(asList(Integer.class, Handler.class)));
    }

    @Test
    public void doesNotRequireConversion() {
        assertFalse(generator.requiresConversion(asList(Integer.class, EventBus.class)));
    }

    @Test
    public void singleHandler() {
        assertFalse(generator.multiplehandlers(asList(Integer.class, Handler.class)));
    }

    @Test
    public void multipleHandlers() {
        assertTrue(generator.multiplehandlers(asList(Integer.class, Handler.class, Handler.class)));
    }
    
    @Test
    public void generatesExample() {
        generator.generate();
    }

}
