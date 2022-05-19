package main.java.ulibs.engine.client.input;

/** A simple interface that allows for easy scroll wheel handling
 * @author -Unknown-
 */
public abstract interface IScrollHandler {
	/** Runs whenever the scroll wheel is scrolled up */
	public abstract void onScrollUp();
	
	/** Runs whenever the scroll wheel is scrolled down */
	public abstract void onScrollDown();
}
