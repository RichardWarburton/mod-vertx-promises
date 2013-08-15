/**
 *
 */
package org.vertx.promises;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.platform.Container;
import org.vertx.promises.impl.DefaultPromise;

/**
 * @author richard
 *
 */
public class PromiseContainer {

	private final Container container;

	public PromiseContainer(final Container container) {
		this.container = container;
	}

	public Promise<AsyncResult<String>> deployVerticle(final String main) {
		final Promise<AsyncResult<String>> donePromise = new DefaultPromise<AsyncResult<String>>();
		container.deployVerticle(main, donePromise);
		return donePromise;
	}

}
