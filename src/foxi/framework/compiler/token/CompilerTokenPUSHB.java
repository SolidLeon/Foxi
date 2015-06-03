package foxi.framework.compiler.token;

import java.util.List;

import foxi.framework.ValueType;
import foxi.framework.command.Command;
import foxi.framework.command.PushCommand;

public class CompilerTokenPUSHB extends CompilerToken {

	@Override
	public void compile(String[] words, List<Command> result) {
		result.add(new PushCommand(words[1], ValueType.VALUE));
	}

}
