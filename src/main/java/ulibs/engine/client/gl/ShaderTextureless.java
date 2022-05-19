package main.java.ulibs.engine.client.gl;

import java.awt.Color;

import main.java.ulibs.common.helpers.ColorH;
import main.java.ulibs.engine.client.math.Matrix4f;

public class ShaderTextureless extends Shader {
	private Matrix4f lastTrMatrix = Matrix4f.identity();
	
	public ShaderTextureless(float left, float right, float bottom, float top, float near, float far) {
		super("textureless", "ulibs/engine", left, right, bottom, top, near, far);
	}
	
	@Override
	protected void internalSetup() {
		setColor(Color.WHITE);
	}
	
	public void setViewMatrix(Matrix4f matrix) {
		set("view_matrix", matrix);
	}
	
	public void setTransformMatrix(Matrix4f matrix) {
		lastTrMatrix = matrix;
		set("transform_matrix", matrix);
	}
	
	public void setColor(Color color) {
		set("color_vec4", ColorH.colorToVec4(color));
	}
	
	public Matrix4f getLastTransformMatrix() {
		return lastTrMatrix;
	}
}
