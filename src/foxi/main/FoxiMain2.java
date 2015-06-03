package foxi.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class FoxiMain2 {
	static Image getImage(String path) {
		try {
			return ImageIO.read(new File("C:\\Users\\mma\\Pictures\\test.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static JLabel getImageLabel(Image img) {
		if (img != null)
			return new JLabel(new ImageIcon(img));
		return new JLabel("Image N/A");
	}
	
	static Image testImage(byte[] memory) {
		BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		new VRAMPainter().paint(g, memory, 0, 64, 64);
		g.dispose();
		return img;
	}
	
	static void open(String title, JLabel lbl) {
		JFrame frm = new JFrame(title);
		frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frm.add(lbl);
		frm.pack();
		frm.setLocationRelativeTo(null);
		SwingUtilities.invokeLater(() -> frm.setVisible(true));
	}
	
	// WORKS!!
	static Image testSome(byte[] memory) {
		BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics g2 = img.createGraphics();
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());
		new VRAMPainter().paint(g2, memory, 0, img.getWidth(), img.getHeight());
		g2.dispose();
		return img;
	}
	
	public static void main(String[] args) {
		Memory mem = new Memory(64 * 64 * 4);
		File imgFile = new File("C:\\Users\\mma\\Pictures\\test.bmp");
		VRAMHelper.loadImageIntoMemory(mem, imgFile);
		SimpleDisplayFrame.open(mem.memory, 640, 480, 0);
//		open("BufferedImage", getImageLabel(getImage("C:\\Users\\mma\\Pictures\\test.bmp")));
//		open("VRAM Image", getImageLabel(testImage(mem.memory)));
//		open("TEST", getImageLabel(testSome(mem.memory)));
		if (true) return;
		
		
		String[] commands = {
			";This is a comment",
			"NOP",
			"MOV %AX 5h",
			"JR (0h)",
			";JR 1",
			"MOV %AX 1",
			"JR %AX",
			"MOV %AX ffh",
			"MOV %AX DEADh",
			"MOV %AX %IP",
			"PUSH %IP",
			"POP %AX",
//			"MOV %BX 00000000h",
//			"ADD %BX 1",
//			"WRITE 0 %BX",
//			"JR -3"
//			"WRITE 0 ff0000ffh",
//			"WRITE 1 00ff00ffh",
//			"WRITE 2 0000ffffh",
//			"WRITE 3 ffffffffh",
//			"WRITE 4 000000ffh",
		};
		
		Lexer lexer = new Lexer();
		Compiler compiler = new Compiler();
		Framework fw = new Framework(new Register(), new Stack(32), new Memory(800 * 800 * 2), compiler.compile(lexer.lex(Arrays.asList(commands))));

		
		fw.initialize();
		
//		SimpleDisplayFrame.open(fw.memory.memory, 8, 8, 0);
		
		Thread th = new Thread(new FrameworkRunner(fw, FoxiMain2::next));
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("=== REGISTER ===");
		for (Entry<String, Integer> entry : fw.register.registers.entrySet()) {
			System.out.printf("%-4s: %04d (%04Xh)%n", entry.getKey(), entry.getValue(), entry.getValue());
		}
		System.out.println("=== STACK ===");
		for (int i = fw.stack.values.length - 1; i >= fw.register.get("SP"); i--) {
			System.out.printf("%04d:  %04d (%04Xh)%n", i, fw.stack.values[i], fw.stack.values[i]);
		}
	}
	
	static Command next(Framework fw) {
		int ip = fw.register.get("IP");
		fw.register.set("IP", ip + 1);
		return fw.getCommand(ip);
		
	}
}

class VRAMHelper {
	public static int toPackedColorARGB(int a, int r, int g, int b) {
		return (a&0xff)<<24 | (r&0xff)<<16 | (g&0xff)<<8 | (b&0xff);
	}
	
	public static void loadImageIntoMemory(Memory mem, File imgFile) {
		try {
			BufferedImage img = ImageIO.read(imgFile);
			byte[] pixels = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
			
			System.out.println("PICTURE LOADED '"+imgFile.getAbsolutePath()+"'");
			System.out.println(VRAMHelper.getImageType(img));
			System.out.println(img.getColorModel());
			
			for (int y = 0; y < img.getHeight(); y++) {
				for (int x = 0; x < img.getWidth(); x++) {
					int argb = img.getRGB(x, y);
					mem.writeDW((x + y * img.getWidth()) * 4, argb);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getImageType(BufferedImage img) {
		switch (img.getType()) {
		case BufferedImage.TYPE_3BYTE_BGR:
			return ("  TYPE_3BYTE_BGR");
		case BufferedImage.TYPE_4BYTE_ABGR:
			return("  TYPE_4BYTE_ABGR");
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			return("  TYPE_4BYTE_ABGR_PRE");
		case BufferedImage.TYPE_BYTE_BINARY:
			return("  TYPE_BYTE_BINARY");
		case BufferedImage.TYPE_BYTE_GRAY:
			return("  TYPE_BYTE_GRAY");
		case BufferedImage.TYPE_BYTE_INDEXED:
			return("  TYPE_BYTE_INDEXED");
		case BufferedImage.TYPE_CUSTOM:
			return("  TYPE_CUSTOM");
		case BufferedImage.TYPE_INT_ARGB:
			return("  TYPE_INT_ARGB");
		case BufferedImage.TYPE_INT_ARGB_PRE:
			return("  TYPE_INT_ARGB_PRE");
		case BufferedImage.TYPE_INT_BGR:
			return("  TYPE_INT_BGR");
		case BufferedImage.TYPE_INT_RGB:
			return("  TYPE_INT_RGB");
		case BufferedImage.TYPE_USHORT_555_RGB:
			return("  TYPE_USHORT_555_RGB");
		case BufferedImage.TYPE_USHORT_565_RGB:
			return("  TYPE_USHORT_565_RGB");
		case BufferedImage.TYPE_USHORT_GRAY:
			return("  TYPE_USHORT_GRAY");
		default:
			return "  TYPE OTHER";
		}
	}
	public static void writeARGB(byte[] memory, int offset, int argb) {
		if (memory.length < offset + 4)
			throw new RuntimeException("memory not big enough");
		memory[offset + 0] = (byte) ((argb >> 24) & 0xff);
		memory[offset + 1] = (byte) ((argb >> 16) & 0xff);
		memory[offset + 2] = (byte) ((argb >>  8) & 0xff);
		memory[offset + 3] = (byte) ((argb >>  0) & 0xff);
		System.out.printf("%02X  %02X  %02X  %02X%n",
				memory[offset + 0],
				memory[offset + 1],
				memory[offset + 2],
				memory[offset + 3]);
	}
}

class Utils {
	public static void hexOut(int[] data) {
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		int i = 0;
		for (i = 0; i < data.length; i++) {
			if (i % 0x10 == 0 && sb1.length() > 0) {
				System.out.printf("%04X:  %-64s    %s%n", (i - 0x10), sb1.toString(), sb2.toString());
				sb1.setLength(0);
				sb2.setLength(0);
			}
			sb1.append(String.format("%02X  ", data[i]));
			sb2.append(Character.isISOControl((char) data[i]) ? '.' : (char) data[i]);
		}
		if (sb1.length() > 0) {
			System.out.printf("%04X:  %-64s    %s%n", (i - 0x10), sb1.toString(), sb2.toString());
		}
	}
}

// LEXER
enum ValueType {
	REGISTER,
	VALUE,
	VALUE_IN_MEMORY,
}
class Lexer {
	public List<String[]> lex(List<String> lines) {
		List<String[]> result = new ArrayList<>();
		System.out.println("LEXER START");
		for (String line : lines) {
			if (line == null) continue;
			line = line.trim();
			if (line.isEmpty()) continue;
			if (line.charAt(0) == ';') continue;
			System.out.println("LEXER: Process '" + line + "'");
			String[] words = line.split(" ");
			for (int i = 0; i < words.length; i++)
				words[i] = words[i].trim();
			if (words.length > 0) {
				if ("NOP".equals(words[0])) result.add(new String[] {"NOP"});
				if ("MOV".equals(words[0])) {
					ValueType a = getType(words[1]);
					ValueType b = getType(words[2]);
					// a must be a register
					if (a == ValueType.REGISTER) {
						switch (b) {
						case REGISTER: result.add(new String[] {"MOVA", stripRegisterPercent(words[1]), stripRegisterPercent(words[2])});
							break;
						case VALUE: result.add(new String[] {"MOVB", stripRegisterPercent(words[1]),  "" + toInt(words[2])});
							break;
						case VALUE_IN_MEMORY: result.add(new String[] {"MOVC", stripRegisterPercent(words[1]),  "" + toInt(stripMemoryParenthesis(words[2]))});
							break;
						}
					}
				}
				if ("ADD".equals(words[0])) {
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
				if ("WRITE".equals(words[0])) {
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
				if ("JR".equals(words[0])) {
					ValueType a = getType(words[1]);
					switch (a) {
					case REGISTER: result.add(new String[] {"JRA", stripRegisterPercent(words[1])});
						break;
					case VALUE: result.add(new String[] {"JRB", "" + toInt(words[1])});
						break;
					case VALUE_IN_MEMORY: result.add(new String[] {"JRC", "" + toInt(stripMemoryParenthesis(words[1]))});
						break;
					}
				}
				if ("PUSH".equals(words[0])) {
					ValueType a = getType(words[1]);
					switch (a) {
					case REGISTER: result.add(new String[] {"PUSHA", stripRegisterPercent(words[1])});
						break;
					case VALUE: result.add(new String[] {"PUSHB", "" + toInt(words[1])});
						break;
					case VALUE_IN_MEMORY: result.add(new String[] {"PUSHC", "" + toInt(stripMemoryParenthesis(words[1]))});
						break;
					}
				}
			}
		}
		System.out.println("LEXER END");
		return result;
	}
	
	private String stripRegisterPercent(String string) {
		return string.substring(1);
	}
	private String stripMemoryParenthesis(String string) {
		return string.substring(1, string.length() - 2);
	}

	private ValueType getType(String s) {
		if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')')
			return ValueType.VALUE_IN_MEMORY;
		if (s.charAt(0) == '%')
			return ValueType.REGISTER;
		return ValueType.VALUE;
	}
	
	private int toInt(String s) {
		int radix = 10;
		if (s.charAt(s.length() - 1) == 'h') {
			s = s.substring(0, s.length() - 1);
			radix = 16;
		}
		return (int) Long.parseLong(s, radix);
	}
}

//COMPILER
class Compiler {
	public Command[] compile(List<String[]> lines) {
		List<Command> result = new ArrayList<>();
		for (String[] words : lines) {
			if (words == null) continue;
			if (words.length > 0) {
				System.out.println("COMPILER:  Process '" + words[0] + "'");
				if ("NOP".equals(words[0])) result.add(new NopCommand());
				if ("MOVA".equals(words[0])) result.add(new WriteRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.REGISTER)); //MOVA %, %
				if ("MOVB".equals(words[0])) result.add(new WriteRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE));
				if ("MOVC".equals(words[0])) result.add(new WriteRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE_IN_MEMORY));
				if ("JRA".equals(words[0])) result.add(new JumpRelativeCommand(words[1], ValueType.REGISTER));
				if ("JRB".equals(words[0])) result.add(new JumpRelativeCommand(words[1], ValueType.VALUE));
				if ("JRC".equals(words[0])) result.add(new JumpRelativeCommand(words[1], ValueType.VALUE_IN_MEMORY));
				if ("PUSHA".equals(words[0])) result.add(new PushCommand(words[1], ValueType.REGISTER));
				if ("PUSHB".equals(words[0])) result.add(new PushCommand(words[1], ValueType.VALUE));
				if ("PUSHC".equals(words[0])) result.add(new PushCommand(words[1], ValueType.VALUE_IN_MEMORY));
				if ("WRITEA".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.REGISTER, words[2], ValueType.REGISTER));
				if ("WRITEB".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE));
				if ("WRITEC".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE_IN_MEMORY));
				if ("WRITED".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE, words[2], ValueType.REGISTER));
				if ("WRITEE".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE, words[2], ValueType.VALUE));
				if ("WRITEF".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE, words[2], ValueType.VALUE_IN_MEMORY));
				if ("WRITEG".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE_IN_MEMORY, words[2], ValueType.REGISTER));
				if ("WRITEH".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE_IN_MEMORY, words[2], ValueType.VALUE));
				if ("WRITEI".equals(words[0])) result.add(new WriteMemoryCommand(words[1], ValueType.VALUE_IN_MEMORY, words[2], ValueType.VALUE_IN_MEMORY));
				if ("ADDA".equals(words[0])) result.add(new AddRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.REGISTER));
				if ("ADDB".equals(words[0])) result.add(new AddRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE));
				if ("ADDC".equals(words[0])) result.add(new AddRegisterCommand(words[1], ValueType.REGISTER, words[2], ValueType.VALUE_IN_MEMORY));
			}
		}
		return result.toArray(new Command[0]);
	}
}

// COMMANDS
class Command {
	public void execute(Framework fw) {
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}

class Command1Arg extends Command {
	protected String value;
	protected ValueType type;
	
	public Command1Arg(String value, ValueType type) {
		super();
		this.value = value;
		this.type = type;
	}
	


	protected int getActual(Framework fw) {
		switch (type) {
		case REGISTER: 			return fw.register.get(value);
		case VALUE: 			return Integer.parseInt(value);
		case VALUE_IN_MEMORY: 	return fw.memory.read(Integer.parseInt(value));
		}
		throw new RuntimeException("Invalid value type");
	}
	
	@Override
	public String toString() {
		switch (type) {
		case REGISTER: 			return String.format("%s %%%s", super.toString(), value);
		case VALUE: 			return String.format("%s %04Xh", super.toString(), Integer.parseInt(value));
		case VALUE_IN_MEMORY: 	return String.format("%s (%04Xh)", super.toString(), Integer.parseInt(value));
		}
		return String.format("%s %s", super.toString(), value);
	}
	
}
class Command2Arg extends Command {
	protected String arg1;
	protected ValueType type1;
	protected String arg2;
	protected ValueType type2;

	public Command2Arg(String arg1, ValueType type1, String arg2,
			ValueType type2) {
		super();
		this.arg1 = arg1;
		this.type1 = type1;
		this.arg2 = arg2;
		this.type2 = type2;
	}

	@Override
	public String toString() {
		switch (type2) {
		case REGISTER: 			return String.format("%s %%%s %%%s", super.toString(), arg1, arg2);
		case VALUE: 			return String.format("%s %%%s %04Xh", super.toString(), arg1, Integer.parseInt(arg2));
		case VALUE_IN_MEMORY: 	return String.format("%s %%%s (%04Xh)", super.toString(), arg1, Integer.parseInt(arg2));
		}
		return String.format("%s %s %s", super.toString(), arg1, arg2);
	}


	protected int getActual1(Framework fw) {
		int actual = 0;
		switch (type1) {
		case REGISTER: actual = fw.register.get(arg1);
			break;
		case VALUE: actual = Integer.parseInt(arg1);
			break;
		case VALUE_IN_MEMORY: actual = fw.memory.read(Integer.parseInt(arg1));
			break;
		}
		return actual;
	}

	protected int getActual2(Framework fw) {
		int actual = 0;
		switch (type2) {
		case REGISTER: actual = fw.register.get(arg2);
			break;
		case VALUE: actual = Integer.parseInt(arg2);
			break;
		case VALUE_IN_MEMORY: actual = fw.memory.read(Integer.parseInt(arg2));
			break;
		}
		return actual;
	}
	
}

class NopCommand extends Command {
}

class PushCommand extends Command1Arg {

	public PushCommand(String value, ValueType type) {
		super(value, type);
	}

	@Override
	public void execute(Framework fw) {
		int sp = fw.getStackPointer();
		sp -= 1;
		fw.setStackPointer(sp);
		fw.stack.pushDirect(sp, getActual(fw));
	}
	
}

class WriteRegisterCommand extends Command2Arg {
	public WriteRegisterCommand(String arg1, ValueType type1, String arg2,
			ValueType type2) {
		super(arg1, type1, arg2, type2);
	}

	@Override
	public void execute(Framework fw) {
		fw.register.set(arg1, getActual2(fw));
	}
	
}
class AddRegisterCommand extends Command2Arg {
	public AddRegisterCommand(String arg1, ValueType type1, String arg2,
			ValueType type2) {
		super(arg1, type1, arg2, type2);
	}

	@Override
	public void execute(Framework fw) {
		int v = fw.register.get(arg1);
		v += getActual2(fw);
		fw.register.set(arg1, v);
	}
	
}
class WriteMemoryCommand extends Command2Arg {
	public WriteMemoryCommand(String arg1, ValueType type1, String arg2,
			ValueType type2) {
		super(arg1, type1, arg2, type2);
	}

	@Override
	public void execute(Framework fw) {
		fw.memory.write(getActual1(fw), getActual2(fw));
	}
	
}
class JumpRelativeCommand extends Command1Arg {
	public JumpRelativeCommand(String value, ValueType type) {
		super(value, type);
	}

	@Override
	public void execute(Framework fw) {
		int ip = fw.getInstructionPointer();
		ip += getActual(fw); //IP points to NEXT command, so if jump 1 it will just skip this next command
		fw.setInstructionPointer(ip);
	}
}

//DISPALY
class SimpleDisplayFrame extends JFrame {
	public static SimpleDisplayFrame open(byte[] memory, int width, int height, int memoryOffset) {
		final SimpleDisplayFrame frm = new SimpleDisplayFrame();
		frm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frm.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frm.closing = true;
			}
		});
		Display display = new Display(memory, width, height, memoryOffset);
		frm.add(display);
		frm.pack();
		frm.setLocationRelativeTo(null);
		SwingUtilities.invokeLater(() -> frm.setVisible(true));
		
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				for (;;) {
					if (frm.closing)
						break;
					display.update();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				frm.dispose();
			}
		}); 
		th.start();
		return frm;
	}
	
	private boolean closing = false;
}
/**
 * Takes a memory as VRAM.
 * Memory pixel data should be stored:
 * ARGB
 * Each channel is one byte.
 * The order is left-to-right, top-to-bottom.
 * So each pixel row should be following.
 * Example:
 * Following image should be drawn:
 * {0,0} {1,0}
 * {0,1} {1,1}
 * 
 * In memory this should be
 * {0,0} {1,0} {0,1} {1,1}
 * ARGB  ARGB  ARGB  ARGB
 * 
 * So we would need 16 bytes for this image to be drawn
 * 
 * @author MMA
 *
 */
class Display extends Canvas {
	
	/** memory from where to draw */
	private byte[] memory;
	/** offset in memory where VRAM begins */
	private int memoryOffset;
	/** size (in pixel) each pixel should be drawn with */
	private int scale = 2;
	private VRAMPainter painter = new VRAMPainter();
	private BufferedImage img;
	
	public Display(byte[] memory, int displayWidth, int displayHeight, int memoryOffset) {
		super();
		this.memory = memory;
		this.memoryOffset = memoryOffset;
		setPreferredSize(new Dimension(displayWidth * scale, displayHeight * scale));
		
		if (memoryOffset < 0 || memoryOffset >= memory.length)
			throw new RuntimeException("Memory offset out-of-bounds, was " + memoryOffset + " must be within [0," + memory.length + "[");
		
		img = new BufferedImage(displayWidth, displayHeight, BufferedImage.TYPE_INT_ARGB);
	}

	public Display(byte[] memory) {
		this(memory, 640, 480, 0);
	}

	public void update() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}
		
		// Draw to buffered image, having the right TYPE (ARGB)
		// This section is tested and works, but only outside ... why?
		Graphics g2 = img.createGraphics();
		g2.fillRect(0, 0, img.getWidth(), img.getHeight());
		painter.paint(g2, memory, memoryOffset, img.getWidth(), img.getHeight());
		g2.dispose();
		
		// Draw image to back buffer, centered and scaled
		Graphics g = bs.getDrawGraphics();
		g.fillRect(0, 0, getWidth(), getHeight());

		int xo = (int) ((getWidth() - img.getWidth() * scale) / 2f);
		int yo = (int) ((getHeight() - img.getHeight() * scale) / 2f);
		g.translate(xo, yo);
		
		g.drawImage(img, 0, 0, img.getWidth() * scale, img.getHeight() * scale, null);
		
		g.translate(-xo, -yo);
		g.dispose();
		bs.show();
	}
	

}

class VRAMPainter {
	public void paint(Graphics g, byte[] memory, int offset, int width, int height) {
		outer: for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int memoryAdress = toMemoryAdress(x, y, offset, width);
				//We reached the end of our VRAM, so end here
				if (memoryAdress >= memory.length)
					break outer;
				g.setColor(getColor(memory, memoryAdress));
				g.fillRect(x, y, 1, 1);
			}
		}
		
	}
	
	public static int toMemoryAdress(int x, int y, int offset, int width) {
		return offset + x + y * width;
	}
	
	/**
	 * Creates a color from the 4 bytes at 
	 * A  offset
	 * R  offset + 1
	 * G  offset + 2
	 * B  offset + 3
	 * @param memory
	 * @param offset
	 * @return Color
	 */
	public static Color getColor(byte[] memory, int offset) {
		int a = memory[offset + 0] & 0xff;
		int r = memory[offset + 1] & 0xff;
		int g = memory[offset + 2] & 0xff;
		int b = memory[offset + 3] & 0xff;
		return new Color(r, g, b, a);
	}
}

// FRAMEWORK
interface INextCommand {
	Command next(Framework fw);
}
class FrameworkRunner implements Runnable {
	private Framework fw;
	private INextCommand nextCommand;
	
	
	public FrameworkRunner(Framework fw, INextCommand nextCommand) {
		super();
		this.fw = fw;
		this.nextCommand = nextCommand;
	}


	@Override
	public void run() {
		for (;;) {
			Command cmd = nextCommand.next(fw);
			if (cmd == null) break;
			System.out.printf("%04X EXEC:  %s%n", fw.register.get("IP"), cmd.toString());
			cmd.execute(fw);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class Framework {
	public Register register;
	public Stack stack;
	public Memory memory;
	public Command[] commands;
	
	public Framework(Register register, Stack stack, Memory memory, Command[] commands) {
		super();
		this.register = register;
		this.stack = stack;
		this.memory = memory;
		this.commands = commands;
	}

	public void initialize() {
		register.newRegister("AX", 0x1234);
		register.newRegister("BX", 0);
		register.newRegister("CX", 0);
		register.newRegister("DX", 0);
		register.newRegister("IP", 0);
		register.newRegister("SP", stack.values.length);
	}

	public int getStackPointer() {
		return register.get("SP");
	}

	public void setStackPointer(int sp) {
		register.set("SP", sp);
	}
	public int getInstructionPointer() {
		return register.get("IP");
	}

	public void setInstructionPointer(int ip) {
		register.set("IP", ip);
	}

	public Command getCommand(int i) {
		if (i < 0 || i >= commands.length) {
			return null;
		}
		return commands[i];
	}
}

// COMPONENTS

class Register {
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

class Stack {
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

class Memory {
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