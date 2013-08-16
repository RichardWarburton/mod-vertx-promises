/**
 *
 */
package org.vertx.promises;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

/**
 * @author richard
 *
 */
public interface PromiseEventBus {

	Promise<Message<String>> send(String address, String message);

	@SuppressWarnings("rawtypes")
	Promise<? extends Message> registerHandler(String address);

	@SuppressWarnings("rawtypes")
	Promise<? extends Message> registerHandler(final String address, Handler<AsyncResult<Void>> handler);

}
