/**
 *
 */
package org.vertx.promises.impl;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.promises.Promise;
import org.vertx.promises.PromiseBus;

/**
 * @author richard
 *
 */
public class DefaultPromiseBus implements PromiseBus {

	private final EventBus eventBus;

	public DefaultPromiseBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * @see org.vertx.promises.PromiseBus#send(java.lang.String, java.lang.String)
	 */
	@Override
	public Promise<Message<String>> send(final String address, final String message) {
		System.out.println("sending " + message);
		final Promise<Message<String>> promise = new DefaultPromise<>();
		eventBus.send(address, message, promise);
		return promise;
	}

}
