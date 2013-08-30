package com.insightfullogic.promises.codegen;

import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.insightfullogic.promises.Promise;
import com.insightfullogic.promises.PromiseException;
import com.insightfullogic.promises.impl.DefaultPromise;
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

	public static final String DEFAULT_PKG = "com.insightfullogic.promises.impl";
	public static final String DEFAULT_PREFIX = "Promise";

    public static final String GENERATED_SOURCES_DIR = "target/generated-sources";

	final JCodeModel code;

    private final JClass promise;
    private final JClass defaultPromise;

    private JDefinedClass klass;

	private String pkgName;

	private String classPrefix;

	public JavaSourceGenerator() {
		this(DEFAULT_PKG, DEFAULT_PREFIX);
	}

	// TODO: common package prefix
    public JavaSourceGenerator(String pkgName, String classPrefix) {
        this.pkgName = pkgName;
		this.classPrefix = classPrefix;
		code = new JCodeModel();
        promise = code.directClass(Promise.class.getName());
        defaultPromise = code.directClass(DefaultPromise.class.getName());
    }

    @Override
    public void newClass(Class<?> wrappedClass) {
        JPackage pkg = code._package(pkgName);
        makeClass(classPrefix+wrappedClass.getSimpleName(), pkg);
        makeConstructor(wrappedClass.getName());
    }

	private void makeConstructor(String wrappedClassName) {
		JMethod constructor = klass.constructor(PUBLIC);
		JClass wrappedClass = code.directClass(wrappedClassName);

		String paramName = wrappedClass.name().toLowerCase();
		JFieldVar wrappedField = klass.field(PRIVATE | FINAL, wrappedClass, paramName);
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
        JClass implType = defaultPromise.narrow(Collections.<JClass>emptyList());
        JVar promiseVar = body.decl(returnType, "promise", JExpr._new(implType));

        generateBindingInvoke(name, parameters, body, promiseVar);

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
		return code.directClass(cls.getName());
	}

	/**
	 * @param name
	 * @param parameters
	 * @param body
	 * @param promiseVar
	 */
	public void generateBindingInvoke(String name, List<JVar> parameters, JBlock body, JVar promiseVar) {
		// Eg: eventBus.registerHandler(address, promise);
        JInvocation invoke = body.invoke(promiseVar, name);
        for (JVar parameter : parameters) {
        	invoke.arg(parameter);
        }
        invoke.arg(promiseVar);
	}

	public List<JVar> generateParameters(List<Class<?>> parameters, JMethod method) {
		List<JVar> parameterRefs = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
        	parameterRefs.add(method.param(parameters.get(0), paramName(i)));
        }
        return parameterRefs;
	}

	public String paramName(int i) {
		return "param" + i;
	}

    @Override
    public void generate() {
    	try {
			File dir = new File(GENERATED_SOURCES_DIR);

			deleteRecursive(dir);
			dir.mkdir();

			code.build(dir);
		} catch (IOException e) {
			throw new PromiseException(e);
		}
    }

    private static void deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()) {
			throw new FileNotFoundException(path.getAbsolutePath());
		}
        if (path.isDirectory()){
        	for (File file : path.listFiles()){
        		deleteRecursive(file);
        	}
        }
        path.delete();
    }


}
