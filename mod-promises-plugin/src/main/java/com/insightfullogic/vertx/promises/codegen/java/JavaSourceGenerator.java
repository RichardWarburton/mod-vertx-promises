package com.insightfullogic.vertx.promises.codegen.java;

import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.insightfullogic.vertx.promises.codegen.ClassGenerator;
import com.insightfullogic.vertx.promises.codegen.CodegenException;
import com.insightfullogic.vertx.promises.codegen.ParameterNames;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;

public class JavaSourceGenerator implements ClassGenerator {

    public static final String DEFAULT_PKG = "com.insightfullogic.vertx.promises";
    public static final String DEFAULT_PREFIX = "Promise";
    public static final String GENERATED_SOURCES = "generated-sources";

    private static final String PROMISE = ".Promise";
    private static final String DEFAULT_PROMISE = ".DefaultPromise";

    final JCodeModel code;

    private final JClass promise;
    private final JClass defaultPromise;
    private final String pkgName;
    private final String classPrefix;
    private final File target;
    private final Map<String, JClass> classCache;
    private final ParameterNames parameterNames;
    private final Set<Class<?>> wrappedClasses;

    private JDefinedClass klass;
    private JFieldVar wrappedField;

    public JavaSourceGenerator() {
        this(new File("target/"), Collections.<Class<?>>emptySet());
    }
    
    public JavaSourceGenerator(File target, Set<Class<?>> wrappedClasses) {
        this(target, wrappedClasses, DEFAULT_PKG, DEFAULT_PREFIX);
    }

    // TODO: common package prefix
    public JavaSourceGenerator(File target, Set<Class<?>> wrappedClasses, String pkgName, String classPrefix) {
        this.wrappedClasses = wrappedClasses;
        classCache = new HashMap<String, JClass>();
        this.target = target;
        this.pkgName = pkgName;
        this.classPrefix = classPrefix;
        code = new JCodeModel();
        promise = directClass(pkgName + PROMISE);
        defaultPromise = directClass(pkgName + DEFAULT_PROMISE);
        parameterNames = new ParameterNames("java.json");
    }

    @Override
    public void newClass(Class<?> wrappedClass) {
        JPackage pkg = code._package(pkgName);
        makeClass(classPrefix + wrappedClass.getSimpleName(), pkg);
        makeConstructor(wrappedClass.getName());
    }

    private void makeConstructor(String wrappedClassName) {
        JMethod constructor = klass.constructor(PUBLIC);
        JClass wrappedClass = directClass(wrappedClassName);

        String paramName = wrappedClass.name().toLowerCase();
        wrappedField = klass.field(PRIVATE | FINAL, wrappedClass, paramName);
        JVar wrappedParam = constructor.param(wrappedClass, "_" + paramName);

        constructor.body().assign(wrappedField, wrappedParam);
    }

    public void makeClass(String name, JPackage pkg) {
        try {
            klass = pkg._class(PUBLIC | FINAL, name, ClassType.CLASS);
        } catch (JClassAlreadyExistsException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void wrapMethod(Method wrappedMethod) {
        String name = wrappedMethod.getName();
        Class<?> returnType = wrappedMethod.getReturnType();
        boolean needsRemapping = wrappedClasses.contains(returnType) && name.startsWith("get");
        JClass returnClass = needsRemapping ? remappedClass(returnType)
                                            : directClass(returnType.getName());
        JMethod method = klass.method(PUBLIC, returnClass, name);
        List<JVar> parameters = generateParameters(asList(wrappedMethod.getParameterTypes()), method);
        JBlock body = method.body();
        JInvocation invoke = JExpr.invoke(wrappedField, method);
        invokeParameters(parameters, invoke);
        if (needsRemapping) {
            invoke = JExpr._new(returnClass)
                          .arg(invoke);
        }
        
        body._return(invoke);
    }
    
    private JClass remappedClass(Class<?> type) {
        return directClass(pkgName + "." + classPrefix + type.getSimpleName());
    }
    
    // Caching means we get automated imports
    private JClass directClass(String name) {
        JClass jClass = classCache.get(name);
        if (jClass == null) {
            jClass = code.directClass(name);
            classCache.put(name, jClass);
        }
        return jClass;
    }

    @Override
    public void convertMethod(String name, Type returnBound, List<Class<?>> parameterTypes) {
        List<TypeVariable<?>> bindings = new ArrayList<>();

        JClass typeParameter = convertType(returnBound, bindings);
        JClass returnType = promise.narrow(typeParameter);

        // Eg: public Promise<? extends Message> registerHandler(final String address) {
        JMethod method = klass.method(PUBLIC, returnType, name);
        rebindGenerics(typeParameter, method, bindings);

        List<JVar> parameters = generateParameters(parameterTypes, method);
        JBlock body = method.body();

        // Eg: final Promise<? extends Message> promise = new DefaultPromise<>();
        JClass implType = defaultPromise.narrow(Collections.<JClass> emptyList());
        JVar promiseVar = body.decl(returnType, "promise", JExpr._new(implType));

        generateBindingInvoke(name, parameters, body, wrappedField, promiseVar);

        // Eg: return promise;
        body._return(promiseVar);
    }

    JClass convertType(Type returnBound, List<TypeVariable<?>> bindings) {
        if (returnBound instanceof Class) {
            return makeClass(returnBound);
        }

        if (returnBound instanceof WildcardType) {
            WildcardType wildcard = (WildcardType) returnBound;
            return makeClass(wildcard.getUpperBounds()[0]).wildcard();
        }

        if (returnBound instanceof TypeVariable<?>) {
            bindings.add((TypeVariable<?>) returnBound);
            // gets rebound once the method is created
            return code.NULL;
        }

        if (returnBound instanceof ParameterizedType) {
            ParameterizedType parameterized = (ParameterizedType) returnBound;
            JClass rawType = makeClass(parameterized.getRawType());

            List<JClass> typeArguments = new ArrayList<>();
            for (Type type : parameterized.getActualTypeArguments()) {
                typeArguments.add(convertType(type, bindings));
            }
            return rawType.narrow(typeArguments);
        }

        throw new IllegalArgumentException("Don't know what to do with: " + returnBound);
    }

    void rebindGenerics(JClass typeParameter, JMethod method, List<TypeVariable<?>> bindings) {
        Iterator<TypeVariable<?>> it = bindings.iterator();
        List<JClass> typeParameters = typeParameter.getTypeParameters();
        for (int i = 0; i < typeParameters.size(); i++) {
            JClass typeParam = typeParameters.get(i);
            if (typeParam == code.NULL) {
                String name = it.next().getName();
                typeParameters.set(i, method.generify(name));
            }
        }
    }

    private JClass makeClass(Type type) {
        Class<?> cls = (Class<?>) type;
        return directClass(cls.getName());
    }

    public JInvocation generateBindingInvoke(String name, List<JVar> parameters, JBlock body, JVar var, JVar finalArg) {
        // Eg: eventBus.registerHandler(address, promise);
        JInvocation invoke = body.invoke(var, name);
        invokeParameters(parameters, invoke);
        invoke.arg(finalArg);
        return invoke;
    }

    private void invokeParameters(List<JVar> parameters, JInvocation invoke) {
        for (JVar parameter : parameters) {
            invoke.arg(parameter);
        }
    }

    public List<JVar> generateParameters(List<Class<?>> parameters, JMethod method) {
        List<JVar> parameterRefs = new ArrayList<>();
        List<String> names = parameterNames.getNames(wrappedField.type().fullName(), method.name());
        for (int i = 0; i < parameters.size(); i++) {
            parameterRefs.add(method.param(parameters.get(i), paramName(names, i)));
        }
        return parameterRefs;
    }

    public String paramName(List<String> names, int i) {
        if (names == null)
            return "param" + i;
        
        return names.get(i);
    }

    @Override
    public void generate() {
        try {
            File generatedSourcesDir = new File(target, GENERATED_SOURCES);

            deleteRecursive(generatedSourcesDir);
            generatedSourcesDir.mkdir();

            code.build(generatedSourcesDir);
        } catch (IOException e) {
            throw new CodegenException(e);
        }
    }

    private static void deleteRecursive(File path) {
        if (!path.exists()) {
            return;
        }
        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                deleteRecursive(file);
            }
        }
        path.delete();
    }


}
