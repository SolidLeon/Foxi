package foxi.framework.command;

import foxi.framework.Framework;
import foxi.framework.ValueType;

public class WriteRegisterCommand extends Command2Arg {
	public WriteRegisterCommand(String arg1, ValueType type1, String arg2,
			ValueType type2) {
		super(arg1, type1, arg2, type2);
	}

	@Override
	public void execute(Framework fw) {
		fw.register.set(arg1, getActual2(fw));
	}
	
}