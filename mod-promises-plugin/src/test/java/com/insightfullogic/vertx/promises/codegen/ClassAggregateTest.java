/**
 *
 */
package com.insightfullogic.vertx.promises.codegen;

import org.junit.Test;
import org.vertx.java.core.eventbus.EventBus;

import com.insightfullogic.vertx.promises.codegen.ClassGenerator;
import com.insightfullogic.vertx.promises.codegen.ClassInspector;
import com.insightfullogic.vertx.promises.codegen.JavaSourceGenerator;

public class ClassAggregateTest {

	@Test
	public void run() {
		ClassGenerator generator = new JavaSourceGenerator();
		ClassInspector inspector = new ClassInspector(EventBus.class, generator);
		inspector.inspect();
		generator.generate();
	}

}
