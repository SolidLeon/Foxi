package foxi.framework.compiler.token;

import java.util.List;

import foxi.framework.command.Command;

public abstract class CompilerToken {

	public abstract void compile(String[] words, List<Command> result);

}
