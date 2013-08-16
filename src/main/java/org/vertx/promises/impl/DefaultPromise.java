/**
 *
 */
package org.vertx.promises.impl;

import org.vertx.java.core.Handler;
import org.vertx.promises.Combiner;
import org.vertx.promises.Function;
import org.vertx.promises.Promise;

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

	DefaultPromise(final Handler<E> delegate) {
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
	 * @see org.vertx.promises.Promise#compose(org.vertx.promises.Promise, org.vertx.promises.Combiner)
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
						final Promise<T> newEvent = combiner.combine(thisEvent, nextEvent);
						newEvent.then(next);
					}
				});
			}
		});
		return next;
	}

	/**
	 * @see org.vertx.promises.Promise#then(org.vertx.java.core.Handler)
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
	 * @see org.vertx.promises.Promise#using(org.vertx.promises.Function)
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
	 * @see org.vertx.promises.Promise#bind(org.vertx.promises.Function)
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
