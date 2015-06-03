package foxi.framework.command;

import foxi.framework.Framework;
import foxi.framework.ValueType;

public class PushCommand extends Command1Arg {

	public PushCommand(String value, ValueType type) {
		super(value, type);
	}

	@Override
	public void execute(Framework fw) {
		int sp = fw.getStackPointer();
		sp -= 1;
		fw.setStackPointer(sp);
		fw.stack.pushDirect(sp, getActual(fw));
	}
	
}