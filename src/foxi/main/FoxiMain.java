package foxi.main;

import foxi.Foxi;
import foxi.FoxiCompiler;
import foxi.FoxiInterpreter;

public class FoxiMain {

	public static void main(String[] args) {
		
		FoxiCompiler compiler = new FoxiCompiler("new.fxi");
		compiler.run();
		FoxiInterpreter interpreter = new FoxiInterpreter(compiler.runtimeResult);
		interpreter.run();
		
//		String file = null;
//		if (args != null && args.length > 0) {
//			file = args[0];
//		}
//		file = "test.fxi";
//		new Thread(new Foxi(file)).start();
	}

}

class BF {
	
	int[] register = new int[10];
	int registerPointer = 0;
	int instructionPointer = 0;
	String src = "++++[>++++<-]";
	
	void run() {
		while (instructionPointer < src.length()) {
			char ch = src.charAt(instructionPointer);
			instructionPointer = instructionPointer + 1;
			switch (ch) {
			case '+': register[registerPointer] += 1; break;
			case '-': register[registerPointer] -= 1; break;
			case '>': registerPointer++; if (registerPointer >= register.length) registerPointer = 0; break;
			case '<': registerPointer--; if (registerPointer < 0) registerPointer = register.length - 1; break;
			case ']':
				if (register[registerPointer] != 0) {
					int closed = 1;
					int loopInstructionPointer = instructionPointer - 1;
					while (closed != 0) {
						char ch2 = src.charAt(--loopInstructionPointer);
						if (ch2 == '[') closed--;
						if (ch2 == ']') closed++;
						if (closed == 0)
							instructionPointer = loopInstructionPointer;
					}
				}
				break;
			}
		}
		for (int i = 0; i < register.length; i++)
			System.out.println(i + ":  " + register[i]);
	}
}
