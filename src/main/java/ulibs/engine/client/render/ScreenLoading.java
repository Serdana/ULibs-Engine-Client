package main.java.ulibs.engine.client.render;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.java.ulibs.engine.client.ClientBase;
import main.java.ulibs.engine.client.gl.Texture;
import main.java.ulibs.engine.client.gl.VertexArray;
import main.java.ulibs.engine.client.gl.ZConstant;
import main.java.ulibs.engine.client.gl.geometry.Quad;
import main.java.ulibs.engine.client.helpers.GLH;
import main.java.ulibs.engine.client.init.Shaders;
import main.java.ulibs.engine.client.utils.GetResource;

public class ScreenLoading implements IRenderer {
	private static final Texture LOADING_SCREEN = new Texture(getDefaultTexture());
	private final VertexArray va = new VertexArray();
	
	@Override
	public void setupGL() {
		ResizeHandler rh = ClientBase.getResizeHandler();
		va.add(new Quad(0, 0, rh.getViewportW(), rh.getViewportH(), ZConstant.Z_HUD_BASE));
		va.setup();
	}
	
	@Override
	public void renderPre() {
		Shaders.Hud().bind();
		LOADING_SCREEN.bind();
		va.drawOnce();
		GLH.unbindTexture();
		GLH.unbindShader();
	}
	
	private static BufferedImage getDefaultTexture() {
		try {
			return ImageIO.read(GetResource.class.getResourceAsStream("/main/resources/ulibs/engine/assets/textures/gui/loading_screen.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void onResize() {
		
	}
}
