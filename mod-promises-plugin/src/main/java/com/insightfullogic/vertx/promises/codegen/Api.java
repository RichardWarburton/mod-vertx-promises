package com.insightfullogic.vertx.promises.codegen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.platform.Container;
import org.vertx.java.platform.Verticle;

public enum Api {

    INST;

    final String pkg = "com.insightfullogic.promises";

    public final Set<Class<?>> classes = new HashSet<>(Arrays.asList(
                                            EventBus.class,
                                            Container.class,
                                            Verticle.class));

}
