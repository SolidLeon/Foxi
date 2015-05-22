package foxi;


public class FoxiInterpreter implements Runnable {

	public FoxiRuntime runtime;
	
	public FoxiInterpreter(FoxiRuntime runtime) {
		this.runtime = runtime;
	}
	
	@Override
	public void run() {
		while (runtime.readRegister("IP") < runtime.memory.length) {
			String op = runtime.memory[runtime.readRegister("IP")];
			System.out.println(op + "@" + runtime.readRegister("IP"));
			runtime.writeRegister("IP", runtime.readRegister("IP") + 1);
			
			
			switch (op) {
			case "PRINT":
				System.out.println(runtime.memory[runtime.readRegister("IP")]);
				runtime.writeRegister("IP", runtime.readRegister("IP") + 1);
				break;
			}
		}
	}
	
}
