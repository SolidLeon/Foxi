package foxi.framework;

public class Stack {
	public int[] values;
	public int stackPointer;
	
	public Stack(int size) {
		values = new int[size];
		stackPointer = size;
	}

	public void push(int value) {
		if (stackPointer <= 0) throw new RuntimeException("Stack overflow");
		values[--stackPointer] = value;
	}
	
	public int pop() {
		if (stackPointer >= values.length) throw new RuntimeException("Stack underflow");
		return values[stackPointer++];
	}
	

	public void pushDirect(int pos, int value) {
		if (pos <= 0) throw new RuntimeException("Stack overflow");
		values[pos] = value;
	}
	
	public int popDirect(int pos) {
		if (pos >= values.length) throw new RuntimeException("Stack underflow");
		return values[pos];
	}
}