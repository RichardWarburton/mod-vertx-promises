package com.insightfullogic.vertx.promises.codegen;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;

import com.insightfullogic.vertx.promises.codegen.ClassInspector;
import com.insightfullogic.vertx.promises.codegen.JavaSourceGenerator;


public class CodeInspectorTest {

	private JavaSourceGenerator generator;
    private ClassInspector inspector;

    @Before
    public void setup() {
    	generator = mock(JavaSourceGenerator.class);
    	inspector = new ClassInspector(EventBus.class, generator);
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
    public void inspectsMethod() throws Exception {
    	Method unregisterHandler = EventBus.class.getMethod("unregisterHandler", String.class, Handler.class);
        inspector.inspectMethod(unregisterHandler);
        ParameterizedType lastType = (ParameterizedType) unregisterHandler.getGenericParameterTypes()[1];
        Type bound = lastType.getActualTypeArguments()[0];
        verify(generator).convertMethod("unregisterHandler", bound, Arrays.<Class<?>>asList(String.class));
    }

    @Test
    public void inspectsClass() throws Exception {
    	inspector.inspect();
    	verify(generator).newClass(EventBus.class);
    }

}
