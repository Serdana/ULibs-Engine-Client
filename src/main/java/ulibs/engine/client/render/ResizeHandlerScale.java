package main.java.ulibs.engine.client.render;

import main.java.ulibs.common.math.Vec2i;
import main.java.ulibs.engine.client.ClientBase;
import main.java.ulibs.engine.client.helpers.GLH;

public class ResizeHandlerScale extends ResizeHandler {
	private final Vec2i hudSize;
	
	public ResizeHandlerScale() {
		this.hudSize = new Vec2i(ClientBase.getDefaultWidth(), ClientBase.getDefaultHeight());
	}
	
	@Override
	public void onResize(int width, int height) {
		int aspectWidth = width, aspectHeight = (int) (aspectWidth / (16f / 9f));
		
		if (aspectHeight > height) {
			aspectHeight = height;
			aspectWidth = (int) (aspectHeight * (16f / 9f));
		}
		
		int aspectX = (int) ((width / 2f) - (aspectWidth / 2f));
		int aspectY = (int) ((height / 2f) - (aspectHeight / 2f));
		
		setViewportPos(aspectX, aspectY);
		setViewportSize(aspectWidth, aspectHeight);
		GLH.setViewport(aspectX, aspectY, aspectWidth, aspectHeight);
	}
	
	@Override
	public Vec2i setMousePos(int x, int y) {
		float mousePosX = (((float) x - getViewportX()) / (getViewportW() / getHudW()));
		float mousePosY = (((float) y - getViewportY()) / (getViewportH() / getHudH()));
		return new Vec2i(Math.round(mousePosX), Math.round(mousePosY));
	}
	
	@Override
	public boolean usesViewportPos() {
		return true;
	}
	
	public int getHudW() {
		return hudSize.getX();
	}
	
	public int getHudH() {
		return hudSize.getY();
	}
}
