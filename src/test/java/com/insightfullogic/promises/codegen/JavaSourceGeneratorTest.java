/**
 *
 */
package com.insightfullogic.promises.codegen;

import static java.nio.file.Files.readAllLines;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;

/**
 * @author richard
 *
 */
public class JavaSourceGeneratorTest {

	private static final String PKG = "com.insightfullogic.promises.impl";

	@BeforeClass
	public static void regenerate() {
		JavaSourceGenerator generator = new JavaSourceGenerator();
		generator.newClass(PKG, "PromiseEventBus", EventBus.class.getName());
		generator.newMethod("registerHandler", Message.class, Arrays.<Class<?>>asList(String.class));
		generator.generate();
	}

	@Test
	public void generatesFile() {
		File file = getFile();
		assertTrue(file.exists());
		assertTrue(file.isFile());
	}

	@Test
	public void generatesPackage() throws IOException {
		assertFileContains("package com.insightfullogic.promises.impl;");
	}

	@Test
	public void generatesClass() throws IOException {
		assertFileContains("public final class PromiseEventBus");
	}

	@Test
	public void generatesConstructor() throws IOException {
	    assertFileContains("public PromiseEventBus(EventBus _eventbus) {");
		assertFileContains("eventbus = _eventbus;");
	}

	@Test
	public void generatesBinding() throws IOException {
	    assertFileContains("Promise<? extends Message> promise = new DefaultPromise<>();");
		assertFileContains("promise.registerHandler(param0, promise);");
		assertFileContains("return promise;");
	}

	public File getFile() {
		return new File("generated-sources/com/insightfullogic/promises/impl/PromiseEventBus.java");
	}

	public void assertFileContains(String toFind) throws IOException {
		File file = getFile();
		for (String line : readAllLines(file.toPath(), Charset.defaultCharset())) {
			if (line.contains(toFind)) {
				return;
			}
		}
		fail("Unable to find: " + toFind);
	}

}
