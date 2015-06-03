package foxi.framework;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

import javax.imageio.ImageIO;

public class VRAMHelper {
	public static int toPackedColorARGB(int a, int r, int g, int b) {
		return (a&0xff)<<24 | (r&0xff)<<16 | (g&0xff)<<8 | (b&0xff);
	}
	
	public static void loadImageIntoMemory(Memory mem, File imgFile) {
		try {
			BufferedImage img = ImageIO.read(imgFile);
			byte[] pixels = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
			
			System.out.println("PICTURE LOADED '"+imgFile.getAbsolutePath()+"'");
			System.out.println("  " + img.getWidth() + "x" + img.getHeight());
			System.out.println(VRAMHelper.getImageType(img));
			System.out.println(img.getColorModel());
			
			for (int y = 0; y < img.getHeight(); y++) {
				for (int x = 0; x < img.getWidth(); x++) {
					int argb = img.getRGB(x, y);
					mem.writeDW((x + y * img.getWidth()) * 4, argb);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getImageType(BufferedImage img) {
		switch (img.getType()) {
		case BufferedImage.TYPE_3BYTE_BGR:
			return ("  TYPE_3BYTE_BGR");
		case BufferedImage.TYPE_4BYTE_ABGR:
			return("  TYPE_4BYTE_ABGR");
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			return("  TYPE_4BYTE_ABGR_PRE");
		case BufferedImage.TYPE_BYTE_BINARY:
			return("  TYPE_BYTE_BINARY");
		case BufferedImage.TYPE_BYTE_GRAY:
			return("  TYPE_BYTE_GRAY");
		case BufferedImage.TYPE_BYTE_INDEXED:
			return("  TYPE_BYTE_INDEXED");
		case BufferedImage.TYPE_CUSTOM:
			return("  TYPE_CUSTOM");
		case BufferedImage.TYPE_INT_ARGB:
			return("  TYPE_INT_ARGB");
		case BufferedImage.TYPE_INT_ARGB_PRE:
			return("  TYPE_INT_ARGB_PRE");
		case BufferedImage.TYPE_INT_BGR:
			return("  TYPE_INT_BGR");
		case BufferedImage.TYPE_INT_RGB:
			return("  TYPE_INT_RGB");
		case BufferedImage.TYPE_USHORT_555_RGB:
			return("  TYPE_USHORT_555_RGB");
		case BufferedImage.TYPE_USHORT_565_RGB:
			return("  TYPE_USHORT_565_RGB");
		case BufferedImage.TYPE_USHORT_GRAY:
			return("  TYPE_USHORT_GRAY");
		default:
			return "  TYPE OTHER";
		}
	}
	public static void writeARGB(byte[] memory, int offset, int argb) {
		if (memory.length < offset + 4)
			throw new RuntimeException("memory not big enough");
		memory[offset + 0] = (byte) ((argb >> 24) & 0xff);
		memory[offset + 1] = (byte) ((argb >> 16) & 0xff);
		memory[offset + 2] = (byte) ((argb >>  8) & 0xff);
		memory[offset + 3] = (byte) ((argb >>  0) & 0xff);
		System.out.printf("%02X  %02X  %02X  %02X%n",
				memory[offset + 0],
				memory[offset + 1],
				memory[offset + 2],
				memory[offset + 3]);
	}
}