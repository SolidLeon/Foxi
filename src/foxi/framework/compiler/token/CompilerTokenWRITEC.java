package foxi.framework.compiler.token;

import java.util.List;

import foxi.framework.ValueType;
import foxi.framework.command.Command;
import foxi.framework.command.WriteMemoryCommand;

public class CompilerTokenWRITEC extends CompilerToken {

	@Override
	public void compile(String[] words, List<Command> result) {
		result.add(new WriteMemoryCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE_IN_MEMORY));
	}

}
