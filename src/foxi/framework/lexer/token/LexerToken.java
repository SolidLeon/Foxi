package foxi.framework.lexer.token;

import java.util.List;

import foxi.framework.ValueType;

public abstract class LexerToken {

	public abstract void lex(String[] words, List<String[]> result);
	
	protected String stripRegisterPercent(String string) {
		return string.substring(1);
	}
	protected String stripMemoryParenthesis(String string) {
		return string.substring(1, string.length() - 2);
	}

	protected ValueType getType(String s) {
		if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')')
			return ValueType.VALUE_IN_MEMORY;
		if (s.charAt(0) == '%')
			return ValueType.REGISTER;
		return ValueType.VALUE;
	}
	
	protected int toInt(String s) {
		int radix = 10;
		if (s.charAt(s.length() - 1) == 'h') {
			s = s.substring(0, s.length() - 1);
			radix = 16;
		}
		return (int) Long.parseLong(s, radix);
	}
	
}
