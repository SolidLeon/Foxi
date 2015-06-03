package foxi.framework.gfx;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

//DISPALY
public class SimpleDisplayFrame extends JFrame {
	public static SimpleDisplayFrame open(byte[] memory, int width, int height, int memoryOffset, int scale) {
		final SimpleDisplayFrame frm = new SimpleDisplayFrame();
		frm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frm.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frm.closing = true;
			}
		});
		Display display = new Display(memory, width, height, memoryOffset, scale);
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