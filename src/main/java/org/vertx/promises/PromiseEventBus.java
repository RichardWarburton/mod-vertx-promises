/**
 *
 */
package org.vertx.promises;

import org.vertx.java.core.eventbus.Message;

/**
 * @author richard
 *
 */
public interface PromiseEventBus {

	Promise<Message<String>> send(String address, String message);

	// TODO: register handler
}
