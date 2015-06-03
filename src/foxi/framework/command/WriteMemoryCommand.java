package foxi.framework.command;

import foxi.framework.Framework;
import foxi.framework.ValueType;

public class WriteMemoryCommand extends Command2Arg {
	public WriteMemoryCommand(String arg1, ValueType type1, String arg2,
			ValueType type2) {
		super(arg1, type1, arg2, type2);
	}

	@Override
	public void execute(Framework fw) {
		fw.memory.write(getActual1(fw), getActual2(fw));
	}
	
}