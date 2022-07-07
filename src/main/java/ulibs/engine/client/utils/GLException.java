package main.java.ulibs.engine.client.utils;

public class GLException extends Exception {
	private static final long serialVersionUID = -2951449009292318484L;
	
	public GLException(Reason reason) {
		super(reason.print);
	}
	
	public enum Reason {
		FAILED_TO_INIT_GL("Failed to initialize OpenGL!"),
		FAILED_TO_INIT_WINDOW("Failed to initialize Window!"),
		NOT_SETUP_VERTEX_ARRAY("Tried to use a non initialized vertex array!"),
		NOT_SETUP_SHADER("Tried to use a non initialized shader!");
		
		private final String print;
		
		private Reason(String print) {
			this.print = print;
		}
	}
}
