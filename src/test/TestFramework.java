package test;

import static org.junit.Assert.*;

import org.junit.Test;

import foxi.framework.Framework;
import foxi.framework.ValueType;
import foxi.framework.command.AddRegisterCommand;
import foxi.framework.command.Command;
import foxi.framework.command.NopCommand;
import foxi.framework.command.PushCommand;
import foxi.framework.components.Memory;
import foxi.framework.components.Register;
import foxi.framework.components.Stack;

public class TestFramework {

	@Test
	public void testFrameworkRegisterStackMemoryCommandArray() {
		Framework fw = new Framework(new Register(), new Stack(1), new Memory(1), new Command[] { new NopCommand() });
		assertEquals("IP", fw.getInstructionPointerName());
		assertEquals("SP", fw.getStackPointerName());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFrameworkRegisterStackMemoryCommandArrayNull1() {
		Framework fw = new Framework(null, new Stack(1), new Memory(1), new Command[0]);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFrameworkRegisterStackMemoryCommandArrayNull2() {
		Framework fw = new Framework(new Register(), null, new Memory(1), new Command[0]);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFrameworkRegisterStackMemoryCommandArrayNull3() {
		Framework fw = new Framework(new Register(), new Stack(1), null, new Command[0]);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFrameworkRegisterStackMemoryCommandArrayNull4() {
		Framework fw = new Framework(new Register(), new Stack(1), new Memory(1), null);
	}

	@Test
	public void testFrameworkStringString() {
		Framework fw = new Framework("SP", "IP", new NopCommand());
		assertNotNull(fw.commands);
		assertNotNull(fw.register);
		assertNotNull(fw.memory);
		assertNotNull(fw.stack);
		assertEquals(64, fw.memory.size());
		assertEquals(true, fw.register.createOnAccess);
		assertEquals(8, fw.stack.size());
		assertEquals(1, fw.commands.length);
		assertEquals("SP", fw.getStackPointerName());
		assertEquals("IP", fw.getInstructionPointerName());
		assertEquals(8, fw.getStackPointer());
		assertEquals(0, fw.getInstructionPointer());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFrameworkStringStringNULL() {
		Framework fw = new Framework(null, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testFrameworkStringStringSPNULL() {
		Framework fw = new Framework(null, "IP");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFrameworkStringStringIPNULL() {
		Framework fw = new Framework("SP", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFrameworkStringStringSPEmpty() {
		Framework fw = new Framework("", "IP");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFrameworkStringStringIPEmpty() {
		Framework fw = new Framework("SP", "");
	}

	@Test
	public void testSetPointerNames() {
		Framework fw = new Framework("A", "B", new NopCommand());
		fw.setPointerNames("IP", "SP");
		assertEquals("IP", fw.getInstructionPointerName());
		assertEquals("SP", fw.getStackPointerName());
	}

	@Test
	public void testGetStackPointer() {
		Framework fw = new Framework("SP", "IP", new NopCommand());
		assertEquals(8, fw.getStackPointer());
	}

	@Test
	public void testSetStackPointer() {
		Framework fw = new Framework("SP", "IP", new NopCommand());
		fw.setStackPointer(3);
		assertEquals(3, fw.getStackPointer());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetStackPointerNegative() {
		Framework fw = new Framework("SP", "IP", new NopCommand());
		fw.setStackPointer(-2);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testSetStackPointerExceed() {
		Framework fw = new Framework("SP", "IP", new NopCommand());
		fw.setStackPointer(9);
		assertEquals(9, fw.getStackPointer());
	}
	@Test
	public void testSetStackPointerMin() {
		Framework fw = new Framework("SP", "IP", new NopCommand());
		fw.setStackPointer(-1);
		assertEquals(-1, fw.getStackPointer()); //-1 is OK, because after writing at stack[0], sp is -1. at read we increase sp to 0 and return the value at stack[0]
	}
	@Test
	public void testSetStackPointerMax() {
		Framework fw = new Framework("SP", "IP", new NopCommand());
		fw.setStackPointer(8); //Default StringString constructor creates a stack of size 8
		assertEquals(8, fw.getStackPointer());
	}

	@Test
	public void testGetInstructionPointer() {
		Framework fw = new Framework("SP", "IP", new NopCommand());
		assertEquals(0, fw.getInstructionPointer());
	}

	@Test
	public void testSetInstructionPointer() {
		Framework fw = new Framework("SP", "IP", new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand());
		fw.setInstructionPointer(5);
		assertEquals(5, fw.getInstructionPointer());
	}

	@Test
	public void testSetInstructionPointerMin() {
		Framework fw = new Framework("SP", "IP", new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand());
		fw.setInstructionPointer(0);
		assertEquals(0, fw.getInstructionPointer());
	}

	@Test
	public void testSetInstructionPointerMax() {
		Framework fw = new Framework("SP", "IP", new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand());
		fw.setInstructionPointer(7);
		assertEquals(7, fw.getInstructionPointer());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetInstructionPointerNegative() {
		Framework fw = new Framework("SP", "IP", new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand());
		fw.setInstructionPointer(-1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetInstructionPointerExceed() {
		Framework fw = new Framework("SP", "IP", new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand(), new NopCommand());
		fw.setInstructionPointer(8);
	}

	@Test
	public void testGetCommand() {
		Framework fw = new Framework("SP", "IP", new NopCommand(), new PushCommand("%AX", ValueType.REGISTER), new AddRegisterCommand("%AX", ValueType.REGISTER, "%AX", ValueType.REGISTER));
		assertEquals(fw.commands[1], fw.getCommand(1));
	}
	
	@Test
	public void testGetCommandMin() {
		Framework fw = new Framework("SP", "IP", new NopCommand(), new PushCommand("%AX", ValueType.REGISTER), new AddRegisterCommand("%AX", ValueType.REGISTER, "%AX", ValueType.REGISTER));
		assertEquals(fw.commands[0], fw.getCommand(0));
	}
	
	@Test
	public void testGetCommandMax() {
		Framework fw = new Framework("SP", "IP", new NopCommand(), new PushCommand("%AX", ValueType.REGISTER), new AddRegisterCommand("%AX", ValueType.REGISTER, "%AX", ValueType.REGISTER));
		assertEquals(fw.commands[2], fw.getCommand(2));
	}
	
	@Test
	public void testGetCommandNegative() {
		Framework fw = new Framework("SP", "IP", new NopCommand(), new PushCommand("%AX", ValueType.REGISTER), new AddRegisterCommand("%AX", ValueType.REGISTER, "%AX", ValueType.REGISTER));
		assertNull(fw.getCommand(-1));
	}
	
	@Test
	public void testGetCommandExceed() {
		Framework fw = new Framework("SP", "IP", new NopCommand(), new PushCommand("%AX", ValueType.REGISTER), new AddRegisterCommand("%AX", ValueType.REGISTER, "%AX", ValueType.REGISTER));
		assertNull(fw.getCommand(3));
	}
	
	@Test
	public void testGetCommandEmpty() {
		Framework fw = new Framework("SP", "IP", new NopCommand());
		fw.commands = new Command[0];
		assertNull(fw.getCommand(0));
	}

}
