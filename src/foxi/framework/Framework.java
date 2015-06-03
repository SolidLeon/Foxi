package foxi.framework;

import foxi.framework.command.Command;

public class Framework {
	public Register register;
	public Stack stack;
	public Memory memory;
	public Command[] commands;
	
	public Framework(Register register, Stack stack, Memory memory, Command[] commands) {
		super();
		this.register = register;
		this.stack = stack;
		this.memory = memory;
		this.commands = commands;
	}

	public void initialize() {
		register.newRegister("AX", 0x1234);
		register.newRegister("BX", 0);
		register.newRegister("CX", 0);
		register.newRegister("DX", 0);
		register.newRegister("IP", 0);
		register.newRegister("SP", stack.values.length);
	}

	public int getStackPointer() {
		return register.get("SP");
	}

	public void setStackPointer(int sp) {
		register.set("SP", sp);
	}
	public int getInstructionPointer() {
		return register.get("IP");
	}

	public void setInstructionPointer(int ip) {
		register.set("IP", ip);
	}

	public Command getCommand(int i) {
		if (i < 0 || i >= commands.length) {
			return null;
		}
		return commands[i];
	}
}