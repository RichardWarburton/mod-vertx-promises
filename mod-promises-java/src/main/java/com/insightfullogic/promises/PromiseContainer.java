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
import org.vertx.java.platform.Container;

import com.insightfullogic.promises.impl.DefaultPromise;

/**
 * @author richard
 */
public class PromiseContainer {

	private final Container container;

	public PromiseContainer(final Container container) {
		this.container = container;
	}

	public Promise<AsyncResult<String>> deployVerticle(final String main) {
		final Promise<AsyncResult<String>> donePromise = new DefaultPromise<AsyncResult<String>>();
		container.deployVerticle(main, donePromise);
		return donePromise;
	}

}
