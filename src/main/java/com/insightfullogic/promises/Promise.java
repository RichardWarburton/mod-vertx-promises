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

import org.vertx.java.core.Handler;

/**
 * @author richard
 */
public interface Promise<E> extends Handler<E> {

	public Promise<Void> then(Handler<E> nextAction);

	public <T> Promise<T> using(Function<E, T> mapper);

	public <T> Promise<T> bind(Function<E, Promise<T>> mapper);

	public <R, T> Promise<T> compose(Function<E, Promise<R>> promise, Combiner<E, R, T> combiner);

}
