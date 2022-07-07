package main.java.ulibs.engine.client.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.engine.client.utils.BufferUtils;
import main.java.ulibs.engine.client.utils.GLException;
import main.java.ulibs.engine.client.utils.GLException.Reason;

public class VertexArray extends VertexArrayData {
	protected int vao, vbo, ibo, tbo;
	protected int count;
	protected boolean wasSetup = false;
	
	/** Sets up everything to be ready for rendering
	 * <br>
	 * Must be called before using! */
	public void setup() {
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
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(getVertices()), GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(Shader.VERTEX_ATTRIB, 3, GL11.GL_FLOAT, false, 0, 0);
		GL20.glEnableVertexAttribArray(Shader.VERTEX_ATTRIB);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(getTcs()), GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(Shader.TCOORD_ATTRIB, 2, GL11.GL_FLOAT, false, 0, 0);
		GL20.glEnableVertexAttribArray(Shader.TCOORD_ATTRIB);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createIntBuffer(indices), GL15.GL_DYNAMIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public void bind() {
		if (!wasSetup) {
			Console.print(WarningType.FatalError, "VertexArray was not setup!", new GLException(Reason.NOT_SETUP_VERTEX_ARRAY)).printStackTrace();
			return;
		}
		
		GL30.glBindVertexArray(vao);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
	}
	
	public void unbind() {
		GL30.glBindVertexArray(0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void draw() {
		GL11.glDrawElements(GL11.GL_TRIANGLES, count, GL11.GL_UNSIGNED_INT, 0);
	}
	
	public void drawOnce() {
		bind();
		draw();
		unbind();
	}
	
	@Override
	public void reset() {
		super.reset();
		wasSetup = false;
		vao = 0;
		vbo = 0;
		ibo = 0;
		tbo = 0;
		count = 0;
	}
}
