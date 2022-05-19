package main.java.ulibs.engine.client.render;

import main.java.ulibs.common.math.Vec2i;
import main.java.ulibs.engine.client.ClientBase;
import main.java.ulibs.engine.client.gl.Shader;
import main.java.ulibs.engine.client.helpers.GLH;
import main.java.ulibs.engine.client.init.Shaders;
import main.java.ulibs.engine.client.math.Matrix4f;

public class ResizeHandlerIgnore extends ResizeHandler {
	@Override
	public void onResize(int width, int height) {
		setViewportSize(width, height);
		GLH.setViewport(0, 0, width, height);
		
		for (Shader s : Shaders.getAll()) {
			float wm = (float) width / (float) ClientBase.getDefaultWidth(), ym = (float) height / (float) ClientBase.getDefaultHeight();
			s.bind();
			s.setProjectionMatrix(Matrix4f.orthographic(s.left, s.right * wm, s.top * ym, s.bottom, s.near, s.far));
		}
		GLH.unbindShader();
	}
	
	@Override
	public Vec2i setMousePos(int x, int y) {
		return new Vec2i(x, y);
	}
	
	@Override
	public boolean usesViewportPos() {
		return false;
	}
}
