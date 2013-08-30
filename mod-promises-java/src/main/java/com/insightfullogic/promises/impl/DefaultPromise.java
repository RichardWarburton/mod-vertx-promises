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

import org.vertx.java.core.Handler;

import com.insightfullogic.promises.Combiner;
import com.insightfullogic.promises.Function;
import com.insightfullogic.promises.Promise;

/**
 * @author richard
 *
 */
public class DefaultPromise<E> implements Promise<E> {

	private Handler<E> delegate;
	private boolean awaitingHandler;
	private E value;

	public DefaultPromise() {
		this(null);
	}

	public DefaultPromise(final Handler<E> delegate) {
		setDelegate(delegate);
		awaitingHandler = false;
	}

	/**
	 * @see org.vertx.java.core.Handler#handle(java.lang.Object)
	 */
	@Override
	public void handle(final E event) {
		if (delegate != null) {
			delegate.handle(event);
		} else {
			awaitingHandler = true;
			value = event;
		}
	}

	/**
	 * @see com.insightfullogic.promises.Promise#compose(com.insightfullogic.promises.Promise, com.insightfullogic.promises.Combiner)
	 */
	@Override
	public <R, T> Promise<T> compose(final Function<E, Promise<R>> nextAction, final Combiner<E, R, T> combiner) {
		final DefaultPromise<T> next = new DefaultPromise<>();
		setDelegate(new Handler<E>() {
			@Override
			public void handle(final E thisEvent) {
				final Promise<R> nextResult = nextAction.handle(thisEvent);
				nextResult.then(new Handler<R>() {
					@Override
					public void handle(final R nextEvent) {
						combiner.combine(thisEvent, nextEvent)
						        .then(next);
					}
				});
			}
		});
		return next;
	}
	
	@Override
	public <L, R, T> Promise<T> diamond(final Function<E, Promise<L>> left, final Function<E, Promise<R>> right, final Combiner<L, R, T> combiner) {
        final DefaultPromise<T> next = new DefaultPromise<>();
        setDelegate(new Handler<E>() {

            private Object stash = null;

            @SuppressWarnings("unchecked")
            @Override
            public void handle(final E thisEvent) {
                final Promise<L> leftResult = left.handle(thisEvent);
                final Promise<R> rightResult = right.handle(thisEvent);
                leftResult.then(new Handler<L>() {
                    @Override
                    public void handle(L left) {
                        if (stash == null) {
                            stash = left;
                        } else {
                            combiner.combine(left, (R) stash)
                                    .then(next);
                        }
                    }
                });
                
                rightResult.then(new Handler<R>() {
                    @Override
                    public void handle(R right) {
                        if (stash == null) {
                            stash = right;
                        } else {
                            combiner.combine((L) stash, right)
                                    .then(next);
                        }
                    }
                });
            }
        });
        return next;
    }



	/**
	 * @see com.insightfullogic.promises.Promise#then(org.vertx.java.core.Handler)
	 */
	@Override
	public Promise<Void> then(final Handler<E> nextAction) {
		final DefaultPromise<Void> next = new DefaultPromise<>();
		setDelegate(new Handler<E>() {
			@Override
			public void handle(final E event) {
				nextAction.handle(event);
				next.handle(null);
			}
		});
		return next;
	}

	/**
	 * @see com.insightfullogic.promises.Promise#using(com.insightfullogic.promises.Function)
	 */
	@Override
	public <T> Promise<T> using(final Function<E, T> mapper) {
		final DefaultPromise<T> next = new DefaultPromise<>();
		setDelegate(new Handler<E>() {
			@Override
			public void handle(final E event) {
				final T newValue = mapper.handle(event);
				next.handle(newValue);
			}
		});
		return next;
	}

	/**
	 * @see com.insightfullogic.promises.Promise#bind(com.insightfullogic.promises.Function)
	 */
	@Override
	public <T> Promise<T> bind(final Function<E, Promise<T>> mapper) {
		final Promise<T> next = new DefaultPromise<>();
		setDelegate(new Handler<E>() {
			@Override
			public void handle(final E event) {
				final Promise<T> result = mapper.handle(event);
				result.then(next);
			}
		});
		return next;
	}

	/**
	 * @param delegate the delegate to set
	 */
	public void setDelegate(final Handler<E> delegate) {
		if (this.delegate != null) {
			throw new IllegalArgumentException();
		}

		if (awaitingHandler) {
			delegate.handle(value);
		}
		this.delegate = delegate;
	}

	@Override
	public String toString() {
		return "DefaultPromise [delegate=" + delegate + ", awaitingHandler=" + awaitingHandler + ", value=" + value + "]";
	}

}
