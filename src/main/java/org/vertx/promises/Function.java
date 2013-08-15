/**
 *
 */
package org.vertx.promises;

/**
 * @author richard
 *
 */
public interface Function<F, T> {

	public T handle (F from);

}
