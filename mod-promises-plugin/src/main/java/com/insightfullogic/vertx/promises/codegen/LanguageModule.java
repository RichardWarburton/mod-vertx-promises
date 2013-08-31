package com.insightfullogic.vertx.promises.codegen;

import java.io.File;
import java.util.Set;

public interface LanguageModule {

    public ClassGenerator makeGenerator(File target, Set<Class<?>> classes);

}
