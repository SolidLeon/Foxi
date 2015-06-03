package foxi.framework.gfx;

import java.awt.Color;
import java.awt.Graphics;

public class VRAMPainter {
	public void paint(Graphics g, byte[] memory, int offset, int width, int height) {
		outer: for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int memoryAdress = toMemoryAdress(x, y, offset, width);
				//We reached the end of our VRAM, so end here
				if (memoryAdress + 4 >= memory.length)
					break outer;
				g.setColor(getColor(memory, memoryAdress));
				g.fillRect(x, y, 1, 1);
			}
		}
	}
	
	public static int toMemoryAdress(int x, int y, int offset, int width) {
		return offset + x + y * width;
	}
	
	/**
	 * Creates a color from the 4 bytes at 
	 * A  offset
	 * R  offset + 1
	 * G  offset + 2
	 * B  offset + 3
	 * @param memory
	 * @param offset
	 * @return Color
	 */
	public static Color getColor(byte[] memory, int offset) {
		int a = memory[offset + 0] & 0xff;
		int r = memory[offset + 1] & 0xff;
		int g = memory[offset + 2] & 0xff;
		int b = memory[offset + 3] & 0xff;
		return new Color(r, g, b, a);
	}
}