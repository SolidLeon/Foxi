package foxi.framework;

import static org.junit.Assert.assertEquals;
import sun.java2d.pipe.SpanClipRenderer;
import foxi.framework.command.Command;
import foxi.framework.components.Memory;
import foxi.framework.components.Register;
import foxi.framework.components.Stack;

public class Framework {
	public Register register;
	public Stack stack;
	public Memory memory;
	public Command[] commands;
	private String stackPointer = "SP";
	private String instructionPointer = "IP";

	public Framework(Register register, Stack stack, Memory memory, Command[] commands, String stackPointer, String instructionPointer) {
		super();
		if (register == null)
			throw new IllegalArgumentException("register null");
		if (stack == null)
			throw new IllegalArgumentException("stack null");
		if (memory == null)
			throw new IllegalArgumentException("memory null");
		if (commands == null)
			throw new IllegalArgumentException("commands null");
		if (stackPointer == null)
			throw new IllegalArgumentException("stack pointer null");
		if (instructionPointer == null)
			throw new IllegalArgumentException("instruction pointer null");
		if (commands.length == 0)
			throw new IllegalArgumentException("No commands passed");
		this.register = register;
		this.stack = stack;
		this.memory = memory;
		this.commands = commands;
		this.stackPointer = stackPointer;
		this.instructionPointer = instructionPointer;

		register.newRegister(instructionPointer, 0);
		register.newRegister(stackPointer, stack.size());
	}
	
	public Framework(Register register, Stack stack, Memory memory, Command[] commands) {
		this(register, stack, memory, commands, "SP", "IP");
	}

	public Framework(String stackPointer, String instructionPointer, Command... commands) {
		this(new Register(true), new Stack(8), new Memory(64), commands, stackPointer, instructionPointer);

	}


	public void setPointerNames(String instructionPointer, String stackPointer) {
		this.instructionPointer = instructionPointer;
		this.stackPointer = stackPointer;
	}

	public int getStackPointer() {
		return register.get(stackPointer);
	}

	public void setStackPointer(int sp) {
		if (sp < -1 || sp > stack.size())
			throw new IllegalArgumentException("Access violation: invalid stack pointer, " + sp);
		register.set(stackPointer, sp);
	}
	public int getInstructionPointer() {
		return register.get(instructionPointer);
	}

	public void setInstructionPointer(int ip) {
		if (ip < 0 || ip >= commands.length)
			throw new IllegalArgumentException("Access violation: invalid instruction pointer, " + ip);
		register.set(instructionPointer, ip);
	}

	public Command getCommand(int i) {
		if (i < 0 || i >= commands.length) {
			return null;
		}
		return commands[i];
	}

	public String getInstructionPointerName() {
		return instructionPointer;
	}

	public String getStackPointerName() {
		return stackPointer;
	}
}