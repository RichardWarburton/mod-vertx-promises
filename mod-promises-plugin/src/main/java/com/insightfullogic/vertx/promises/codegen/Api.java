package com.insightfullogic.vertx.promises.codegen;

import java.util.Arrays;
import java.util.List;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.platform.Container;

public enum Api {

    INST;

    final String pkg = "com.insightfullogic.promises";

    final List<Class<?>> classes = Arrays.asList(EventBus.class,
                                                 Container.class);

}
