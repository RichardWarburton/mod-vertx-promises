package com.insightfullogic.promises.codegen;

import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vertx.java.core.Handler;

public class CodeGenerator {

    private Class<?> klass;
    private String pkg;

    public CodeGenerator(Class<?> klass, String pkg) {
        this.klass = klass;
        this.pkg = pkg;
    }

    public void generate() {
        String generatedName = getGeneratedName();
        for (Method method : klass.getDeclaredMethods()) {
            List<Class<?>> parameterTypes = new ArrayList<>(asList(method.getParameterTypes()));
            if (!requiresConversion(parameterTypes)) {
                // TODO: just delegate
                continue;
            }

            if(multiplehandlers(parameterTypes)) {
                // TODO: add support for this
                continue;
            }

            if (!hasSimpleReturnType(method)) {
                // TODO: add support for this
                continue;
            }
            
            System.out.println(method);
            Class<?> lastType = parameterTypes.get(lastIndex(parameterTypes));
//            TypeVariable<?> typeParameter = lastType.getTypeParameters()[0];
//            System.out.println(Arrays.toString(typeParameter.getBounds()));
//            System.out.println(Arrays.asList(typeParameter));
            System.out.println("Method: " + Arrays.toString(method.getTypeParameters()));
            
            System.out.println(lastType);
            System.out.println(lastType.getGenericSuperclass());
        }
    }

    private boolean hasSimpleReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        return Void.TYPE == returnType || returnType == klass;
    }

    boolean requiresConversion(List<Class<?>> parameterTypes) {
        return parameterTypes.contains(Handler.class);
    }

    boolean multiplehandlers(List<Class<?>> parameterTypes) {
        int lastIndex = lastIndex(parameterTypes);
        return lastIndex != parameterTypes.indexOf(Handler.class);
    }

    private int lastIndex(List<Class<?>> parameterTypes) {
        return parameterTypes.size() - 1;
    }

    String getGeneratedName() {
        return pkg + "." + klass.getSimpleName();
    }

}
