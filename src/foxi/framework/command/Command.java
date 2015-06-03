package foxi.framework.command;

import foxi.framework.Framework;

// COMMANDS
public class Command {
	public void execute(Framework fw) {
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}