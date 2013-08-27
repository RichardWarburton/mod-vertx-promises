package com.insightfullogic.promises.codegen;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;

public class CodeInspectorTest {

    private static final ClassInspector inspector = new ClassInspector(EventBus.class, Api.INST.pkg);
    
    @Test
    public void classRenaming() {
        assertEquals("com.insightfullogic.promises.EventBus", inspector.getGeneratedName());
    }

    @Test
    public void requiresConversion() {
        assertTrue(inspector.requiresConversion(asList(Integer.class, Handler.class)));
    }

    @Test
    public void doesNotRequireConversion() {
        assertFalse(inspector.requiresConversion(asList(Integer.class, EventBus.class)));
    }

    @Test
    public void singleHandler() {
        assertFalse(inspector.multiplehandlers(asList(Integer.class, Handler.class)));
    }

    @Test
    public void multipleHandlers() {
        assertTrue(inspector.multiplehandlers(asList(Integer.class, Handler.class, Handler.class)));
    }
    
    @Test
    public void generatesExample() {
        inspector.generate();
    }

}
