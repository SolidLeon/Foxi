package foxi.framework;

public class Utils {
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