package foxi.framework.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foxi.framework.ValueType;
import foxi.framework.command.AddRegisterCommand;
import foxi.framework.command.Command;
import foxi.framework.command.JumpRelativeCommand;
import foxi.framework.command.NopCommand;
import foxi.framework.command.PushCommand;
import foxi.framework.command.WriteMemoryCommand;
import foxi.framework.command.WriteRegisterCommand;
import foxi.framework.compiler.token.CompilerToken;
import foxi.framework.compiler.token.CompilerTokenNOP;

//COMPILER
public class Compiler {
	
	private Map<String, CompilerToken> tokens = new HashMap<>();
	
	public Command[] compile(List<String[]> lines) {
		List<Command> result = new ArrayList<>();
		for (String[] words : lines) {
			if (words == null) continue;
			if (words.length > 0) {
				System.out.println("COMPILER:  Process '" + words[0] + "'");
				CompilerToken token = tokens.get(words[0]);
				if (token != null) {
					//call token
					token.compile(words, result);
				} else {
					System.err.println("COMPILER:  unknown token " + words[0]);
				}
			}
		}
		return result.toArray(new Command[0]);
	}

	public void addToken(String name, CompilerToken token) {
		if (tokens.containsKey(name))
			throw new RuntimeException("Compiler token already exists for " + name);
		tokens.put(name, token);
	}
}