package foxi.framework.compiler.token;

import java.util.List;

import foxi.framework.ValueType;
import foxi.framework.command.AddRegisterCommand;
import foxi.framework.command.Command;

public class CompilerTokenADDC extends CompilerToken {

	@Override
	public void compile(String[] words, List<Command> result) {
		result.add(new AddRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE_IN_MEMORY));
	}

}
