package main.java.ulibs.engine.client.gl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;

import main.java.ulibs.common.math.Vec2f;
import main.java.ulibs.common.math.Vec3f;
import main.java.ulibs.common.math.Vec4f;
import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.engine.client.helpers.GLH;
import main.java.ulibs.engine.client.math.Matrix4f;
import main.java.ulibs.engine.client.utils.GLException;
import main.java.ulibs.engine.client.utils.GLException.Reason;

public abstract class Shader {
	public static final int VERTEX_ATTRIB = 0;
	public static final int TCOORD_ATTRIB = 1;
	
	public final String name;
	public final int id;
	private boolean wasSetup;
	
	public final float left, right, top, bottom, near, far;
	
	private final Map<String, Integer> locationCache = new HashMap<String, Integer>();
	
	/**
	 * @param name The name of the Shader
	 * @param internalTitle The internal title package name
	 */
	protected Shader(String name, String internalTitle, float left, float right, float bottom, float top, float near, float far) {
		this.name = name;
		this.id = load(name, internalTitle);
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;
		this.near = near;
		this.far = far;
	}
	
	public final void setup() {
		wasSetup = true;
		bind();
		setProjectionMatrix(Matrix4f.orthographic(left, right, top, bottom, near, far));
		internalSetup();
		GLH.unbindShader();
	}
	
	/** Don't worry about binding/unbinding shader here! That's being handled elsewhere */
	protected abstract void internalSetup();
	
	/** Don't worry about binding/unbinding shader here! That's being handled elsewhere */
	public void onResize() {
		
	}
	
	/** Binds the shader for use */
	public void bind() {
		if (!wasSetup) {
			Console.print(WarningType.FatalError, "Tried to use shader '" + name + "' but it was never setup!", new GLException(Reason.notSetupShader));
			return;
		}
		
		GL20.glUseProgram(id);
	}
	
	public void setProjectionMatrix(Matrix4f matrix) {
		set("projection_matrix", matrix);
	}
	
	protected void set(String name, int value) {
		GL20.glUniform1i(getUniform(name), value);
	}
	
	protected void set(String name, int[] value) {
		GL20.glUniform1iv(getUniform(name), value);
	}
	
	protected void set(String name, float value) {
		GL20.glUniform1f(getUniform(name), value);
	}
	
	protected void set(String name, Vec2f vec) {
		GL20.glUniform2f(getUniform(name), vec.getX(), vec.getY());
	}
	
	protected void set(String name, Vec3f vec) {
		GL20.glUniform3f(getUniform(name), vec.getX(), vec.getY(), vec.getZ());
	}
	
	protected void set(String name, Vec4f vec) {
		GL20.glUniform4f(getUniform(name), vec.getX(), vec.getY(), vec.getZ(), vec.getW());
	}
	
	protected void set(String name, Matrix4f matrix) {
		GL20.glUniformMatrix4fv(getUniform(name), false, matrix.toFloatBuffer());
	}
	
	protected int getUniform(String name) {
		if (locationCache.containsKey(name)) {
			return locationCache.get(name);
		}
		
		int result = GL20.glGetUniformLocation(id, name);
		if (result == -1) {
			Console.print(WarningType.Error, "Could not find uniform variable '" + name + "'!");
		} else {
			locationCache.put(name, result);
		}
		return result;
	}
	
	private static int load(String name, String internalTitle) {
		String vert = loadAsString(Shader.class.getResourceAsStream("/main/resources/" + internalTitle + "/assets/shaders/" + name + ".vert"));
		String frag = loadAsString(Shader.class.getResourceAsStream("/main/resources/" + internalTitle + "/assets/shaders/" + name + ".frag"));
		
		int program = GL20.glCreateProgram(), vertID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER), fragID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(vertID, vert);
		GL20.glShaderSource(fragID, frag);
		
		GL20.glCompileShader(vertID);
		if (GL20.glGetShaderi(vertID, GL20.GL_COMPILE_STATUS) == GLH.FALSE) {
			Console.print(WarningType.Error, "Failed to compile vertex shader called '" + name + "'!");
			Console.print(WarningType.Error, GL20.glGetShaderInfoLog(vertID));
			return -1;
		}
		
		GL20.glCompileShader(fragID);
		if (GL20.glGetShaderi(fragID, GL20.GL_COMPILE_STATUS) == GLH.FALSE) {
			Console.print(WarningType.Error, "Failed to compile fragment shader '" + name + "'!");
			Console.print(WarningType.Error, GL20.glGetShaderInfoLog(fragID));
			return -1;
		}
		
		GL20.glAttachShader(program, vertID);
		GL20.glAttachShader(program, fragID);
		GL20.glLinkProgram(program);
		GL20.glValidateProgram(program);
		
		GL20.glDeleteShader(vertID);
		GL20.glDeleteShader(fragID);
		
		return program;
	}
	
	private static String loadAsString(InputStream is) {
		StringBuilder sb = new StringBuilder();
		
		try (BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
			String buffer = "";
			while ((buffer = r.readLine()) != null) {
				sb.append(buffer + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
}
