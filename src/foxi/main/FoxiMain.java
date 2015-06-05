package foxi.main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import foxi.framework.Framework;
import foxi.framework.FrameworkRunner;
import foxi.framework.command.Command;
import foxi.framework.compiler.Compiler;
import foxi.framework.compiler.token.CompilerTokenADDA;
import foxi.framework.compiler.token.CompilerTokenADDB;
import foxi.framework.compiler.token.CompilerTokenADDC;
import foxi.framework.compiler.token.CompilerTokenJRA;
import foxi.framework.compiler.token.CompilerTokenJRB;
import foxi.framework.compiler.token.CompilerTokenJRC;
import foxi.framework.compiler.token.CompilerTokenMOVA;
import foxi.framework.compiler.token.CompilerTokenMOVB;
import foxi.framework.compiler.token.CompilerTokenMOVC;
import foxi.framework.compiler.token.CompilerTokenNOP;
import foxi.framework.compiler.token.CompilerTokenPUSHA;
import foxi.framework.compiler.token.CompilerTokenPUSHB;
import foxi.framework.compiler.token.CompilerTokenPUSHC;
import foxi.framework.compiler.token.CompilerTokenWRITEA;
import foxi.framework.compiler.token.CompilerTokenWRITEB;
import foxi.framework.compiler.token.CompilerTokenWRITEC;
import foxi.framework.compiler.token.CompilerTokenWRITED;
import foxi.framework.compiler.token.CompilerTokenWRITEE;
import foxi.framework.compiler.token.CompilerTokenWRITEF;
import foxi.framework.compiler.token.CompilerTokenWRITEG;
import foxi.framework.compiler.token.CompilerTokenWRITEH;
import foxi.framework.compiler.token.CompilerTokenWRITEI;
import foxi.framework.components.Memory;
import foxi.framework.components.Register;
import foxi.framework.components.Stack;
import foxi.framework.gfx.SimpleDisplayFrame;
import foxi.framework.gfx.VRAMHelper;
import foxi.framework.gfx.VRAMPainter;
import foxi.framework.lexer.Lexer;
import foxi.framework.lexer.token.LexerTokenADD;
import foxi.framework.lexer.token.LexerTokenJR;
import foxi.framework.lexer.token.LexerTokenMOV;
import foxi.framework.lexer.token.LexerTokenNOP;
import foxi.framework.lexer.token.LexerTokenPUSH;
import foxi.framework.lexer.token.LexerTokenWRITE;

public class FoxiMain {
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
	static void imageTest() {
		Memory mem = new Memory(0x10000000);
		File imgFile = new File("C:\\Users\\mma\\Pictures\\thank_talos_its_fredas.jpg");
//		File imgFile = new File("C:\\Users\\mma\\Pictures\\test.bmp");
		VRAMHelper.loadImageIntoMemory(mem, imgFile);
		mem.writeDW(0, 0xffff0000);
		mem.writeDW(4, 0xff00ff00);
		mem.writeDW(8, 0xff0000ff);
		SimpleDisplayFrame.open(mem.memory, 580, 485, 0, 1);
//		open("BufferedImage", getImageLabel(getImage("C:\\Users\\mma\\Pictures\\test.bmp")));
//		open("VRAM Image", getImageLabel(testImage(mem.memory)));
//		open("TEST", getImageLabel(testSome(mem.memory)));
	}
	public static void main(String[] args) {
		String[] commands = {
			// JUST SOME TESTS
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
			
			// INITIALIZE BX as alpha color register
			"MOV %BX 00000000h",
			// PREPARE COLORS
			"WRITE 0 00h", //A
			"WRITE 1 ffh", //R
			"WRITE 2 00h", //G
			"WRITE 3 00h", //B
			
			"WRITE 4 00h", //A
			"WRITE 5 00h", //R
			"WRITE 6 ffh", //G
			"WRITE 7 00h", //B

			"WRITE 8 00h", //A
			"WRITE 9 00h", //R
			"WRITE 10 00h", //G
			"WRITE 11 ffh", //B
			
			// LOOP TO CHANGE THE ALPHA VALUE FOR ALL 3 PIXELS
			"ADD %BX 1",
			"WRITE 0 %BX",
			"WRITE 4 %BX",
			"WRITE 8 %BX",
			"JR -5"
		};
		
		Lexer lexer = new Lexer();
		lexer.addToken("NOP", new LexerTokenNOP());
		lexer.addToken("MOV", new LexerTokenMOV());
		lexer.addToken("JR", new LexerTokenJR());
		lexer.addToken("PUSH", new LexerTokenPUSH());
		lexer.addToken("WRITE", new LexerTokenWRITE());
		lexer.addToken("ADD", new LexerTokenADD());
		Compiler compiler = new Compiler();
		compiler.addToken("NOP", new CompilerTokenNOP());
		compiler.addToken("MOVA", new CompilerTokenMOVA());
		compiler.addToken("MOVB", new CompilerTokenMOVB());
		compiler.addToken("MOVC", new CompilerTokenMOVC());
		compiler.addToken("ADDA", new CompilerTokenADDA());
		compiler.addToken("ADDB", new CompilerTokenADDB());
		compiler.addToken("ADDC", new CompilerTokenADDC());
		compiler.addToken("JRA", new CompilerTokenJRA());
		compiler.addToken("JRB", new CompilerTokenJRB());
		compiler.addToken("JRC", new CompilerTokenJRC());
		compiler.addToken("PUSHA", new CompilerTokenPUSHA());
		compiler.addToken("PUSHB", new CompilerTokenPUSHB());
		compiler.addToken("PUSHC", new CompilerTokenPUSHC());
		compiler.addToken("WRITEA", new CompilerTokenWRITEA());
		compiler.addToken("WRITEB", new CompilerTokenWRITEB());
		compiler.addToken("WRITEC", new CompilerTokenWRITEC());
		compiler.addToken("WRITED", new CompilerTokenWRITED());
		compiler.addToken("WRITEE", new CompilerTokenWRITEE());
		compiler.addToken("WRITEF", new CompilerTokenWRITEF());
		compiler.addToken("WRITEG", new CompilerTokenWRITEG());
		compiler.addToken("WRITEH", new CompilerTokenWRITEH());
		compiler.addToken("WRITEI", new CompilerTokenWRITEI());
		Framework fw = new Framework(new Register(), new Stack(32), new Memory(800 * 800 * 2), compiler.compile(lexer.lex(Arrays.asList(commands))));


		fw.register.newRegister("AX", 0x1234);
		fw.register.newRegister("BX", 0);
		fw.register.newRegister("CX", 0);
		fw.register.newRegister("DX", 0);
		fw.register.newRegister("IP", 0);
		fw.register.newRegister("SP", fw.stack.size());
		fw.setPointerNames("IP", "SP");
		
		
		SimpleDisplayFrame.open(fw.memory.memory, 8, 8, 0, 8);
		
		Thread th = new Thread(new FrameworkRunner(fw, FoxiMain::next));
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



// COMPONENTS
