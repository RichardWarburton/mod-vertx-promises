/*
 * Copyright 2013 Richard Warburton <richard.warburton@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.insightfullogic.vertx.promises.unit;

import static org.vertx.testtools.VertxAssert.assertEquals;
import static org.vertx.testtools.VertxAssert.assertTrue;
import static org.vertx.testtools.VertxAssert.testComplete;

import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.testtools.TestVerticle;

import com.insightfullogic.vertx.promises.Combiner;
import com.insightfullogic.vertx.promises.Function;
import com.insightfullogic.vertx.promises.Promise;
import com.insightfullogic.vertx.promises.PromiseContainer;
import com.insightfullogic.vertx.promises.PromiseEventBus;
import com.insightfullogic.vertx.promises.PromiseVertx;

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
		final PromiseEventBus bus = vertx.promiseBus();
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
	
	@Test
    public void parallelNotNestingOfDoom() {
        final PromiseVertx vertx = new PromiseVertx(this.vertx);
        final PromiseEventBus bus = vertx.promiseBus();
        final PromiseContainer container = new PromiseContainer(this.container);
        container.deployVerticle(StubVerticle.class.getName())
        .diamond(new Function<AsyncResult<String>, Promise<Message<String>>>() {
            @Override
            public Promise<Message<String>> handle(AsyncResult<String> from) {
                assertTrue(from.succeeded());
                return bus.send("a", Q + "a");
            }
        }, new Function<AsyncResult<String>, Promise<Message<String>>>() {
            @Override
            public Promise<Message<String>> handle(AsyncResult<String> from) {
                assertTrue(from.succeeded());
                return bus.send("b", Q + "b");
            }
        }, new Combiner<Message<String>, Message<String>, Message<String>>() {
            @Override
            public Promise<Message<String>> combine(Message<String> left, Message<String> right) {
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

    @Test
    public void replyNestingOfDoom() {
        final PromiseVertx vertx = new PromiseVertx(this.vertx);
        final PromiseContainer container = new PromiseContainer(this.container);

        vertx.promiseBus()
             .registerHandler("testVerticle", new Handler<Message<String>>() {
            @Override
            public void handle(final Message<String> event) {
                assertEquals("abc", event.body());
                testComplete();
            }
        }).bind(new Function<AsyncResult<Void>, Promise<AsyncResult<String>>>() {
            @Override
            public Promise<AsyncResult<String>> handle(AsyncResult<Void> from) {
                return container.deployVerticle(SenderVerticle.class.getName());
            }
        }).then(new Handler<AsyncResult<String>>() {
            @Override
            public void handle(final AsyncResult<String> event) {
                assertTrue(event.succeeded());
            }
        });
    }

}
