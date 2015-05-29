package foxi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * command <arg> <arg> <arg>...
 * Args are space delimited.
 * The last arg is taken as whole
 * @author MMA
 *
 */
public class FoxiCompiler implements Runnable {
	
	// "RET" -> (parts, cmd, remainder, args) -> parts.add(cmd)
	interface IAddParts {
		void addParts(List<String> result, String cmd, String remainder, String[] args);
	}
	Map<String, IAddParts> makeParts = new HashMap<>();
	private String file;
	public FoxiRuntime runtimeResult;
	
	public FoxiCompiler(String file) {
		this.file = file;

		makeParts.put("RET", (result, cmd, remainder, args) -> result.add(cmd));
		makeParts.put("PRINT", (result, cmd, remainder, args) -> {result.add(cmd); result.add(remainder);} );
	}
	
	public List<String> parseLines(List<String> lines) {
		List<String> parts = new ArrayList<>();
		for (String line : lines) {
			parts.addAll(parseLine(line));
		}
		return parts;
	}
	
	public List<String> parseLine(String line) {
		List<String> parts = new ArrayList<>();
		line = line.trim();
		if (line.isEmpty())
			return parts;
		if (line.charAt(0) == ';')
			return parts;
		
		int idx = line.indexOf(' ');
		String cmd = null;
		String remainder = null;
		if (idx == -1) {
			cmd = line;
			remainder = "";
		} else {
			cmd = line.substring(0, idx);
			remainder = line.substring(idx).trim();
		}
		String[] args = remainder.split(" ");
		
		addParts(parts, cmd, remainder, args);
		
		return parts;
	}

	private void addParts(List<String> parts, String cmd, String remainder, String[] args) {
		switch (cmd) {
		case "RET":
			parts.add(cmd);
			break;
		case "MOV":
			if (args.length != 2) 
				throw new RuntimeException("Invalid argument count for MOV");
			String dst = args[0].trim();
			String src = args[1].trim();
			parts.add(cmd);
			parts.add(dst);
			parts.add(src);
			break;
		case "PRINT":
			if (args.length < 2)
				throw new RuntimeException("Invalid argument count for PRINT");
			parts.add(cmd);
			parts.add(remainder);
			break;
		}
	}

	@Override
	public void run() {
		try {
			List<String> lines = Files.readAllLines(Paths.get(file));
			System.out.println("== COMPILE RESULT ==");
			List<String> compileResult = parseLines(lines);
			for (String s : compileResult)
				System.out.println(s);
			System.out.println("====================");
			runtimeResult = new FoxiRuntime();
			runtimeResult.memory = compileResult.toArray(new String[0]);
			runtimeResult.register = new HashMap<>();
			runtimeResult.register.put("IP", 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

