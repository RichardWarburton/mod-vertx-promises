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
package com.insightfullogic.promises.unit;

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
				reply(msg, "hello");
			}
		});

		eb.registerHandler("b", new Handler<Message<String>>() {
			@Override
			public void handle(final Message<String> msg) {
				reply(msg, "world");
			}
		});

		eb.registerHandler("c", new Handler<Message<String>>() {
			@Override
			public void handle(final Message<String> msg) {
				reply(msg, msg.body());
			}
		});
	}

	public void reply(final Message<String> msg, final String reply) {
		msg.reply(reply);
	}

}
