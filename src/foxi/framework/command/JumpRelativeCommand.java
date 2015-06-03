package foxi.framework.command;

import foxi.framework.Framework;
import foxi.framework.ValueType;

public class JumpRelativeCommand extends Command1Arg {
	public JumpRelativeCommand(String value, ValueType type) {
		super(value, type);
	}

	@Override
	public void execute(Framework fw) {
		int ip = fw.getInstructionPointer();
		ip += getActual(fw); //IP points to NEXT command, so if jump 1 it will just skip this next command
		fw.setInstructionPointer(ip);
	}
}