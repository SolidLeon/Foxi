package foxi.framework.gfx;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * Takes a memory as VRAM.
 * Memory pixel data should be stored:
 * ARGB
 * Each channel is one byte.
 * The order is left-to-right, top-to-bottom.
 * So each pixel row should be following.
 * Example:
 * Following image should be drawn:
 * {0,0} {1,0}
 * {0,1} {1,1}
 * 
 * In memory this should be
 * {0,0} {1,0} {0,1} {1,1}
 * ARGB  ARGB  ARGB  ARGB
 * 
 * So we would need 16 bytes for this image to be drawn
 * 
 * @author MMA
 *
 */
public class Display extends Canvas {
	
	/** memory from where to draw */
	private byte[] memory;
	/** offset in memory where VRAM begins */
	private int memoryOffset;
	/** size (in pixel) each pixel should be drawn with */
	private int scale = 2;
	private VRAMPainter painter = new VRAMPainter();
	private BufferedImage img;
	
	public Display(byte[] memory, int width, int height, int memoryOffset, int scale) {
		super();
		this.memory = memory;
		this.memoryOffset = memoryOffset;
		this.scale = scale;
		
		System.out.printf("DISPLAY created, memory size=%d,  w=%d,  h=%d,  off=%d,  scale=%d%n", memory.length, width, height, memoryOffset, scale);
		
		setPreferredSize(new Dimension(width * scale, height * scale));
		
		if (memoryOffset < 0 || memoryOffset >= memory.length)
			throw new RuntimeException("Memory offset out-of-bounds, was " + memoryOffset + " must be within [0," + memory.length + "[");
		
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	public void update() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}
		
		// Draw to buffered image, having the right TYPE (ARGB)
		// This section is tested and works, but only outside ... why?
		Graphics g2 = img.createGraphics();
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());
		painter.paint(g2, memory, memoryOffset, img.getWidth(), img.getHeight());
		g2.dispose();
		
		// Draw image to back buffer, centered and scaled
		Graphics g = bs.getDrawGraphics();
		g.fillRect(0, 0, getWidth(), getHeight());

		int xo = (int) ((getWidth() - img.getWidth() * scale) / 2f);
		int yo = (int) ((getHeight() - img.getHeight() * scale) / 2f);
		g.translate(xo, yo);
		
		g.drawImage(img, 0, 0, img.getWidth() * scale, img.getHeight() * scale, null);
		
		g.translate(-xo, -yo);
		g.dispose();
		bs.show();
	}
	

}