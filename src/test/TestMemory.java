package test;

import static org.junit.Assert.*;

import org.junit.Test;

import foxi.framework.components.Memory;

public class TestMemory {

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

	@Test
	public void testMemoryFill() {
		Memory memory = new Memory(8);
		memory.fill(0, 8, 0xDE);
		for (int i = 0; i < memory.size(); i++)
			assertEquals((byte) 0xDE, memory.read(i));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMemoryFillInvalidOffsetMin() {
		Memory memory = new Memory(8);
		memory.fill(-1, 8, 0xDE);
		for (int i = 0; i < memory.size(); i++)
			assertEquals(0xDE, memory.read(i));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMemoryFillInvalidOffsetMax() {
		Memory memory = new Memory(8);
		memory.fill(8, 8, 0xDE);
		for (int i = 0; i < memory.size(); i++)
			assertEquals(0xDE, memory.read(i));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMemoryFillInvalidLenMin() {
		Memory memory = new Memory(8);
		memory.fill(0, -1, 0xDE);
		for (int i = 0; i < memory.size(); i++)
			assertEquals(0xDE, memory.read(i));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testMemoryFillInvalidLenMax() {
		Memory memory = new Memory(8);
		memory.fill(0, 12, 0xDE);
		for (int i = 0; i < memory.size(); i++)
			assertEquals(0xDE, memory.read(i));
	}
	@Test(expected=IllegalArgumentException.class)
	public void testMemoryFillInvalidoffsetLen() {
		Memory memory = new Memory(8);
		memory.fill(7, 2, 0xDE);
		for (int i = 0; i < memory.size(); i++)
			assertEquals(0xDE, memory.read(i));
	}
	
	@Test
	public void testMemoryFillWrapper() {
		Memory memory = new Memory(8);
		memory.fill(0, 0xDE);
		for (int i = 0; i < memory.size(); i++)
			assertEquals((byte)0xDE, memory.read(i));
	}

}
