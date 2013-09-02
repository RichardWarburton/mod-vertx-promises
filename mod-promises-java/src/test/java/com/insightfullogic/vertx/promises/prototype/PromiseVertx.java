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
package com.insightfullogic.vertx.promises.prototype;

import org.vertx.java.core.Vertx;


/**
 * @author richard
 *
 */
public class PromiseVertx {

	private final Vertx vertx;

	public PromiseVertx(final Vertx vertx) {
		this.vertx = vertx;
	}

	public PromiseEventBus promiseBus() {
		return new DefaultPromiseEventBus(vertx.eventBus());
	}

}
