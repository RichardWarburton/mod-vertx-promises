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
package com.insightfullogic.promises.impl;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;

import com.insightfullogic.promises.Promise;
import com.insightfullogic.promises.PromiseEventBus;

/**
 * @author richard
 *
 */
public class DefaultPromiseEventBus implements PromiseEventBus {

	private final EventBus eventBus;

	public DefaultPromiseEventBus(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * @see com.insightfullogic.promises.PromiseEventBus#send(java.lang.String, java.lang.String)
	 */
	@Override
	public Promise<Message<String>> send(final String address, final String message) {
		final Promise<Message<String>> promise = new DefaultPromise<>();
		eventBus.send(address, message, promise);
		return promise;
	}

	/**
	 * @see com.insightfullogic.promises.PromiseEventBus#registerHandler(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Promise<? extends Message> registerHandler(final String address) {
		final Promise<? extends Message> promise = new DefaultPromise<>();
		eventBus.registerHandler(address, promise);
		return promise;
	}

    @SuppressWarnings("rawtypes")
    @Override
    public Promise<AsyncResult<Void>> registerHandler(String address, Handler<? extends Message> handler) {
        final Promise<AsyncResult<Void>> resultHandler = new DefaultPromise<>();
        eventBus.registerHandler(address, handler, resultHandler);
        return resultHandler;
    }

}
