package main.java.ulibs.engine.client.helpers;

import java.awt.Color;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import main.java.ulibs.common.math.Vec2i;
import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.engine.client.utils.GLException;
import main.java.ulibs.engine.client.utils.GLException.Reason;

public class GLH {
	/** GL True */
	public static final int TRUE = GL11.GL_TRUE;
	/** GL False */
	public static final int FALSE = GL11.GL_FALSE;
	/** GL Null */
	public static final long NULL = MemoryUtil.NULL;
	
	/**
	 * @param x Viewport's X
	 * @param y Viewport's Y
	 * @param w Viewport's Width
	 * @param h Viewport's Height
	 * @return {@link ByteBuffer} with viewport's pixel data
	 */
	public static ByteBuffer readPixels(int x, int y, int w, int h) {
		GL11.glReadBuffer(GL11.GL_FRONT);
		ByteBuffer buf = BufferUtils.createByteBuffer(w * h * 4);
		GL11.glReadPixels(x, y, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		return buf;
	}
	
	/** Unbinds the current texture **/
	public static void unbindTexture() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	/** Unbinds the current shader **/
	public static void unbindShader() {
		GL20.glUseProgram(0);
	}
	
	/** Enables GL Scissors */
	public static void enableScissors() {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}
	
	/** Disables GL Scissors */
	public static void disableScissors() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	//TODO make this take normal coords instead of upside down GL coords
	public static void scissor(int x, int y, int w, int h) {
		GL11.glScissor(x, y, w, h);
	}
	
	public static void createCapabilities() {
		GL.createCapabilities();
	}
	
	/** Sets the given {@link Color} as the clear color
	 * @param color The color to use
	 */
	public static void clearColor(Color color) {
		GL11.glClearColor(color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f, color.getAlpha() / 256f);
	}
	
	//TODO make this use an enum or something to make my life easier!
	//future me here, what the fuck did i mean by use an enum?
	public static int getError() {
		return GL11.glGetError();
	}
	
	public static boolean isError(int error) {
		return error != GL11.GL_NO_ERROR;
	}
	
	public static void clearColorDepthBuffer() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	/** Sets the Viewport to the given values
	 * @param x Viewport's X
	 * @param y Viewport's Y
	 * @param w Viewport's Width
	 * @param h Viewport's Height
	 */
	public static void setViewport(int x, int y, int w, int h) {
		GL11.glViewport(x, y, w, h);
	}
	
	/** Enables GL Depth Test */
	public static void enableDepth() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	/** Disables GL Depth Test */
	public static void disableDepth() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	/** Enables GL Blend */
	public static void enableBlend() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	/** Disables GL Blend */
	public static void disableBlend() {
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void setActiveTexture0() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
	}
	
	/** @return The GL Version */
	public static String getVersion() {
		return GL11.glGetString(GL11.GL_VERSION);
	}
	
	/**
	 * @param title The title to use for the window
	 * @param width The width to use for the window
	 * @param height The height to use for the window
	 * @return A long representing the window ID
	 * @throws GLException Throws if the window fails to initialize. Will print stacktrace automatically.
	 */
	public static long createWindow(String title, int width, int height) throws GLException {
		long window = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
		if (window == GLH.NULL) {
			throw Console.print(WarningType.FatalError, "Failed to initialize Window!", new GLException(Reason.FAILED_TO_INIT_WINDOW));
		}
		
		return window;
	}
	
	/**
	 * @param resizeable Whether or not to allow window resizing
	 * @return The current monitor's width & height as a {@link Vec2i}
	 */
	public static Vec2i setWindowHints(boolean resizeable) {
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizeable ? TRUE : FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		
		GLFWVidMode v = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, v.redBits());
		GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, v.greenBits());
		GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, v.blueBits());
		GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, v.refreshRate());
		return new Vec2i(v.width(), v.height());
	}
	
	/** Sets the window's X/Y position to the given coordinates
	 * @param window The window to set position
	 * @param x The X coordinate to set the window to
	 * @param y The Y coordinate to set the window to
	 */
	public static void setWindowPos(long window, int x, int y) {
		GLFW.glfwSetWindowPos(window, x, y);
	}
	
	/**
	 * 
	 * @throws GLException Throws if the OpenGL fails to initialize. Will print stacktrace automatically.
	 */
	public static void initGL() throws GLException {
		if (!GLFW.glfwInit()) {
			throw Console.print(WarningType.FatalError, "Failed to initialize OpenGL!", new GLException(Reason.FAILED_TO_INIT_GL));
		}
	}
	
	/** True if the window should close, otherwise false */
	public static boolean shouldWindowClose(long window) {
		return GLFW.glfwWindowShouldClose(window);
	}
	
	public static void setVSync(boolean vsync) {
		GLFW.glfwSwapInterval(vsync ? 1 : 0);
	}
	
	public static void swapBuffers(long window) {
		GLFW.glfwSwapBuffers(window);
	}
	
	public static void enableWindowDecorations(long window) {
		GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, TRUE);
	}
	
	public static void disableWindowDecorations(long window) {
		GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, FALSE);
	}
	
	public static Vec2i getMonitorSize() {
		GLFWVidMode v = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		return new Vec2i(v.width(), v.height());
	}
	
	public static int getMonitorRefreshRate() {
		return GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).refreshRate();
	}
	
	public static void setWindowData(long window, int xPos, int yPos, int width, int height, int refreshRate) {
		GLFW.glfwSetWindowMonitor(window, NULL, xPos, yPos, width, height, refreshRate);
	}
	
	public static void setWindowData(long window, int xPos, int yPos, int width, int height) {
		setWindowData(window, xPos, yPos, width, height, getMonitorRefreshRate());
	}
	
	public static void setWindowData(long window, Vec2i pos, Vec2i size, int refreshRate) {
		setWindowData(window, pos.getX(), pos.getY(), size.getX(), size.getY(), refreshRate);
	}
	
	public static void setWindowData(long window, Vec2i pos, Vec2i size) {
		setWindowData(window, pos, size, getMonitorRefreshRate());
	}
	
	public static void setWindowData(long window, int xPos, int yPos, Vec2i size, int refreshRate) {
		setWindowData(window, xPos, yPos, size.getX(), size.getY(), refreshRate);
	}
	
	public static void setWindowData(long window, int xPos, int yPos, Vec2i size) {
		setWindowData(window, xPos, yPos, size, getMonitorRefreshRate());
	}
	
	public static void pollEvents() {
		GLFW.glfwPollEvents();
	}
}
