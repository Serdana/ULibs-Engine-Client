module ulibs.engine.client {
	exports main.java.ulibs.engine.client;
	exports main.java.ulibs.engine.client.input;
	exports main.java.ulibs.engine.client.math;
	exports main.java.ulibs.engine.client.utils;
	exports main.java.ulibs.engine.client.helpers;
	exports main.java.ulibs.engine.client.gl;
	exports main.java.ulibs.engine.client.gl.geometry;
	exports main.java.ulibs.engine.client.init;
	exports main.java.ulibs.engine.client.render;
	
	requires com.google.gson;
	requires java.desktop;
	requires org.lwjgl;
	requires org.lwjgl.glfw;
	requires org.lwjgl.opengl;
	requires transitive ulibs.common;
	requires ulibs.engine.common;
}