/**
 *
 */
package org.vertx.promises;

import org.vertx.java.core.Vertx;
import org.vertx.promises.impl.DefaultPromiseEventBus;

/**
 * @author richard
 *
 */
public class PromiseVertx {

	private final Vertx vertx;

	public PromiseVertx(final Vertx vertx) {
		this.vertx = vertx;
	}

	public PromiseEventBus promiseBus() {
		return new DefaultPromiseEventBus(vertx.eventBus());
	}

}
