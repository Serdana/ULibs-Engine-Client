package main.java.ulibs.engine.client.input;

import java.util.HashMap;
import java.util.Map;

/** You shouldn't ever need to use this. This is only used internally.
 * @author -Unknown-
 */
public class Inputs {
	static final Map<Integer, EnumKeyInput> KEY_INPUT_MAP = new HashMap<Integer, EnumKeyInput>();
	static final Map<Integer, EnumMouseInput> MOUSE_INPUT_MAP = new HashMap<Integer, EnumMouseInput>();
	
	/** You shouldn't ever need to use this. This is only used internally.
	* @param key GLFW id to convert to enum
	* @return A {@link EnumKeyInput} that represents the given ID
	*/
	public static EnumKeyInput getKeyFromInt(int key) {
		return KEY_INPUT_MAP.getOrDefault(key, EnumKeyInput.KEY_UNKNOWN);
	}
	
	/** You shouldn't ever need to use this. This is only used internally.
	 * @param key GLFW id to convert to enum
	 * @return A {@link EnumMouseInput} that represents the given ID
	 */
	public static EnumMouseInput getMouseFromInt(int key) {
		return MOUSE_INPUT_MAP.getOrDefault(key, EnumMouseInput.MOUSE_UNKNOWN);
	}
	
	/** This is only used to ensure {@link IInputHandler} uses one of the correct enums */
	interface IInputEnum {
		
	}
}
