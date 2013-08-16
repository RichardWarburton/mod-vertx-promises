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
public class PromisesVerticleTest extends TestVerticle {

	private static final String Q = "whats up ";

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
					return bus.send("a", Q + "a");
				}
			}).compose(new Function<Message<String>, Promise<Message<String>>>() {
				@Override
				public Promise<Message<String>> handle(final Message<String> from) {
					return bus.send("b", Q + "b");
				}
			}, new Combiner<Message<String>, Message<String>, Message<String>>() {
				@Override
				public Promise<Message<String>> combine(final Message<String> left, final Message<String> right) {
					return bus.send("c", left.body() + " " + right.body());
				}
		    }).then(new Handler<Message<String>>() {
				@Override
				public void handle(final Message<String> cReply) {
					assertEquals("hello world", cReply.body());
					testComplete();
				}
		    });
	}

}
