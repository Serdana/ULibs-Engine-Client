package main.java.ulibs.engine.client.input;

import main.java.ulibs.engine.client.input.Inputs.IInputEnum;

/** A simple interface that allows for easy input handling
 * @author -Unknown-
 * @param <T> The type of input that this class will handle. Should be either {@link EnumKeyInput} or {@link EnumMouseInput}
 */
public interface IInputHandler<T extends Enum<T> & IInputEnum> {
	/** Method that runs when an input of the given type is pressed
	 * @param input See {@link IInputHandler}'s type doc
	 */
	public abstract void onPress(T input);
	
	/** Method that runs when an input of the given type is released
	 * @param input See {@link IInputHandler}'s type doc
	 */
	public abstract void onRelease(T input);
	
	/** Method that runs when an input of the given type is repeated
	 * @param input See {@link IInputHandler}'s type doc
	 */
	public abstract void onRepeat(T input);
}
