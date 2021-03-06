package main.java.ulibs.engine.client.gl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.engine.client.gl.geometry.GeoData;
import main.java.ulibs.engine.client.utils.BufferUtils;
import main.java.ulibs.engine.client.utils.GLException;
import main.java.ulibs.engine.client.utils.GLException.Reason;

/** Similar to {@link VertexArray} but instead has control over the elements inside
 * @author -Unknown-
 * @param <T> The Key to use for the {@link VertexArrayData} map
 */
public class KeyedVertexArray<T> {
	private final Map<T, VertexArrayData> map = new LinkedHashMap<T, VertexArrayData>();
	private int vao, vbo, ibo, tbo, count;
	private boolean wasSetup = false, requiresCacheReset = true;
	
	private float[] verticesCache = new float[0], tcsCache = new float[0];
	private int[] indicesCache = new int[0];
	
	public KeyedVertexArray<T> setObjectData(T t, VertexArrayData data) {
		if (!map.containsKey(t)) {
			map.put(t, data);
			return this;
		}
		
		map.get(t).set(data);
		requiresCacheReset = true;
		return this;
	}
	
	public KeyedVertexArray<T> setObjectData(T t, GeoData geodata) {
		VertexArrayData data = new VertexArrayData().add(geodata);
		if (!map.containsKey(t)) {
			map.put(t, data);
		}
		
		map.get(t).set(data);
		requiresCacheReset = true;
		return this;
	}
	
	/** Removed the given key from the map
	 * @param t The key to remove
	 */
	public void removeObject(int t) {
		map.remove(t);
		requiresCacheReset = true;
	}
	
	/** Sets up everything to be ready for rendering
	 * <br>
	 * Must be called before using! */
	public void setup() {
		float[] vertices = getVertices();
		float[] tcs = getTcs();
		int[] indices = getIndices();
		
		count = indices.length;
		if (!wasSetup) {
			wasSetup = true;
			vao = GL30.glGenVertexArrays();
			vbo = GL15.glGenBuffers();
			tbo = GL15.glGenBuffers();
			ibo = GL15.glGenBuffers();
		}
		
		GL30.glBindVertexArray(vao);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(vertices), GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(Shader.VERTEX_ATTRIB, 3, GL11.GL_FLOAT, false, 0, 0);
		GL20.glEnableVertexAttribArray(Shader.VERTEX_ATTRIB);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(tcs), GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(Shader.TCOORD_ATTRIB, 2, GL11.GL_FLOAT, false, 0, 0);
		GL20.glEnableVertexAttribArray(Shader.TCOORD_ATTRIB);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createIntBuffer(indices), GL15.GL_DYNAMIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	/** Binds self to be used for rendering
	 * <br>
	 * Must be called before using! */
	public void bind() {
		if (!wasSetup) {
			Console.print(WarningType.FatalError, "KeyedVertexArray was not setup!", new GLException(Reason.NOT_SETUP_VERTEX_ARRAY)).printStackTrace();
			return;
		}
		
		GL30.glBindVertexArray(vao);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
	}
	
	/** Unbinds self and frees resources
	 * <br>
	 * Should be called when done! */
	public void unbind() {
		GL30.glBindVertexArray(0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	/** Draws the stored data
	 * <br>
	 * Must call {@link #bind} before using! */
	public void draw() {
		GL11.glDrawElements(GL11.GL_TRIANGLES, count, GL11.GL_UNSIGNED_INT, 0);
	}
	
	public void drawOnce() {
		bind();
		draw();
		unbind();
	}
	
	public void reset() {
		wasSetup = false;
		vao = 0;
		vbo = 0;
		ibo = 0;
		tbo = 0;
		count = 0;
		map.clear();
		requiresCacheReset = true;
	}
	
	private float[] getVertices() {
		if (requiresCacheReset) {
			resetCache();
		}
		return verticesCache;
	}
	
	private int[] getIndices() {
		if (requiresCacheReset) {
			resetCache();
		}
		return indicesCache;
	}
	
	private float[] getTcs() {
		if (requiresCacheReset) {
			resetCache();
		}
		return tcsCache;
	}
	
	private void resetCache() {
		requiresCacheReset = false;
		
		int verticiesSize = 0;
		for (VertexArrayData data : map.values()) {
			for (GeoData gd : data.datas) {
				verticiesSize += gd.vertices.length;
			}
		}
		
		FloatBuffer buf0 = FloatBuffer.allocate(verticiesSize);
		map.values().forEach(c -> c.datas.forEach(gd -> buf0.put(gd.vertices)));
		verticesCache = buf0.flip().array();
		
		int indicesSize = 0;
		for (VertexArrayData data : map.values()) {
			for (GeoData gd : data.datas) {
				indicesSize += gd.indices.length;
			}
		}
		
		IntBuffer buf1 = IntBuffer.allocate(indicesSize);
		int count = 0;
		for (VertexArrayData data : map.values()) {
			for (GeoData gd : data.datas) {
				int[] ids = gd.indices.clone();
				for (int i = 0; i < ids.length; i++) {
					ids[i] += count * 4;
				}
				
				buf1.put(ids);
				count++;
			}
		}
		
		indicesCache = buf1.flip().array();
		
		int tcsSize = 0;
		for (VertexArrayData data : map.values()) {
			for (GeoData gd : data.datas) {
				tcsSize += gd.tcs.length;
			}
		}
		
		FloatBuffer buf2 = FloatBuffer.allocate(tcsSize);
		map.values().forEach(c -> c.datas.forEach(gd -> buf2.put(gd.tcs)));
		tcsCache = buf2.flip().array();
	}
}
