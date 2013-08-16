/**
 *
 */
package org.vertx.promises.unit;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.platform.Verticle;

/**
 * @author richard
 *
 */
public class SenderVerticle extends Verticle {

	/**
	 * @see org.vertx.java.platform.Verticle#start()
	 */
	@Override
	public void start() {
		final EventBus eb = vertx.eventBus();
		eb.send("testVerticle", "abc");
	}

}
