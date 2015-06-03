package foxi.framework.command;

import foxi.framework.Framework;
import foxi.framework.ValueType;

public class AddRegisterCommand extends Command2Arg {
	public AddRegisterCommand(String arg1, ValueType type1, String arg2,
			ValueType type2) {
		super(arg1, type1, arg2, type2);
	}

	@Override
	public void execute(Framework fw) {
		int v = fw.register.get(arg1);
		v += getActual2(fw);
		fw.register.set(arg1, v);
	}
	
}