/**
 *
 */
package org.vertx.promises.impl;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.promises.Promise;
import org.vertx.promises.PromiseEventBus;

/**
 * @author richard
 *
 */
public class DefaultPromiseEventBus implements PromiseEventBus {

	private final EventBus eventBus;

	public DefaultPromiseEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * @see org.vertx.promises.PromiseEventBus#send(java.lang.String, java.lang.String)
	 */
	@Override
	public Promise<Message<String>> send(final String address, final String message) {
		final Promise<Message<String>> promise = new DefaultPromise<>();
		eventBus.send(address, message, promise);
		return promise;
	}

	/**
	 * @see org.vertx.promises.PromiseEventBus#registerHandler(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Promise<? extends Message> registerHandler(final String address) {
		final Promise<? extends Message> promise = new DefaultPromise<>();
		eventBus.registerHandler(address, promise);
		return promise;
	}

	/**
	 * @see org.vertx.promises.PromiseEventBus#registerHandler(java.lang.String, org.vertx.java.core.Handler)
	 */
	@Override
	public Promise<? extends Message> registerHandler(final String address, final Handler<AsyncResult<Void>> handler) {
		return null;
	}

}
