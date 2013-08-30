package com.insightfullogic.vertx.promises.codegen;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public interface ClassGenerator {

	public void newClass(Class<?> wrappedClass);

    public void convertMethod(String name, Type returnBound, List<Class<?>> parameters);

    public void wrapMethod(Method method);

    public void generate();

}
