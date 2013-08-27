package com.insightfullogic.promises.codegen;

import java.util.List;

public interface ClassGenerator {

    public void newClass(String pkg, String name);

    public void newMethod(String name, Class<?> returnType, List<Class<?>> parameters);
    
    public void generate();

}
