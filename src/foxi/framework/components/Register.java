package foxi.framework.components;

import java.util.HashMap;
import java.util.Map;

public class Register {
	public Map<String, Integer> registers = new HashMap<>();
	public boolean createOnAccess = false;
	
	public void newRegister(String name, int initialValue) {
		registers.put(name, initialValue);
	}
	
	public int get(String name) {
		if (!registers.containsKey(name)) {
			if (createOnAccess) {
				registers.put(name, 0);
			} else {
				throw new RuntimeException("Register does not exist");
			}
		}
		return registers.get(name);
	}
	
	public void set(String name, int value) {
		if (!registers.containsKey(name)) {
			if (createOnAccess) {
				registers.put(name, 0);
			} else {
				throw new RuntimeException("Register does not exist");
			}
		}
		registers.put(name, value);
	}
}