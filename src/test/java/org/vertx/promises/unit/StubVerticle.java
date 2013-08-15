/**
 * 
 */
package org.vertx.promises.unit;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;

/**
 * @author richard
 * 
 */
public class StubVerticle extends Verticle {

	/**
	 * @see org.vertx.java.platform.Verticle#start()
	 */
	@Override
	public void start() {
		final EventBus eb = vertx.eventBus();

		eb.registerHandler("a", new Handler<Message<String>>() {
			@Override
			public void handle(final Message<String> msg) {
				msg.reply("hello");
			}
		});

		eb.registerHandler("b", new Handler<Message<String>>() {
			@Override
			public void handle(final Message<String> msg) {
				msg.reply("world");
			}
		});

		eb.registerHandler("c", new Handler<Message<String>>() {
			@Override
			public void handle(final Message<String> msg) {
				msg.reply(msg.body());
			}
		});

	}
}
