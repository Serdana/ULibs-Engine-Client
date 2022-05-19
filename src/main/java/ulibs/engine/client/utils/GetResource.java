package main.java.ulibs.engine.client.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.engine.client.ClientBase;

public class GetResource {
	/** A blank debug image that'll be used in the event {@link GetResource#getTexture(String, String)} could not find any image at the given path */
	public static final BufferedImage NIL = getNilTexture();
	
	private static BufferedImage getNilTexture() {
		try {
			return ImageIO.read(GetResource.class.getResourceAsStream("/main/resources/ulibs/engine/assets/textures/nil.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedImage getTexture(String folder, String textureName) {
		if (!folder.isEmpty() && !folder.endsWith("/")) {
			folder += "/";
		}
		
		if (GetResource.class.getResourceAsStream(ClientBase.getAssetLocation() + "textures/" + folder + textureName + ".png") == null) {
			Console.print(WarningType.Error, "Cannot find texture : '" + ClientBase.getAssetLocation() + "textures/" + folder + textureName + ".png'");
			return NIL;
		}
		
		try {
			return ImageIO.read(GetResource.class.getResourceAsStream(ClientBase.getAssetLocation() + "textures/" + folder + textureName + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
			return NIL;
		}
	}
	
	public static BufferedImage getTexture(String textureName) {
		return getTexture("", textureName);
	}
}
