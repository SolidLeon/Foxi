package foxi.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FoxiMain2 {
	public static void main(String[] args) {
		String[] commands = {
			";This is a comment",
			"NOP",
			"MOV %AX 5h",
			"JR (0h)",
			";JR 1",
			"MOV %AX 1",
			"JR %AX",
			"MOV %AX ffh",
			"MOV %AX DEADh",
			"MOV %AX %IP",
			"PUSH %IP",
			"POP %AX"
		};
		
		Lexer lexer = new Lexer();
		Compiler compiler = new Compiler();
		Framework fw = new Framework(new Register(), new Stack(32), new Memory(32), compiler.compile(lexer.lex(Arrays.asList(commands))));
		fw.register.newRegister("AX", 0x1234);
		fw.register.newRegister("IP", 0);
		fw.register.newRegister("SP", fw.stack.values.length);
		
		Thread th = new Thread(new FrameworkRunner(fw, FoxiMain2::next));
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("=== REGISTER ===");
		for (Entry<String, Integer> entry : fw.register.registers.entrySet()) {
			System.out.printf("%-4s: %04d (%04Xh)%n", entry.getKey(), entry.getValue(), entry.getValue());
		}
		System.out.println("=== STACK ===");
		for (int i = fw.stack.values.length - 1; i >= fw.register.get("SP"); i--) {
			System.out.printf("%04d:  %04d (%04Xh)%n", i, fw.stack.values[i], fw.stack.values[i]);
		}
	}
	
	static Command next(Framework fw) {
		int ip = fw.register.get("IP");
		fw.register.set("IP", ip + 1);
		return fw.getCommand(ip);
		
	}
}

// LEXER
enum ValueType {
	REGISTER,
	VALUE,
	VALUE_IN_MEMORY,
}
class Lexer {
	public List<String[]> lex(List<String> lines) {
		List<String[]> result = new ArrayList<>();
		System.out.println("LEXER START");
		for (String line : lines) {
			if (line == null) continue;
			line = line.trim();
			if (line.isEmpty()) continue;
			if (line.charAt(0) == ';') continue;
			System.out.println("LEXER: Process '" + line + "'");
			String[] words = line.split(" ");
			for (int i = 0; i < words.length; i++)
				words[i] = words[i].trim();
			if (words.length > 0) {
				if ("NOP".equals(words[0])) result.add(new String[] {"NOP"});
				if ("MOV".equals(words[0])) {
					ValueType a = getType(words[1]);
					ValueType b = getType(words[2]);
					// a must be a register
					if (a == ValueType.REGISTER) {
						switch (b) {
						case REGISTER: result.add(new String[] {"MOVA", stripRegisterPercent(words[1]), stripRegisterPercent(words[2])});
							break;
						case VALUE: result.add(new String[] {"MOVB", stripRegisterPercent(words[1]),  "" + toInt(words[2])});
							break;
						case VALUE_IN_MEMORY: result.add(new String[] {"MOVC", stripRegisterPercent(words[1]),  "" + toInt(stripMemoryParenthesis(words[2]))});
							break;
						}
					}
				}
				if ("JR".equals(words[0])) {
					ValueType a = getType(words[1]);
					switch (a) {
					case REGISTER: result.add(new String[] {"JRA", stripRegisterPercent(words[1])});
						break;
					case VALUE: result.add(new String[] {"JRB", "" + toInt(words[1])});
						break;
					case VALUE_IN_MEMORY: result.add(new String[] {"JRC", "" + toInt(stripMemoryParenthesis(words[1]))});
						break;
					}
				}
				if ("PUSH".equals(words[0])) {
					ValueType a = getType(words[1]);
					switch (a) {
					case REGISTER: result.add(new String[] {"PUSHA", stripRegisterPercent(words[1])});
						break;
					case VALUE: result.add(new String[] {"PUSHB", "" + toInt(words[1])});
						break;
					case VALUE_IN_MEMORY: result.add(new String[] {"PUSHC", "" + toInt(stripMemoryParenthesis(words[1]))});
						break;
					}
				}
			}
		}
		System.out.println("LEXER END");
		return result;
	}
	
	private String stripRegisterPercent(String string) {
		return string.substring(1);
	}
	private String stripMemoryParenthesis(String string) {
		return string.substring(1, string.length() - 2);
	}

	private ValueType getType(String s) {
		if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')')
			return ValueType.VALUE_IN_MEMORY;
		if (s.charAt(0) == '%')
			return ValueType.REGISTER;
		return ValueType.VALUE;
	}
	
	private int toInt(String s) {
		int radix = 10;
		if (s.charAt(s.length() - 1) == 'h') {
			s = s.substring(0, s.length() - 1);
			radix = 16;
		}
		return Integer.parseInt(s, radix);
	}
}

//COMPILER
class Compiler {
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
			}
		}
		return result.toArray(new Command[0]);
	}
}

// COMMANDS
class Command {
	public void execute(Framework fw) {
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}

class Command1Arg extends Command {
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
class Command2Arg extends Command {
	protected String arg1;
	protected ValueType type1;
	protected String arg2;
	protected ValueType type2;

	public Command2Arg(String arg1, ValueType type1, String arg2,
			ValueType type2) {
		super();
		this.arg1 = arg1;
		this.type1 = type1;
		this.arg2 = arg2;
		this.type2 = type2;
	}

	@Override
	public String toString() {
		switch (type2) {
		case REGISTER: 			return String.format("%s %%%s %%%s", super.toString(), arg1, arg2);
		case VALUE: 			return String.format("%s %%%s %04Xh", super.toString(), arg1, Integer.parseInt(arg2));
		case VALUE_IN_MEMORY: 	return String.format("%s %%%s (%04Xh)", super.toString(), arg1, Integer.parseInt(arg2));
		}
		return String.format("%s %s %s", super.toString(), arg1, arg2);
	}


	protected int getActual1(Framework fw) {
		int actual = 0;
		switch (type1) {
		case REGISTER: actual = fw.register.get(arg1);
			break;
		case VALUE: actual = Integer.parseInt(arg1);
			break;
		case VALUE_IN_MEMORY: actual = fw.memory.read(Integer.parseInt(arg1));
			break;
		}
		return actual;
	}

	protected int getActual2(Framework fw) {
		int actual = 0;
		switch (type2) {
		case REGISTER: actual = fw.register.get(arg2);
			break;
		case VALUE: actual = Integer.parseInt(arg2);
			break;
		case VALUE_IN_MEMORY: actual = fw.memory.read(Integer.parseInt(arg2));
			break;
		}
		return actual;
	}
	
}

class NopCommand extends Command {
}

class PushCommand extends Command1Arg {

	public PushCommand(String value, ValueType type) {
		super(value, type);
	}

	@Override
	public void execute(Framework fw) {
		int sp = fw.register.get("SP");
		sp -= 1;
		fw.register.set("SP", sp);
		fw.stack.pushDirect(sp, getActual(fw));
	}
	
}

class WriteRegisterCommand extends Command2Arg {
	public WriteRegisterCommand(String arg1, ValueType type1, String arg2,
			ValueType type2) {
		super(arg1, type1, arg2, type2);
	}

	@Override
	public void execute(Framework fw) {
		fw.register.set(arg1, getActual2(fw));
	}
	
}
class JumpRelativeCommand extends Command1Arg {
	public JumpRelativeCommand(String value, ValueType type) {
		super(value, type);
	}

	@Override
	public void execute(Framework fw) {
		int ip = fw.register.get("IP");
		ip += getActual(fw); //IP points to NEXT command, so if jump 1 it will just skip this next command
		fw.register.set("IP", ip);
	}
}

// FRAMEWORK
interface INextCommand {
	Command next(Framework fw);
}
class FrameworkRunner implements Runnable {
	private Framework fw;
	private INextCommand nextCommand;
	
	
	public FrameworkRunner(Framework fw, INextCommand nextCommand) {
		super();
		this.fw = fw;
		this.nextCommand = nextCommand;
	}


	@Override
	public void run() {
		for (;;) {
			Command cmd = nextCommand.next(fw);
			if (cmd == null) break;
			System.out.printf("%04X EXEC:  %s%n", fw.register.get("IP"), cmd.toString());
			cmd.execute(fw);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class Framework {
	public Register register;
	public Stack stack;
	public Memory memory;
	public Command[] commands;
	
	public Framework(Register register, Stack stack, Memory memory, Command[] commands) {
		super();
		this.register = register;
		this.stack = stack;
		this.memory = memory;
		this.commands = commands;
	}

	public Command getCommand(int i) {
		if (i < 0 || i >= commands.length) {
			return null;
		}
		return commands[i];
	}
}

// COMPONENTS

class Register {
	public Map<String, Integer> registers = new HashMap<>();
	public boolean createOnAccess = false;
	
	public void newRegister(String name, int initialValue) {
		registers.put(name, initialValue);
	}
	
	public int get(String name) {
		if (!registers.containsKey(name)) {
			if (createOnAccess) {
				registers.put(name, 0);
			} else {
				throw new RuntimeException("Register does not exist");
			}
		}
		return registers.get(name);
	}
	
	public void set(String name, int value) {
		if (!registers.containsKey(name)) {
			if (createOnAccess) {
				registers.put(name, 0);
			} else {
				throw new RuntimeException("Register does not exist");
			}
		}
		registers.put(name, value);
	}
}

class Stack {
	public int[] values;
	public int stackPointer;
	
	public Stack(int size) {
		values = new int[size];
		stackPointer = size;
	}

	public void push(int value) {
		if (stackPointer <= 0) throw new RuntimeException("Stack overflow");
		values[--stackPointer] = value;
	}
	
	public int pop() {
		if (stackPointer >= values.length) throw new RuntimeException("Stack underflow");
		return values[stackPointer++];
	}
	

	public void pushDirect(int pos, int value) {
		if (pos <= 0) throw new RuntimeException("Stack overflow");
		values[pos] = value;
	}
	
	public int popDirect(int pos) {
		if (pos >= values.length) throw new RuntimeException("Stack underflow");
		return values[pos];
	}
}

class Memory {
	public int[] memory;
	
	public Memory(int size) {
		memory = new int[size];
	}

	public void clear() {
		for (int i = 0; i < memory.length; i++)
			memory[i] = 0;
	}
	
	public int read(int pos) {
		if (pos < 0 || pos >= memory.length)
			throw new RuntimeException("Access violation");
		return memory[pos];
	}
	
	public void write(int pos, int value) {
		if (pos < 0 || pos >= memory.length)
			throw new RuntimeException("Access violation");
		memory[pos] = value;
	}
}