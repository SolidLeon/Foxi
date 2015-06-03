package foxi.framework.lexer.token;

import java.util.List;

import foxi.framework.ValueType;

public class LexerTokenPUSH extends LexerToken {

	@Override
	public void lex(String[] words, List<String[]> result) {
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
