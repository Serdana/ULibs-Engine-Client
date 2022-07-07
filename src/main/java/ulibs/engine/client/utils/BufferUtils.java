package main.java.ulibs.engine.client.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/** A few utility methods for converting arrays into their respective buffers
 * @author -Unknown-
 */
public class BufferUtils {
	/** Creates a {@link ByteBuffer} from the given array
	 * @param array The array to turn into a {@link ByteBuffer}
	 * @return A {@link ByteBuffer} with the data from given array
	 */
	public static ByteBuffer createByteBuffer(byte[] array) {
		return ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder()).put(array).flip();
	}
	
	/** Creates a {@link FloatBuffer} from the given array
	 * @param array The array to turn into a {@link FloatBuffer}
	 * @return A {@link FloatBuffer} with the data from given array
	 */
	public static FloatBuffer createFloatBuffer(float[] array) {
		return ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer().put(array).flip();
	}
	
	/** Creates a {@link IntBuffer} from the given array
	 * @param array The array to turn into a {@link IntBuffer}
	 * @return A {@link IntBuffer} with the data from given array
	 */
	public static IntBuffer createIntBuffer(int[] array) {
		return ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer().put(array).flip();
	}
}
