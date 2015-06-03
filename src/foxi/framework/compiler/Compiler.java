package foxi.framework.compiler;

import java.util.ArrayList;
import java.util.List;

import foxi.framework.ValueType;
import foxi.framework.command.AddRegisterCommand;
import foxi.framework.command.Command;
import foxi.framework.command.JumpRelativeCommand;
import foxi.framework.command.NopCommand;
import foxi.framework.command.PushCommand;
import foxi.framework.command.WriteMemoryCommand;
import foxi.framework.command.WriteRegisterCommand;

//COMPILER
public class Compiler {
	public Command[] compile(List<String[]> lines) {
		List<Command> result = new ArrayList<>();
		for (String[] words : lines) {
			if (words == null) continue;
			if (words.length > 0) {
				System.out.println("COMPILER:  Process '" + words[0] + "'");
				if ("NOP".equals(words[0])) result.add(new NopCommand());
				if ("MOVA".equals(words[0])) result.add(new WriteRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.REGISTER)); //MOVA %, %
				if ("MOVB".equals(words[0])) result.add(new WriteRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE));
				if ("MOVC".equals(words[0])) result.add(new WriteRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE_IN_MEMORY));
				if ("JRA".equals(words[0])) result.add(new JumpRelativeCommand(words[1], ValueType.REGISTER));
				if ("JRB".equals(words[0])) result.add(new JumpRelativeCommand(words[1], ValueType.VALUE));
				if ("JRC".equals(words[0])) result.add(new JumpRelativeCommand(words[1], ValueType.VALUE_IN_MEMORY));
				if ("PUSHA".equals(words[0])) result.add(new PushCommand(words[1], ValueType.REGISTER));
				if ("PUSHB".equals(words[0])) result.add(new PushCommand(words[1], ValueType.VALUE));
				if ("PUSHC".equals(words[0])) result.add(new PushCommand(words[1], ValueType.VALUE_IN_MEMORY));
				if ("WRITEA".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.REGISTER, words[2], ValueType.REGISTER));
				if ("WRITEB".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE));
				if ("WRITEC".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE_IN_MEMORY));
				if ("WRITED".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE, words[2], ValueType.REGISTER));
				if ("WRITEE".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE, words[2], ValueType.VALUE));
				if ("WRITEF".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE, words[2], ValueType.VALUE_IN_MEMORY));
				if ("WRITEG".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE_IN_MEMORY, words[2], ValueType.REGISTER));
				if ("WRITEH".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE_IN_MEMORY, words[2], ValueType.VALUE));
				if ("WRITEI".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE_IN_MEMORY, words[2], ValueType.VALUE_IN_MEMORY));
				if ("ADDA".equals(words[0])) result.add(new AddRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.REGISTER));
				if ("ADDB".equals(words[0])) result.add(new AddRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE));
				if ("ADDC".equals(words[0])) result.add(new AddRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE_IN_MEMORY));
			}
		}
		return result.toArray(new Command[0]);
	}
}