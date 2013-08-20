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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.vertx.java.core.Handler;

import com.insightfullogic.promises.impl.DefaultPromise;

/**
 * Factory for Promises instances
 * 
 * @author richard
 */
public class Promises {

    public static <E> Promise<E> empty() {
        return new DefaultPromise<>();
    }

    public static <E> Promise<E> from(Handler<E> handler) {
        return new DefaultPromise<>(handler);
    }

    /**
     * This blocks, it really shouldn't.  Undecided as to the best way to handle this.
     */
    public static <E> Promise<E> from(Future<E> future) {
        DefaultPromise<E> promise = new DefaultPromise<>();
        try {
            promise.handle(future.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new PromiseException(e);
        }
        return promise;
    }

}
