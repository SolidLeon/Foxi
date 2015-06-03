package foxi.framework.lexer.token;

import java.util.List;

import foxi.framework.ValueType;

public class LexerTokenWRITE extends LexerToken {

	@Override
	public void lex(String[] words, List<String[]> result) {
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

}
