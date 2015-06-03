package foxi.framework.compiler.token;

import java.util.List;

import foxi.framework.command.Command;
import foxi.framework.command.NopCommand;

public class CompilerTokenNOP extends CompilerToken {

	@Override
	public void compile(String[] words, List<Command> result) {
		result.add(new NopCommand());
	}

}
