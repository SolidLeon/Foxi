package foxi.framework;

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
	public String stackPointer;
	public String instructionPointer;
	
	public Framework(Register register, Stack stack, Memory memory, Command[] commands) {
		super();
		this.register = register;
		this.stack = stack;
		this.memory = memory;
		this.commands = commands;
	}

	public Framework(String stackPointer, String instructionPointer) {
		super();
		this.stackPointer = stackPointer;
		this.instructionPointer = instructionPointer;
	}


	public void setPointerNames(String instructionPointer, String stackPointer) {
		this.instructionPointer = instructionPointer;
		this.stackPointer = stackPointer;
	}

	public int getStackPointer() {
		return register.get(stackPointer);
	}

	public void setStackPointer(int sp) {
		register.set(stackPointer, sp);
	}
	public int getInstructionPointer() {
		return register.get(instructionPointer);
	}

	public void setInstructionPointer(int ip) {
		register.set(instructionPointer, ip);
	}

	public Command getCommand(int i) {
		if (i < 0 || i >= commands.length) {
			return null;
		}
		return commands[i];
	}
}