package main.java.ulibs.engine.client.input;

import org.lwjgl.glfw.GLFW;

import main.java.ulibs.engine.client.input.Inputs.IInputEnum;

/** Enum containing all possible mouse inputs
 * @author -Unknown-
 */
@SuppressWarnings("javadoc")
public enum EnumMouseInput implements IInputEnum {
	MOUSE_UNKNOWN(-1),
	
	/** Left Mouse Button */
	MOUSE_BUTTON_1(GLFW.GLFW_MOUSE_BUTTON_1),
	/** Right Mouse Button */
	MOUSE_BUTTON_2(GLFW.GLFW_MOUSE_BUTTON_2),
	/** Middle Mouse Button */
	MOUSE_BUTTON_3(GLFW.GLFW_MOUSE_BUTTON_3),
	
	MOUSE_BUTTON_4(GLFW.GLFW_MOUSE_BUTTON_4),
	MOUSE_BUTTON_5(GLFW.GLFW_MOUSE_BUTTON_5),
	MOUSE_BUTTON_6(GLFW.GLFW_MOUSE_BUTTON_6),
	MOUSE_BUTTON_7(GLFW.GLFW_MOUSE_BUTTON_7),
	MOUSE_BUTTON_8(GLFW.GLFW_MOUSE_BUTTON_8);
	
	private final int id;
	
	private EnumMouseInput(int id) {
		this.id = id;
		Inputs.MOUSE_INPUT_MAP.put(id, this);
	}
	
	@Override
	public String toString() {
		switch (id) {
			case 1:
				return "Mouse: '1' (Left)";
			case 2:
				return "Mouse: '2' (Right)";
			case 3:
				return "Mouse: '3' (Middle)";
			default:
				return "Mouse: '" + id + "'";
		}
	}
}
