package com.insightfullogic.promises.codegen;

import static com.sun.codemodel.JMod.FINAL;
import static com.sun.codemodel.JMod.PRIVATE;
import static com.sun.codemodel.JMod.PUBLIC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    private static final String GENERATED_SOURCES_DIR = "generated-sources";

	private final JCodeModel code;

    private final JClass promise;
    private final JClass defaultPromise;

    private JDefinedClass klass;

    public JavaSourceGenerator() {
        code = new JCodeModel();
        promise = code.directClass(Promise.class.getName());
        defaultPromise = code.directClass(DefaultPromise.class.getName());
    }

    @Override
    public void newClass(String pkgName, String name, String wrappedClass) {
        JPackage pkg = code._package(pkgName);
        makeClass(name, pkg);
        makeConstructor(wrappedClass);
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
    public void newMethod(String name, Class<?> returnBound, List<Class<?>> parameterTypes) {
    	JClass typeParameter = code.directClass(returnBound.getName()).wildcard();
    	JClass returnType = promise.narrow(typeParameter);

    	// Eg: public Promise<? extends Message> registerHandler(final String address) {
    	JMethod method = klass.method(PUBLIC, returnType, name);
    	List<JVar> parameters = generateParameters(parameterTypes, method);
        JBlock body = method.body();

        // Eg: final Promise<? extends Message> promise = new DefaultPromise<>();
        JClass implType = defaultPromise.narrow(Collections.<JClass>emptyList());
        JVar promiseVar = body.decl(returnType, "promise", JExpr._new(implType));

        generateBindingInvoke(name, parameters, body, promiseVar);

        // Eg: return promise;
        body._return(promiseVar);
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
