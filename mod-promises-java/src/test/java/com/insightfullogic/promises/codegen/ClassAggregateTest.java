/**
 *
 */
package com.insightfullogic.promises.codegen;

import org.junit.Test;
import org.vertx.java.core.eventbus.EventBus;

/**
 * @author richard
 *
 */
public class ClassAggregateTest {

	@Test
	public void run() {
		ClassGenerator generator = new JavaSourceGenerator();
		ClassInspector inspector = new ClassInspector(EventBus.class, generator);
		inspector.inspect();
		generator.generate();
	}

}
