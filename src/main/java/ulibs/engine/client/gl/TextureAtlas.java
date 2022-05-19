package main.java.ulibs.engine.client.gl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.ulibs.common.helpers.MathH;
import main.java.ulibs.common.math.Vec2f;
import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.engine.client.utils.GetResource;
import main.java.ulibs.engine.common.utils.INameable;

public class TextureAtlas<T extends INameable> {
	private final Map<T, Vec2f> map = new HashMap<T, Vec2f>();
	private final int size;
	private final Texture texture;
	
	public TextureAtlas(List<T> list, int cellSize, String folder) {
		this.size = MathH.ceil(Math.sqrt(list.size()));
		Console.print(WarningType.TextureDebug, "Creating atlas using folder '" + folder + "' that is " + size + "x" + size);
		
		BufferedImage img = new BufferedImage(size * cellSize, size * cellSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		
		float size = this.size, s = 1f / size; //For future me, size needs to be a float cuz math
		for (int i = 0; i < list.size(); i++) {
			map.put(list.get(i), new Vec2f((i % size) / size, -MathH.floor(i / size) / size - s));
			
			g.drawImage(getTexture(folder, list.get(i).getName()), (i % this.size) * cellSize, MathH.floor(i / this.size) * cellSize, cellSize, cellSize, null);
		}
		g.dispose();
		
		texture = new Texture(img);
	}
	
	public float[] getTextureCoordsFor(T obj) {
		Vec2f texCoords = map.get(obj);
		float s = 1f / size;
		
		return new float[] { texCoords.getX(), texCoords.getY() + s, texCoords.getX(), texCoords.getY(), texCoords.getX() + s, texCoords.getY(), texCoords.getX() + s,
				texCoords.getY() + s };
	}
	
	/** Same as {@link Texture#bind()} */
	public void bind() {
		texture.bind();
	}
	
	/** @return The Width/Height of the atlas */
	public int size() {
		return size;
	}
	
	private static BufferedImage getTexture(String folder, String name) {
		Console.print(WarningType.TextureDebug, "Registered '" + name + "' for " + TextureAtlas.class.getSimpleName() + "!");
		return GetResource.getTexture(folder, name);
	}
}
