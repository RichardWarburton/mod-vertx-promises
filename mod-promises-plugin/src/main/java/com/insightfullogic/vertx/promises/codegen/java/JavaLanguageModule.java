package com.insightfullogic.vertx.promises.codegen.java;

import java.io.File;
import java.util.Set;

import com.insightfullogic.vertx.promises.codegen.ClassGenerator;
import com.insightfullogic.vertx.promises.codegen.LanguageModule;

public class JavaLanguageModule implements LanguageModule {

    @Override
    public ClassGenerator makeGenerator(File target, Set<Class<?>> wrappedClasses) {
        return new JavaSourceGenerator(target, wrappedClasses);
    }

}
