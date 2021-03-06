package foxi.framework.lexer.token;

import java.util.List;

import foxi.framework.ValueType;

public class LexerTokenADD extends LexerToken {

	@Override
	public void lex(String[] words, List<String[]> result) {
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

}
