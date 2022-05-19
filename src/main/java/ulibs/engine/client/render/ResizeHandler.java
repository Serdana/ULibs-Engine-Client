package main.java.ulibs.engine.client.render;

import main.java.ulibs.common.math.Vec2i;
import main.java.ulibs.engine.client.ClientBase;

public abstract class ResizeHandler {
	private final Vec2i viewportSize, viewportPos = new Vec2i();
	
	public ResizeHandler() {
		viewportSize = new Vec2i(ClientBase.getDefaultWidth(), ClientBase.getDefaultHeight());
	}
	
	public abstract void onResize(int width, int height);
	
	public abstract Vec2i setMousePos(int x, int y);
	
	public abstract boolean usesViewportPos();
	
	public boolean isViewportCentered() {
		return true;
	}
	
	public int getViewportX() {
		return viewportPos.getX();
	}
	
	public int getViewportY() {
		return viewportPos.getY();
	}
	
	public int getViewportW() {
		return viewportSize.getX();
	}
	
	public int getViewportH() {
		return viewportSize.getY();
	}
	
	protected void setViewportPos(int x, int y) {
		viewportPos.set(x, y);
	}
	
	protected void setViewportSize(int width, int height) {
		viewportSize.set(width, height);
	}
}
