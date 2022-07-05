package main.java.ulibs.engine.client.gl;

import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import main.java.ulibs.engine.client.utils.BufferUtils;

public class Texture {
	private final int width, height;
	public final int textureID;
	private final boolean useAntiAliasing;
	
	public Texture(BufferedImage img, boolean useAntiAliasing) {
		this.useAntiAliasing = useAntiAliasing;
		width = img.getWidth();
		height = img.getHeight();
		textureID = load(img);
	}
	
	public Texture(BufferedImage img) {
		this(img, false);
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
	
	private int load(BufferedImage img) {
		int[] pixels = img.getRGB(0, 0, width, height, new int[width * height], 0, width), data = new int[width * height];
		
		for (int i = 0; i < width * height; i++) {
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);
			
			data[i] = a << 24 | b << 16 | g << 8 | r;
		}
		
		int scaleType = useAntiAliasing ? GL11.GL_LINEAR : GL11.GL_NEAREST;
		
		int result = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, result);
		GL30.glTexParameterIi(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, scaleType);
		GL30.glTexParameterIi(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, scaleType);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(data));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		return result;
	}
}
