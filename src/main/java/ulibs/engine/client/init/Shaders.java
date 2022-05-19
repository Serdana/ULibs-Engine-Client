package main.java.ulibs.engine.client.init;

import java.util.ArrayList;
import java.util.List;

import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.engine.client.ClientBase;
import main.java.ulibs.engine.client.gl.Shader;
import main.java.ulibs.engine.client.gl.ShaderTextured;
import main.java.ulibs.engine.client.gl.ShaderTextureless;

public class Shaders {
	private static final List<Shader> ALL = new ArrayList<Shader>();
	
	private static ShaderTextured hud;
	private static ShaderTextureless hudTextureless;
	private static ShaderTextured object;
	private static ShaderTextureless objectTextureless;
	
	public static void registerAll() {
		Console.print(WarningType.Info, "Started registering " + Shaders.class.getSimpleName() + "!");
		int w = ClientBase.getDefaultWidth(), h = ClientBase.getDefaultHeight();
		
		hud = add(new ShaderTextured(0f, w, 0f, h, -1f, 10f));
		hudTextureless = add(new ShaderTextureless(0f, w, 0f, h, -1f, 10f));
		object = add(new ShaderTextured(0f, 16f, 0f, 9f, -1f, 10f));
		objectTextureless = add(new ShaderTextureless(0f, 16f, 0f, 9f, -1f, 10f));
		
		for (Shader s : ALL) {
			s.setup();
		}
	}
	
	private static <T extends Shader> T add(T s) {
		ALL.add(s);
		Console.print(Console.WarningType.RegisterDebug, "'" + s.name + "' was registered!");
		return s;
	}
	
	public static ShaderTextured Hud() {
		return hud;
	}
	
	public static ShaderTextureless HudTextureless() {
		return hudTextureless;
	}
	
	public static ShaderTextured Object() {
		return object;
	}
	
	public static ShaderTextureless ObjectTextureless() {
		return objectTextureless;
	}
	
	public static List<Shader> getAll() {
		return ALL;
	}
}
