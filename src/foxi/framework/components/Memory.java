package foxi.framework.components;

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
	
	public int readW(int pos) {
		if (pos < 0 || pos+1 >= memory.length)
			throw new RuntimeException("Access violation");
		return (memory[pos]&0xff)<<8 | (memory[pos + 1]&0xff);
	}
	
	public int readDW(int pos) {
		if (pos < 0 || pos+3 >= memory.length)
			throw new RuntimeException("Access violation");
		return (memory[pos]&0xff)<<24 | (memory[pos + 1]&0xff)<<16 | (memory[pos + 2]&0xff)<<8 | (memory[pos + 3]&0xff);
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
		if (pos < 0 || pos+3 >= memory.length)
			throw new RuntimeException("Access violation");
		memory[pos+0] = (byte) ((value>>24)&0xff);
		memory[pos+1] = (byte) ((value>>16)&0xff);
		memory[pos+2] = (byte) ((value>>8)&0xff);
		memory[pos+3] = (byte) (value&0xff);
	}

	public int size() {
		return memory.length;
	}

	public void fill(int off, int len, int value) {
		if (off < 0 || off >= size())
			throw new IllegalArgumentException("Invalid offset");
		if (len < 0 || len > size())
			throw new IllegalArgumentException("Invalid len");
		if (off + (len-1) >= size())
			throw new IllegalArgumentException("Invalid offset and length");
		for (int i = off; i < off+len; i++) {
			memory[i] = (byte) (value&0xff);
		}
	}
	
	public void fill(int off, int value) {
		fill(off, size(), value);
	}
}