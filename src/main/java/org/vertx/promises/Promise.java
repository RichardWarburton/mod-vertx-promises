/**
 *
 */
package org.vertx.promises;

import org.vertx.java.core.Handler;

/**
 * @author richard
 */
public interface Promise<E> extends Handler<E> {

	public Promise<Void> then(Handler<E> nextAction);

	public <T> Promise<T> using(Function<E, T> mapper);

	public <T> Promise<T> bind(Function<E, Promise<T>> mapper);

	public <R, T> Promise<T> compose(Promise<R> promise, Combiner<E, R, T> combiner);

}
