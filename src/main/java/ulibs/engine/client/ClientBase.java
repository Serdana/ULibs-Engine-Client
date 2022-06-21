package main.java.ulibs.engine.client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWWindowPosCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import main.java.ulibs.common.math.Vec2i;
import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.engine.client.gl.Shader;
import main.java.ulibs.engine.client.helpers.GLH;
import main.java.ulibs.engine.client.init.Shaders;
import main.java.ulibs.engine.client.input.EnumKeyInput;
import main.java.ulibs.engine.client.input.EnumMouseInput;
import main.java.ulibs.engine.client.input.IInputHandler;
import main.java.ulibs.engine.client.input.IScrollHandler;
import main.java.ulibs.engine.client.input.Inputs;
import main.java.ulibs.engine.client.render.EnumScreenTearFix;
import main.java.ulibs.engine.client.render.IRenderer;
import main.java.ulibs.engine.client.render.ResizeHandler;
import main.java.ulibs.engine.client.render.ScreenLoading;
import main.java.ulibs.engine.client.utils.GLException;
import main.java.ulibs.engine.common.CommonBase;
import main.java.ulibs.engine.common.config.ConfigH;
import main.java.ulibs.engine.common.utils.ITickable;

public abstract class ClientBase extends CommonBase {
	/** A {@link Vec2i} for the current mouse position */
	public static final Vec2i MOUSE_POS = new Vec2i();
	
	private final IInputHandler<EnumKeyInput> keyHandler;
	private final IInputHandler<EnumMouseInput> mouseHandler;
	private final IScrollHandler scrollHandler;
	
	protected static long window;
	private static int lastWidth, lastHeight, windowX, windowY, lastWindowX, lastWindowY;
	private static boolean isFullscreen;
	private static int defaultWidth, defaultHeight;
	
	private ScreenLoading loadingScreen;
	
	private static EnumScreenTearFix screenFix = EnumScreenTearFix.vsync;
	
	private final List<IRenderer> renderers = new ArrayList<IRenderer>();
	private final Supplier<List<Shader>> shadersToSetup;
	private static ResizeHandler resizeHandler;
	
	protected ClientBase(String title, String internalTitle, int defaultWidth, int defaultHeight, boolean isDebug, int logCount, WarningType[] warnings,
			Supplier<List<Shader>> shadersToSetup) {
		super(title, internalTitle, isDebug, logCount, warnings);
		this.shadersToSetup = shadersToSetup;
		
		this.keyHandler = setKeyHandler();
		this.mouseHandler = setMouseHandler();
		this.scrollHandler = setScrollHandler();
		
		ClientBase.defaultWidth = defaultWidth;
		ClientBase.defaultHeight = defaultHeight;
		ClientBase.resizeHandler = setResizeHandler();
		
		Thread.currentThread().setName("Client");
	}
	
	@Override
	protected void preInitWrap() {
		internalRender();
		super.preInitWrap();
	}
	
	@Override
	protected final void initWrap() {
		Console.print(WarningType.Info, "Initialization started...");
		loadingState = LoadingState.INIT;
		init();
		loadingState = LoadingState.CONFIG;
		ConfigH.loadConfigs();
		loadingState = LoadingState.RENDERERS;
		rendererSetup();
		Console.print(WarningType.Info, "Initialization finished!");
	}
	
	@Override
	protected final boolean preRun() {
		glSetup();
		loadingScreen = new ScreenLoading();
		loadingScreen.setupGL();
		return true;
	}
	
	@Override
	protected final void internalRender() {
		int err = GLH.getError();
		if (GLH.isError(err)) {
			Console.print(WarningType.Error, "OpenGL Error: " + err);
		}
		
		GLH.pollEvents();
		GLH.clearColorDepthBuffer();
		
		if (!isLoading()) {
			render();
		} else {
			renderLoadingScreen();
		}
		
		GLH.swapBuffers(window);
	}
	
	private void rendererSetup() {
		for (IRenderer r : renderers) {
			r.setupGL();
		}
	}
	
	@Override
	protected void tick() {
		for (IRenderer r : renderers) {
			if (r instanceof ITickable) {
				((ITickable) r).tick();
			}
		}
	}
	
	protected void render() {
		for (IRenderer r : renderers) {
			r.renderAll();
		}
	}
	
	/** Adds an {@link IRenderer} to a list allowing automatic handling
	 * @param r The renderer to add
	 */
	protected final void addRenderer(IRenderer r) {
		if (loadingState != LoadingState.INIT) {
			Console.print(WarningType.Warning, "Added a renderer before or after #init. This could be bad?");
		}
		
		renderers.add(r);
		Console.print(WarningType.RegisterDebug, "Registered '" + r.getClass().getSimpleName() + "' as a renderer!");
	}
	
	/** @return The list of {@link IRenderer}s */
	protected final List<IRenderer> getRenderers() {
		return renderers;
	}
	
	protected void renderLoadingScreen() {
		loadingScreen.renderAll();
	}
	
	private final void glSetup() {
		Console.print(WarningType.Info, "OpenGL setup started...");
		
		try {
			GLH.initGL();
		} catch (GLException e) {
			return;
		}
		
		Vec2i monSize = GLH.setWindowHints(true);
		
		try {
			window = GLH.createWindow(title, defaultWidth, defaultHeight);
		} catch (GLException e) {
			return;
		}
		
		windowX = monSize.getX() / 2 - defaultWidth / 2;
		windowY = monSize.getY() / 2 - defaultHeight / 2;
		
		GLH.setWindowPos(window, windowX, windowY);
		
		if (getKeyHandler() != null) {
			GLFW.glfwSetKeyCallback(window, new GLFWKeyCallbackI() {
				@Override
				public void invoke(long window, int key, int scancode, int action, int mods) {
					if (isLoading()) {
						return;
					}
					
					switch (action) {
						case GLFW.GLFW_PRESS:
							getKeyHandler().onPress(Inputs.getKeyFromInt(key));
							break;
						case GLFW.GLFW_RELEASE:
							getKeyHandler().onRelease(Inputs.getKeyFromInt(key));
							break;
						case GLFW.GLFW_REPEAT:
							getKeyHandler().onRepeat(Inputs.getKeyFromInt(key));
							break;
					}
				}
			});
		}
		
		if (getMouseHandler() != null) {
			GLFW.glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallbackI() {
				@Override
				public void invoke(long window, int button, int action, int mods) {
					if (isLoading()) {
						return;
					}
					
					switch (action) {
						case GLFW.GLFW_PRESS:
							getMouseHandler().onPress(Inputs.getMouseFromInt(button));
							break;
						case GLFW.GLFW_RELEASE:
							getMouseHandler().onRelease(Inputs.getMouseFromInt(button));
							break;
						case GLFW.GLFW_REPEAT:
							getMouseHandler().onRepeat(Inputs.getMouseFromInt(button));
							break;
					}
				}
			});
		}
		
		if (getScrollHandler() != null) {
			GLFW.glfwSetScrollCallback(window, new GLFWScrollCallbackI() {
				@Override
				public void invoke(long window, double xoffset, double yoffset) {
					if (isLoading()) {
						return;
					}
					
					if (yoffset == 1) {
						getScrollHandler().onScrollUp();
					} else {
						getScrollHandler().onScrollDown();
					}
				}
			});
		}
		
		GLFW.glfwSetCursorPosCallback(window, new GLFWCursorPosCallbackI() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				if (isLoading()) {
					return;
				}
				
				onMouseMoved();
				MOUSE_POS.set(resizeHandler.setMousePos((int) Math.round(xpos), (int) Math.round(ypos)));
			}
		});
		
		GLFW.glfwSetWindowPosCallback(window, new GLFWWindowPosCallbackI() {
			@Override
			public void invoke(long window, int xpos, int ypos) {
				windowX = xpos;
				windowY = ypos;
			}
		});
		
		GLFW.glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallbackI() {
			@Override
			public void invoke(long window, int width, int height) {
				resizeHandler.onResize(width, height);
				
				for (Shader s : Shaders.getAll()) {
					s.bind();
					s.onResize();
				}
				GLH.unbindShader();
				for (IRenderer r : renderers) {
					r.onResize();
				}
			}
		});
		
		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwShowWindow(window);
		
		GLH.createCapabilities();
		
		GLH.setVSync(screenFix == EnumScreenTearFix.vsync);
		GLH.clearColor(new Color(20, 20, 20, 1));
		GLH.enableDepth();
		GLH.enableBlend();
		GLH.setActiveTexture0();
		
		if (shadersToSetup != null) {
			List<Shader> list = shadersToSetup.get();
			if (list != null) {
				Shaders.getAll().addAll(list);
			}
		}
		Shaders.registerAll();
		
		Console.print(WarningType.Info, "OpenGL setup finished! Running OpenGL version: " + GLH.getVersion() + "!");
	}
	
	public static final void toggleFullScreen() {
		Console.print(WarningType.Debug, "Toggling Fullscreen!");
		isFullscreen = !isFullscreen;
		refreshFullscreen();
	}
	
	private static final void refreshFullscreen() {
		if (isFullscreen) {
			int viewportX = resizeHandler.getViewportX(), viewportY = resizeHandler.getViewportY();
			if (resizeHandler.isViewportCentered()) {
				viewportX *= 2;
				viewportY *= 2;
			}
			
			lastWidth = viewportX + resizeHandler.getViewportW();
			lastHeight = viewportY + resizeHandler.getViewportH();
			lastWindowX = windowX;
			lastWindowY = windowY;
			
			GLH.disableWindowDecorations(window);
			switch (screenFix) {
				case experimental:
					GLH.setVSync(false);
					GLH.setWindowData(window, 0, -1, GLH.getMonitorSize().addY(2));
					break;
				case off:
					GLH.setVSync(false);
					GLH.setWindowData(window, 0, 0, GLH.getMonitorSize());
					break;
				case vsync:
					GLH.setVSync(true);
					GLH.setWindowData(window, 0, 0, GLH.getMonitorSize());
					break;
			}
		} else {
			GLH.setVSync(screenFix == EnumScreenTearFix.vsync);
			GLH.enableWindowDecorations(window);
			GLH.setWindowData(window, lastWindowX, lastWindowY, lastWidth, lastHeight);
		}
	}
	
	protected void setScreenFixType(EnumScreenTearFix screenFix) {
		ClientBase.screenFix = screenFix;
	}
	
	protected void onMouseMoved() {
		
	}
	
	@Override
	protected Gson setupGson(GsonBuilder builder) {
		return builder.setPrettyPrinting().create();
	}
	
	protected abstract IInputHandler<EnumKeyInput> setKeyHandler();
	
	protected abstract IInputHandler<EnumMouseInput> setMouseHandler();
	
	protected abstract IScrollHandler setScrollHandler();
	
	protected abstract ResizeHandler setResizeHandler();
	
	public final IInputHandler<EnumKeyInput> getKeyHandler() {
		return keyHandler;
	}
	
	public final IInputHandler<EnumMouseInput> getMouseHandler() {
		return mouseHandler;
	}
	
	public final IScrollHandler getScrollHandler() {
		return scrollHandler;
	}
	
	@Override
	protected final boolean shouldClose() {
		return super.shouldClose() || GLH.shouldWindowClose(window);
	}
	
	public static int getDefaultWidth() {
		return defaultWidth;
	}
	
	public static int getDefaultHeight() {
		return defaultHeight;
	}
	
	public static ResizeHandler getResizeHandler() {
		return resizeHandler;
	}
}
