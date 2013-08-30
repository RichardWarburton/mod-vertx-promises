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
package com.insightfullogic.promises;

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

	// TODO: take a handler of promise, as an argument, and return the result as a promise.
	@SuppressWarnings("rawtypes")
	Promise<AsyncResult<Void>> registerHandler(final String address, Handler<? extends Message> handler);

}
