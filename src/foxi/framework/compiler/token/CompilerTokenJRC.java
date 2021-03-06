package foxi.framework.compiler.token;

import java.util.List;

import foxi.framework.ValueType;
import foxi.framework.command.Command;
import foxi.framework.command.JumpRelativeCommand;

public class CompilerTokenJRC extends CompilerToken {

	@Override
	public void compile(String[] words, List<Command> result) {
		result.add(new JumpRelativeCommand(words[1], ValueType.VALUE_IN_MEMORY));
	}

}
