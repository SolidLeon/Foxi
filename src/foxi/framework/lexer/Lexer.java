package foxi.framework.lexer;

import java.util.ArrayList;
import java.util.List;

import foxi.framework.ValueType;

public class Lexer {
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
				if ("ADD".equals(words[0])) {
					ValueType a = getType(words[1]);
					ValueType b = getType(words[2]);
					// a must be a register
					if (a == ValueType.REGISTER) {
						switch (b) {
						case REGISTER: result.add(new String[] {"ADDA", stripRegisterPercent(words[1]), stripRegisterPercent(words[2])});
							break;
						case VALUE: result.add(new String[] {"ADDB", stripRegisterPercent(words[1]),  "" + toInt(words[2])});
							break;
						case VALUE_IN_MEMORY: result.add(new String[] {"ADDC", stripRegisterPercent(words[1]),  "" + toInt(stripMemoryParenthesis(words[2]))});
							break;
						}
					}
				}
				if ("WRITE".equals(words[0])) {
					ValueType a = getType(words[1]); //memory address
					ValueType b = getType(words[2]); //value
					if (a == ValueType.REGISTER) {
						switch (b) {
						case REGISTER: result.add(new String[] {"WRITEA", stripRegisterPercent(words[1]), stripRegisterPercent(words[2])});
							break;
						case VALUE: result.add(new String[] {"WRITEB", stripRegisterPercent(words[1]),  "" + toInt(words[2])});
							break;
						case VALUE_IN_MEMORY: result.add(new String[] {"WRITEC", stripRegisterPercent(words[1]),  "" + toInt(stripMemoryParenthesis(words[2]))});
							break;
						}
					}
					if (a == ValueType.VALUE) {
						switch (b) {
						case REGISTER: result.add(new String[] {"WRITED", "" + toInt(words[1]), stripRegisterPercent(words[2])});
							break;
						case VALUE: result.add(new String[] {"WRITEE", "" + toInt(words[1]),  "" + toInt(words[2])});
							break;
						case VALUE_IN_MEMORY: result.add(new String[] {"WRITEF", "" + toInt(words[1]),  "" + toInt(stripMemoryParenthesis(words[2]))});
							break;
						}
					}
					if (a == ValueType.VALUE_IN_MEMORY) {
						switch (b) {
						case REGISTER: result.add(new String[] {"WRITEG", "" + toInt(stripMemoryParenthesis(words[1])), stripRegisterPercent(words[2])});
							break;
						case VALUE: result.add(new String[] {"WRITEH", "" + toInt(stripMemoryParenthesis(words[1])),  "" + toInt(words[2])});
							break;
						case VALUE_IN_MEMORY: result.add(new String[] {"WRITEI", "" + toInt(stripMemoryParenthesis(words[1])),  "" + toInt(stripMemoryParenthesis(words[2]))});
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
		return (int) Long.parseLong(s, radix);
	}
}