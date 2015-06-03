package foxi.framework.command;

import foxi.framework.Framework;
import foxi.framework.ValueType;

public class Command2Arg extends Command {
	protected String arg1;
	protected ValueType type1;
	protected String arg2;
	protected ValueType type2;

	public Command2Arg(String arg1, ValueType type1, String arg2,
			ValueType type2) {
		super();
		this.arg1 = arg1;
		this.type1 = type1;
		this.arg2 = arg2;
		this.type2 = type2;
	}

	@Override
	public String toString() {
		switch (type2) {
		case REGISTER: 			return String.format("%s %%%s %%%s", super.toString(), arg1, arg2);
		case VALUE: 			return String.format("%s %%%s %04Xh", super.toString(), arg1, Integer.parseInt(arg2));
		case VALUE_IN_MEMORY: 	return String.format("%s %%%s (%04Xh)", super.toString(), arg1, Integer.parseInt(arg2));
		}
		return String.format("%s %s %s", super.toString(), arg1, arg2);
	}


	protected int getActual1(Framework fw) {
		int actual = 0;
		switch (type1) {
		case REGISTER: actual = fw.register.get(arg1);
			break;
		case VALUE: actual = Integer.parseInt(arg1);
			break;
		case VALUE_IN_MEMORY: actual = fw.memory.read(Integer.parseInt(arg1));
			break;
		}
		return actual;
	}

	protected int getActual2(Framework fw) {
		int actual = 0;
		switch (type2) {
		case REGISTER: actual = fw.register.get(arg2);
			break;
		case VALUE: actual = Integer.parseInt(arg2);
			break;
		case VALUE_IN_MEMORY: actual = fw.memory.read(Integer.parseInt(arg2));
			break;
		}
		return actual;
	}
	
}