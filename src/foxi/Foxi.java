package foxi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Foxi implements Runnable {

	interface ICommand {
		void call(String[] r);
	}
	
	private String file;
	private Charset cs = Charset.defaultCharset();
	
	private boolean logging = false;
	
	// VALUES:
	// 	REGISTER
	//	DEC
	//	HEX
	private int[] stack = new int[32];
	private Map<String, Integer> register = new HashMap<>();
	private Map<String, ICommand> commands = new HashMap<>();
	private Map<String, Integer> labels = new HashMap<>();
	private Map<String, String> strings = new HashMap<>();
	private List<Integer> setInterrupts = new ArrayList<Integer>();
	private byte[] memory;
	
	private static int F_ZERO = 0x01;
	
	public Foxi(String file) {
		this.file = file;
	}
	
	@Override
	public void run() {
		//INITIALIZE
		logging = true;
		stack = new int[32];
		memory = new byte[0x1000];
		strings.clear();
		
		register.clear();
		register.put("SP", stack.length);
		register.put("IP", 0); //instruction pointer
		register.put("AX", 0);
		register.put("BX", 0);
		register.put("CX", 0);
		register.put("DX", 0);
		register.put("F", 0);
		//VRAM
		register.put("V0", 0);
		register.put("V1", 0);
		register.put("V2", 0);
		register.put("V3", 0);
		register.put("V4", 0);
		register.put("V5", 0);
		register.put("V6", 0);
		register.put("V7", 0);
		register.put("V8", 0);
		register.put("V9", 0);
		register.put("VA", 0);
		register.put("VB", 0);
		register.put("VC", 0);
		register.put("VD", 0);
		register.put("VE", 0);
		register.put("VF", 0);
		
		commands.clear();
		commands.put("MOV", r -> cbMOV(r));
		commands.put("ADD", r -> cbADD(r));
		commands.put("SUB", r -> cbSUB(r));
		commands.put("CMP", r -> cbSUB(r));
		commands.put("PUSH", r -> cbPUSH(r));
		commands.put("POP", r -> cbPOP(r));
		commands.put("GOTO", r -> cbGOTO(r));
		commands.put("JZ", r -> cbJZ(r));
		commands.put("JNZ", r -> cbJNZ(r));
		commands.put("JRNZ", r -> cbJRNZ(r));
		commands.put("JRZ", r -> cbJRZ(r));
		commands.put("JP", r -> cbJMP(r));
		commands.put("JR", r -> cbJR(r));
		commands.put("PRINT", r -> cbPRINT(r));
		commands.put("STRING", r -> cbSTRING(r));
		commands.put("READ", r -> cbREAD(r));
		commands.put("WRITE", r -> cbWRITE(r));
		commands.put("CALL", r -> cbCALL(r));
		commands.put("RET", r -> cbRETN(r));
		commands.put("INT", r -> cbINT(r));
		
		log("-- REGISTER --");
		for (String key : register.keySet())
			log("  " + key);
		log("-- COMMANDS --");
		for (String key : commands.keySet())
			log("  " + key);
		
		// INITIALIZE END
		logging = false;
		
		
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(file), cs);
			//read was ok
			if (!lines.isEmpty()) {
				List<String[]> instructions = parse(lines);
				findLabels(instructions);
				execute(instructions);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log("==========");
		log("END");
		log("-- REGISTER --");
		for (String key : register.keySet())
			log("%-2s: %d", key, register.get(key));
	}

	private void cbCALL(String[] r) {
		// call <label>
		// call <address>

		if (r != null && r.length > 0) {
			String dst = r[1].trim();
			push(getRegister("IP"));
			gotoAddress(parseInt(dst));
		}
		
	}

	private void cbINT(String[] r) {
		// int value
		if (r != null && r.length > 0) {
			String sInterrupt = r[1].trim();
			int interrupt = parseInt(sInterrupt);
			if (!setInterrupts.contains(interrupt))
				setInterrupts.add(interrupt);
		}
		
	}

	private void cbRETN(String[] r) {
		pop("IP");
	}
	
	private void findLabels(List<String[]> instructions) {
		for (int i = 0; i < instructions.size(); i++) {
			if (instructions.get(i)[0].charAt(0) == ':') {
				String lbl = instructions.get(i)[0].substring(1).trim();
				log("LABEL: " + lbl);
				labels.put(lbl, i);
			}
		}
	}

	private void execute(List<String[]> lines) {
		// 60 ticks per second
		// msec | ticks
		// 1000 | 60
		//  500 | 30
		//    1 | 0.06
		//   17 | 1,02
		
		double unprocessed = 0.0;
		long lastUpdate = System.currentTimeMillis();
		while (getRegister("IP") < lines.size()) {
			long delta = System.currentTimeMillis() - lastUpdate;
			lastUpdate = System.currentTimeMillis();
			unprocessed += delta * (60.0 / 1000.0);
			while (unprocessed > 1.0) {
				unprocessed -= 1.0;
				
				processInterrupt();
				
				String[] r = lines.get(getRegister("IP"));
				setRegister("IP", getRegister("IP") + 1);
				if (r[0].charAt(0) == ':') {
					log("SKIP LABEL: " + r[0]);
				} else {
					log("EXEC: '" + r[0] + "' (" + r[1] + ")");
					ICommand cmd = commands.get(r[0]);
					if (cmd != null)
						cmd.call(r);
					else
						throw new RuntimeException("Unknown command '" + r[0] + "'");
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void processInterrupt() {
		if (setInterrupts.isEmpty())
			return;
		int interrupt = setInterrupts.remove(0);
		if (interrupt == 0x08) {
			System.out.println("REPAINT!!!");
		}
	}

	private void setRegister(String name, int value) {
		if (!register.containsKey(name))
			throw new RuntimeException("Invalid register name '" + name + "'");
		register.put(name, value);
	}

	private int getRegister(String name) {
		if (register.containsKey(name)) {
			return register.get(name);
		}
		throw new RuntimeException("Invalid register name '" + name + "'");
	}
	private int getRegisterPre(String name, int add) {
		if (register.containsKey(name)) {
			register.put(name, register.get(name) + add);
			return register.get(name);
		}
		throw new RuntimeException("Invalid register name '" + name + "'");
	}
	private int getRegisterPost(String name, int add) {
		if (register.containsKey(name)) {
			int value = register.get(name);
			register.put(name, value + add);
			return value;
		}
		throw new RuntimeException("Invalid register name '" + name + "'");
	}
	
	private boolean isRegister(String name) {
		return register.containsKey(name);
	}
	
	private void push(int value) {
		stack[getRegisterPre("SP", - 1)] = value;
	}
	
	private void pop(String dstRegister) {
		setRegister(dstRegister, stack[getRegisterPost("SP",  1)]);
	}
	
	private void cbPUSH(String[] r) {
		//push n
		if (r != null && r.length > 0) {
			String src = r[1].trim();
			int v = parseInt(src);
			push(v);
		}
	}

	private void cbPOP(String[] r) {
		//pop register
		if (r != null && r.length > 0) {
			String dst = r[1].trim();
			if (isRegister(dst)) {
				pop(dst);
			} else {
				throw new RuntimeException("POP required a register as destination");
			}
		}
	}

	private void cbJMP(String[] r) {
		//jz label
		//jz address
		if (r != null && r.length > 0) {
			String dst = r[1].trim();
			_goto(dst);
		}
	}
	private void cbSTRING(String[] r) {
		//string <name> <text>
		if (r != null && r.length > 0) {
			int idx = r[1].indexOf(' ');
			String name = r[1].substring(0, idx);
			String value = r[1].substring(idx + 1).trim().replace("\\n", "\n").replace("\\r", "\r");
			strings.put(name, value);
		}
	}
	private void cbPRINT(String[] r) {
		//print .........
		if (r != null && r.length > 0) {
			String stringName = r[1].trim();
			if (isRegister(stringName)) {
				System.out.print((char) getRegister(stringName));
			} else {
				System.out.print(strings.get(stringName));
			}
		}
	}
	private void cbJR(String[] r) {
		//jz label
		//jz address
		if (r != null && r.length > 0) {
			String dst = r[1].trim();
			gotoRelative(dst);
		}
	}
	private void cbJRZ(String[] r) {
		//jzr int
		if (r != null && r.length > 0) {
			String dst = r[1].trim();
			if ((getRegister("F") & F_ZERO) != 0)
				gotoRelative(dst);
		}
	}

	private void cbJRNZ(String[] r) {
		//jzr int
		if (r != null && r.length > 0) {
			String dst = r[1].trim();
			if ((getRegister("F") & F_ZERO) == 0)
				gotoRelative(dst);
		}
	}
	
	private void cbJZ(String[] r) {
		//jz label
		//jz address
		if (r != null && r.length > 0) {
			String dst = r[1].trim();
			if ((getRegister("F") & F_ZERO) != 0)
				_goto(dst);
		}
	}
	private void cbJNZ(String[] r) {
		//jnz label
		//jnz address
		if (r != null && r.length > 0) {
			String dst = r[1].trim();
			if ((getRegister("F") & F_ZERO) == 0)
				_goto(dst);
		}
	}

	private void gotoRelative(String dst) {
		//goto relative address
		//goto relative register
		int currentAddress = getRegister("IP");
		gotoAddress(currentAddress + parseInt(dst));
	}
	
	private void _goto(String dst) {
		gotoAddress(parseInt(dst));
	}
	
	/** use {@link Foxi#_goto(String)} */
	private void gotoAddress(int dst) {
		setRegister("IP", dst);
	}
	
	private void cbGOTO(String[] r) {
		if (r != null && r.length > 0) {
			String dst = r[1].trim();
			_goto(dst);
		}
	}

	private boolean isLabel(String dst) {
		return labels.containsKey(dst);
	}


	private void cmp(String[] r) {
		// if zero flag is set skip next instruction
		if ((getRegister("F") & F_ZERO) != 0)
			setRegister("IP", getRegister("IP") + 1);
	}

	private boolean isAdress(String s) {
		return s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')';
	}
	
	private boolean isInt(String s) {
		int radix = 10;
		if (s.endsWith("h") || s.endsWith("H")) {
			s = s.substring(0, s.length() - 1);
			radix = 16;
		}
		try {
			Integer.parseInt(s, radix);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	private int toInt(String s) {
		int radix = 10;
		if (s.endsWith("h") || s.endsWith("H")) {
			s = s.substring(0, s.length() - 1);
			radix = 16;
		}
		return Integer.parseInt(s, radix);
	}
	private int toMemInt(String s) {
		// strip of parenthesis
		return toInt(s.substring(1, s.length() - 1)); 
	}

	private void cbADD(String[] r) {
		String[] params = r[1].split(" ");
		if (params != null && params.length > 1) {
			String dst = params[0];
			String src = params[1];
			
			if (isRegister(dst)) {
				setRegister(dst, getRegister(dst) + parseInt(src));
			} else {
				// we cannot store values in a value
				throw new RuntimeException("Move into value");
			}
			if (getRegister(dst) == 0)
				setRegister("F", getRegister("F") | F_ZERO);
			else
				setRegister("F", getRegister("F") & ~F_ZERO);
		}
	}
	private void cbSUB(String[] r) {
		String[] params = r[1].split(" ");
		if (params != null && params.length > 1) {
			String dst = params[0];
			String src = params[1];
			
			if (isRegister(dst)) {
				setRegister(dst, getRegister(dst) - parseInt(src));
			} else {
				// we cannot store values in a value
				throw new RuntimeException("Move into value");
			}
			log("RESULT: " + getRegister(dst));
			// subtraction result was 0 so set zero flag
			if (getRegister(dst) == 0)
				setRegister("F", getRegister("F") | F_ZERO);
			else
				setRegister("F", getRegister("F") & ~F_ZERO);
		}
	}

	private void cbMOV(String[] r) {
		//mov register register 		mov AX BX
		//mov register mem-address		mov AX (400h)
		//mov register value			mov AX 400h
		String[] params = r[1].split(" ");
		if (params != null && params.length > 1) {
			String dst = params[0];
			String src = params[1];
			
			if (src.equals(dst)) {
				//ignore move to itself
				throw new RuntimeException("Move to itself");
			} else {
				if (isRegister(dst)) {
					setRegister(dst, parseInt(src));
				} else {
					// we cannot move into a value
					throw new RuntimeException("Move into value");
				}
			}
		}
	}
	
	private int parseInt(String s) {
		if (isRegister(s)) {
			return getRegister(s);
		} else if (isAdress(s)) {
			return memRead(toMemInt(s));
		} else if (isLabel(s)) {
			return labels.get(s);
		} else {
			return toInt(s);
		}
	}
	
	// MEMORY OPERATIONS
	private int memRead(int address) {
		return memory[address];
	}
	private void cbREAD(String[] r) {
		//read address
		if (r != null && r.length > 0) {
			int idx = r[1].indexOf(' ');
		}
	}
	
	private void cbWRITE(String[] r) {
		
	}

	private List<String[]> parse(List<String> lines) {
		List<String[]> result = new ArrayList<>();
		for (String line : lines) {
			if ((line = line.trim()).isEmpty())
				continue;
			if (line.charAt(0) == ';')
				continue;
			String[] r = parse(line);
			log("PARSE: " + r[0] + "  " + r[1]);
			result.add(r);
		}
		return result;
	}

	/**
	 * 
	 * @param line
	 * @return [cmd, remaining line]
	 */
	private String[] parse(String line) {
		String[] result = null;
		if (line.indexOf(' ') != -1) {
			String[] split = line.split(" ");
			result = new String[] { split[0].trim(), line.substring(line.indexOf(' ')).trim() };		
		} else {
			result = new String[] { line, "" };
		}
		return result;
	}

	private void log(String format, Object... args) {
		if (logging)
			System.out.println(String.format(format, args));
	}
}
