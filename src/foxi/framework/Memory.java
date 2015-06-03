package foxi.framework;

public class Memory {
	public byte[] memory;
	
	public Memory(int size) {
		memory = new byte[size];
		System.out.printf("MEMORY: Allocated %d bytes%n", size);
	}
	
	public void clear() {
		for (int i = 0; i < memory.length; i++)
			memory[i] = 0;
	}
	
	public int read(int pos) {
		if (pos < 0 || pos >= memory.length)
			throw new RuntimeException("Access violation");
		return memory[pos];
	}

	public void write(int pos, int value) {
		if (pos < 0 || pos >= memory.length)
			throw new RuntimeException("Access violation");
		memory[pos] = (byte) (value&0xff);
	}
	public void writeW(int pos, int value) {
		if (pos < 0 || pos+1 >= memory.length)
			throw new RuntimeException("Access violation");
		memory[pos] = (byte) ((value>>8)&0xff);
		memory[pos+1] = (byte) (value&0xff);
	}
	public void writeDW(int pos, int value) {
		if (pos < 0 || pos >= memory.length)
			throw new RuntimeException("Access violation");
		memory[pos] = (byte) ((value>>24)&0xff);
		memory[pos+1] = (byte) ((value>>16)&0xff);
		memory[pos+2] = (byte) ((value>>8)&0xff);
		memory[pos+3] = (byte) (value&0xff);
	}
}