/**
 *
 */
package org.vertx.promises;

/**
 * @author richard
 *
 */
public interface Combiner<L, R, T> {

	public Promise<T> combine(L left, R right);

}
