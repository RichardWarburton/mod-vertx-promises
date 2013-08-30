package com.insightfullogic.vertx.promises.codegen;

import java.io.File;

public interface LanguageModule {

    public ClassGenerator makeGenerator(File target);

}
