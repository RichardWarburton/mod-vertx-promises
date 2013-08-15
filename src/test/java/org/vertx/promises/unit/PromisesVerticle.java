/**
 *
 */
package org.vertx.promises.unit;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.promises.Combiner;
import org.vertx.promises.Function;
import org.vertx.promises.Promise;
import org.vertx.promises.PromiseBus;
import org.vertx.promises.PromiseContainer;
import org.vertx.promises.PromiseVertx;
import org.vertx.testtools.TestVerticle;

/**
 * @author richard
 *
 */
public class PromisesVerticle extends TestVerticle {

	private static final String Q = "whatsUp";

	// Collects messages from 'a' and 'b', send them to 'c' and check response
	@Test
	public void notNestingOfDoom() {
		final PromiseVertx vertx = new PromiseVertx(this.vertx);
		final PromiseBus bus = vertx.promiseBus();
		final PromiseContainer container = new PromiseContainer(this.container);
		container
			.deployVerticle(StubVerticle.class.getName())
			.bind(new Function<AsyncResult<String>, Promise<Message<String>>>() {
				@Override
				public Promise<Message<String>> handle(final AsyncResult<String> from) {
					assertTrue(from.succeeded());
					System.out.println("STGART");
					return bus.send("a", Q);
				}
			}).compose(bus.send("b", Q), new Combiner<Message<String>, Message<String>, Message<String>>() {
				@Override
				public Promise<Message<String>> combine(final Message<String> left, final Message<String> right) {
					System.out.println("STGART2");
					return bus.send("c", left.body() + " " + right.body());
				}
		    }).then(new Handler<Message<String>>() {
				@Override
				public void handle(final Message<String> cReply) {
					System.out.println("STGART3");
					assertEquals("hello world", cReply.body());
					testComplete();
				}
		    });
	}

	@Test
	public void sigh() {
		final PromiseVertx vertx = new PromiseVertx(this.vertx);
		final PromiseBus bus = vertx.promiseBus();
		container.deployVerticle(StubVerticle.class.getName(), new Handler<AsyncResult<String>>() {
			@Override
			public void handle(final AsyncResult<String> event) {
				bus.send("a", Q).compose(bus.send("b", Q), new Combiner<Message<String>, Message<String>, Message<String>>() {
					@Override
					public Promise<Message<String>> combine(final Message<String> left, final Message<String> right) {
						System.out.println("STGART2");
						return bus.send("c", left.body() + " " + right.body());
					}
			    }).then(new Handler<Message<String>>() {
					@Override
					public void handle(final Message<String> cReply) {
						System.out.println("STGART3");
						assertEquals("hello world", cReply.body());
						testComplete();
					}
			    });
			}
		});
	}

}
