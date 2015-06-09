package test;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

import foxi.framework.gfx.VRAMPainter;

public class TestVRAMPainter {


	@Test
	public void testToMemoryAdress() {
		assertEquals(0, VRAMPainter.toMemoryAdress(0, 0, 0, 0));
	}
	@Test
	public void testToMemoryAdress2() {
		assertEquals(4, VRAMPainter.toMemoryAdress(1, 0, 0, 0));
	}
	@Test
	public void testToMemoryAdress3() {
		assertEquals(8, VRAMPainter.toMemoryAdress(2, 0, 0, 0));
	}
	@Test
	public void testToMemoryAdress4() {
		assertEquals(32, VRAMPainter.toMemoryAdress(0, 1, 0, 8));
	}
	@Test
	public void testToMemoryAdress5() {
		assertEquals(36, VRAMPainter.toMemoryAdress(1, 1, 0, 8));
	}
	@Test
	public void testToMemoryAdress6() {
		assertEquals(40, VRAMPainter.toMemoryAdress(1, 1, 1, 8));
	}
	@Test
	public void testToMemoryAdress7() {
		assertEquals(72, VRAMPainter.toMemoryAdress(1, 2, 1, 8));
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
}
