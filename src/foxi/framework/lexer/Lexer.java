package foxi.framework.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foxi.framework.ValueType;
import foxi.framework.lexer.token.LexerToken;

public class Lexer {
	
	private Map<String, LexerToken> tokens = new HashMap<>();
	
	public Lexer addToken(String name, LexerToken token) {
		if (tokens.containsKey(name))
			throw new IllegalArgumentException("Name " + name + " does already exist for " + tokens.get(name).getClass().getSimpleName());
		tokens.put(name, token);
		System.out.printf("LEXER: Add token '%s' for '%s'%n", name, token.getClass().getSimpleName());
		return this;
	}
	
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
				LexerToken token = tokens.get(words[0]);
				if (token != null) {
					token.lex(words, result);
				} else {
					System.err.println("LEXER: unknown token, " + words[0]);
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