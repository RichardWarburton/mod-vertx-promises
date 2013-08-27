package com.insightfullogic.promises.codegen;

import static com.sun.codemodel.JMod.FINAL;

import java.util.List;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class JavaSourceGenerator implements ClassGenerator {
    
    private JCodeModel code;
    private JDefinedClass klass;

    public JavaSourceGenerator() {
        code = new JCodeModel();
    }

    @Override
    public void newClass(String pkg, String name) {
        code._package(pkg);
        try {
            klass = code._class(FINAL, name, ClassType.CLASS);
        } catch (JClassAlreadyExistsException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void newMethod(String name, Class<?> returnType, List<Class<?>> parameters) {
        JMethod method = klass.method(JMod.PUBLIC, returnType, name);
        for (int i = 0; i < parameters.size(); i++) {
            method.param(parameters.get(0), "param" + i);
        }
        
    }

    @Override
    public void generate() {
        // TODO Auto-generated method stub
        
    }

}
