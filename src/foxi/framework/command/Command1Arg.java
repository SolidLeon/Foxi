package foxi.framework.command;

import foxi.framework.Framework;
import foxi.framework.ValueType;

public class Command1Arg extends Command {
	protected String value;
	protected ValueType type;
	
	public Command1Arg(String value, ValueType type) {
		super();
		this.value = value;
		this.type = type;
	}
	


	protected int getActual(Framework fw) {
		switch (type) {
		case REGISTER: 			return fw.register.get(value);
		case VALUE: 			return Integer.parseInt(value);
		case VALUE_IN_MEMORY: 	return fw.memory.read(Integer.parseInt(value));
		}
		throw new RuntimeException("Invalid value type");
	}
	
	@Override
	public String toString() {
		switch (type) {
		case REGISTER: 			return String.format("%s %%%s", super.toString(), value);
		case VALUE: 			return String.format("%s %04Xh", super.toString(), Integer.parseInt(value));
		case VALUE_IN_MEMORY: 	return String.format("%s (%04Xh)", super.toString(), Integer.parseInt(value));
		}
		return String.format("%s %s", super.toString(), value);
	}
	
}