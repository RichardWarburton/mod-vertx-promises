package com.insightfullogic.vertx.promises.codegen;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.vertx.java.core.eventbus.EventBus;

public class ParameterNamesTest {

    @Test
    public void loadsNames() {
        ParameterNames parameters = new ParameterNames("java.json");
        List<String> names = parameters.getNames(EventBus.class.getName(), "send");
        assertEquals(asList("address", "message"), names);
    }

}
