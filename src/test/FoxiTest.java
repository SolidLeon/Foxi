package test;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

import foxi.framework.Memory;
import foxi.framework.gfx.VRAMPainter;

public class FoxiTest {

	@Test
	public void testToMemoryAdress() {
		assertEquals(0, VRAMPainter.toMemoryAdress(0, 0, 0, 0));
	}
	@Test
	public void testToMemoryAdress2() {
		assertEquals(1, VRAMPainter.toMemoryAdress(1, 0, 0, 0));
	}
	@Test
	public void testToMemoryAdress3() {
		assertEquals(2, VRAMPainter.toMemoryAdress(2, 0, 0, 0));
	}
	@Test
	public void testToMemoryAdress4() {
		assertEquals(8, VRAMPainter.toMemoryAdress(0, 1, 0, 8));
	}
	@Test
	public void testToMemoryAdress5() {
		assertEquals(9, VRAMPainter.toMemoryAdress(1, 1, 0, 8));
	}
	@Test
	public void testToMemoryAdress6() {
		assertEquals(10, VRAMPainter.toMemoryAdress(1, 1, 1, 8));
	}
	@Test
	public void testToMemoryAdress7() {
		assertEquals(18, VRAMPainter.toMemoryAdress(1, 2, 1, 8));
	}

	@Test
	public void testGetColorAWTColorRed() {
		Color col = Color.RED;
		byte[] memory = {
				(byte) (col.getAlpha()),
				(byte) (col.getRed()),
				(byte) (col.getGreen()),
				(byte) (col.getBlue())
		};
		assertEquals(col, VRAMPainter.getColor(memory, 0));
	}
	@Test
	public void testGetColorAWTColorGreen() {
		Color col = Color.GREEN;
		byte[] memory = {
				(byte) (col.getAlpha()),
				(byte) (col.getRed()),
				(byte) (col.getGreen()),
				(byte) (col.getBlue())
		};
		assertEquals(col, VRAMPainter.getColor(memory, 0));
	}
	@Test
	public void testGetColorAWTColorBlue() {
		Color col = Color.BLUE;
		byte[] memory = {
				(byte) (col.getAlpha()),
				(byte) (col.getRed()),
				(byte) (col.getGreen()),
				(byte) (col.getBlue())
		};
		assertEquals(col, VRAMPainter.getColor(memory, 0));
	}
	@Test
	public void testGetColorAWTColorBlack() {
		Color col = Color.BLACK;
		byte[] memory = {
				(byte) (col.getAlpha()),
				(byte) (col.getRed()),
				(byte) (col.getGreen()),
				(byte) (col.getBlue())
		};
		assertEquals(col, VRAMPainter.getColor(memory, 0));
	}

	@Test
	public void testGetColorAWTColorWhite() {
		Color col = Color.WHITE;
		byte[] memory = {
				(byte) (col.getAlpha()),
				(byte) (col.getRed()),
				(byte) (col.getGreen()),
				(byte) (col.getBlue())
		};
		assertEquals(col, VRAMPainter.getColor(memory, 0));
	}
	@Test
	public void testGetColorRed() {
		Color col = Color.RED;
		byte[] memory = {
				(byte) 0xff,
				(byte) 0xff,
				(byte) 0x00,
				(byte) 0x00
		};
		assertEquals(col, VRAMPainter.getColor(memory, 0));
		assertEquals(new Color(255,0,0,255), VRAMPainter.getColor(memory, 0));
	}
	@Test
	public void testGetColorGreen() {
		Color col = Color.GREEN;
		byte[] memory = {
				(byte) 0xff,
				(byte) 0x00,
				(byte) 0xff,
				(byte) 0x00
		};
		assertEquals(col, VRAMPainter.getColor(memory, 0));
		assertEquals(new Color(0,255,0,255), VRAMPainter.getColor(memory, 0));
	}
	@Test
	public void testGetColorBlue() {
		Color col = Color.BLUE;
		byte[] memory = {
				(byte) 0xff,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0xff
		};
		assertEquals(col, VRAMPainter.getColor(memory, 0));
		assertEquals(new Color(0,0,255,255), VRAMPainter.getColor(memory, 0));
	}
	@Test
	public void testGetColorBlack() {
		Color col = Color.BLACK;
		byte[] memory = {
				(byte) 0xff,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00
		};
		assertEquals(col, VRAMPainter.getColor(memory, 0));
		assertEquals(new Color(0,0,0,255), VRAMPainter.getColor(memory, 0));
	}
	@Test
	public void testGetColorWhite() {
		Color col = Color.WHITE;
		byte[] memory = {
				(byte) 0xff,
				(byte) 0xff,
				(byte) 0xff,
				(byte) 0xff
		};
		assertEquals(col, VRAMPainter.getColor(memory, 0));
		assertEquals(new Color(255,255,255,255), VRAMPainter.getColor(memory, 0));
	}
	@Test
	public void testMemoryAllocation() {
		Memory memory = new Memory(64);
		assertEquals(64, memory.memory.length);
	}
	@Test
	public void testMemoryWrite() {
		Memory memory = new Memory(1);
		memory.write(0, 0xDEAD1234);
		assertEquals((byte)0x34, memory.memory[0]);
	}
	@Test
	public void testMemoryWriteW() {
		Memory memory = new Memory(2);
		memory.writeW(0, 0xDEAD1234);
		assertEquals((byte)0x12, memory.memory[0]);
		assertEquals((byte)0x34, memory.memory[1]);
	}
	@Test
	public void testMemoryWriteDW() {
		Memory memory = new Memory(4 * 3);
		memory.writeDW(0, 0xDEAD1234);
		assertEquals((byte)0xDE, memory.memory[0]);
		assertEquals((byte)0xAD, memory.memory[1]);
		assertEquals((byte)0x12, memory.memory[2]);
		assertEquals((byte)0x34, memory.memory[3]);
	}
	@Test
	public void testMemoryWriteRGBOffset() {
		Memory memory = new Memory(4 * 3);
		memory.writeDW(0, 0xffff0000);
		memory.writeDW(4, 0xff00ff00);
		memory.writeDW(8, 0xff0000ff);
		assertEquals((byte)0xff, memory.memory[0]);
		assertEquals((byte)0xff, memory.memory[1]);
		assertEquals((byte)0x00, memory.memory[2]);
		assertEquals((byte)0x00, memory.memory[3]);

		assertEquals((byte)0xff, memory.memory[4 + 0]);
		assertEquals((byte)0x00, memory.memory[4 + 1]);
		assertEquals((byte)0xff, memory.memory[4 + 2]);
		assertEquals((byte)0x00, memory.memory[4 + 3]);

		assertEquals((byte)0xff, memory.memory[8 + 0]);
		assertEquals((byte)0x00, memory.memory[8 + 1]);
		assertEquals((byte)0x00, memory.memory[8 + 2]);
		assertEquals((byte)0xff, memory.memory[8 + 3]);
	}
	

	@Test
	public void testMemoryRead() {
		Memory memory = new Memory(1);
		memory.write(0, 0xDEAD1234);
		assertEquals((byte)0x34, memory.read(0));
	}
	@Test
	public void testMemoryReadW() {
		Memory memory = new Memory(2);
		memory.writeW(0, 0xDEAD1234);
		assertEquals((short)0x1234, memory.readW(0));
	}
	@Test
	public void testMemoryReadDW() {
		Memory memory = new Memory(4);
		memory.writeDW(0, 0xDEAD1234);
		assertEquals(0xDEAD1234, memory.readDW(0));
	}
	@Test
	public void testMemoryReadRGBOffset() {
		Memory memory = new Memory(4 * 3);
		memory.writeDW(0, 0xffff0000);
		memory.writeDW(4, 0xff00ff00);
		memory.writeDW(8, 0xff0000ff);
		assertEquals((byte)0xff, memory.memory[0]);
		assertEquals((byte)0xff, memory.memory[1]);
		assertEquals((byte)0x00, memory.memory[2]);
		assertEquals((byte)0x00, memory.memory[3]);

		assertEquals((byte)0xff, memory.memory[4 + 0]);
		assertEquals((byte)0x00, memory.memory[4 + 1]);
		assertEquals((byte)0xff, memory.memory[4 + 2]);
		assertEquals((byte)0x00, memory.memory[4 + 3]);

		assertEquals((byte)0xff, memory.memory[8 + 0]);
		assertEquals((byte)0x00, memory.memory[8 + 1]);
		assertEquals((byte)0x00, memory.memory[8 + 2]);
		assertEquals((byte)0xff, memory.memory[8 + 3]);
		

		assertEquals((byte)0xff, memory.read(0));
		assertEquals((byte)0xff, memory.read(1));
		assertEquals((byte)0x00, memory.read(2));
		assertEquals((byte)0x00, memory.read(3));

		assertEquals((byte)0xff, memory.read(4 + 0));
		assertEquals((byte)0x00, memory.read(4 + 1));
		assertEquals((byte)0xff, memory.read(4 + 2));
		assertEquals((byte)0x00, memory.read(4 + 3));

		assertEquals((byte)0xff, memory.read(8 + 0));
		assertEquals((byte)0x00, memory.read(8 + 1));
		assertEquals((byte)0x00, memory.read(8 + 2));
		assertEquals((byte)0xff, memory.read(8 + 3));
	}
	
}
