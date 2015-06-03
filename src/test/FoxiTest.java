package test;

import static org.junit.Assert.*;

import org.junit.Test;

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

}
