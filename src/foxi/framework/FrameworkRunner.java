package foxi.framework;

import foxi.framework.command.Command;
import foxi.framework.command.INextCommand;

public class FrameworkRunner implements Runnable {
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