package main.java.ulibs.engine.client.render;

import main.java.ulibs.common.math.Vec2i;
import main.java.ulibs.engine.client.helpers.GLH;

public class ResizeHandlerStretch extends ResizeHandler {
	@Override
	public void onResize(int width, int height) {
		setViewportSize(width, height);
		GLH.setViewport(0, 0, width, height);
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
