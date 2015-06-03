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
import foxi.framework.Memory;
import foxi.framework.Register;
import foxi.framework.Stack;
import foxi.framework.VRAMHelper;
import foxi.framework.command.Command;
import foxi.framework.compiler.Compiler;
import foxi.framework.gfx.SimpleDisplayFrame;
import foxi.framework.gfx.VRAMPainter;
import foxi.framework.lexer.Lexer;

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



// COMPONENTS
