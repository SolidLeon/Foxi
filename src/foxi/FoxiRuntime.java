package foxi;

import java.util.Map;

public class FoxiRuntime {
	public Map<String, Integer> register;
	public String[] memory;
	
	public int readRegister(String name) {
		return register.get(name);
	}
	public void writeRegister(String name, int value) {
		register.put(name, value);
	}
	public String readMemory(int pos) {
		return memory[pos];
	}
	public void writeMemory(int pos, String value) {
		memory[pos] = value;
	}
}
