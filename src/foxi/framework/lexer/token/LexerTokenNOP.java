package foxi.framework.lexer.token;

import java.util.List;

public class LexerTokenNOP extends LexerToken {

	@Override
	public void lex(String[] words, List<String[]> result) {
		result.add(new String[] { "NOP" });
	}

}
