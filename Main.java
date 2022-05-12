import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.io.File;
import java.io.PrintStream;
import java.io.IOException;

public class Main {

	private static String log_file = "pConsole_errlog";

	private Cons c;
	private LoadHandler loadHandler;
	private String lastProg;

	public Main(String[] args) {
		c = new Cons(Cons.DELAY_SHORT);
		try {
			File log = new File(log_file);
			if (!log.delete())
				c.errorln("Failed to truncate '" + log_file + "'");
			System.setErr(new PrintStream(log));
		} catch (IOException e) {
			c.errorln("WARNING: Failed to open error logfile '" + log_file + "'");
			System.setErr(null);
		}
		loadHandler = new LoadHandler(args, c);
		c.println("Loading dir: " + loadHandler.getDirectory());
		c.print("Booting");
		c.printLoading('.', 1000);
		c.println("\nboot: \u001b[32msuccess\u001b[0m");
		lastProg = "p";
	}

	private void run() {
		String in;
		do {
			in = c.readLine("%s > ", loadHandler.getRelativeDirectory());
		} while (interp(in));
	}

	private boolean interp(String in) {
		if (in.isBlank()) return true;
		else if (in.equals(".")) in = lastProg;
		String[] inargs = in.split(" ");
		switch (inargs[0]) {
			case "exit":
			case "e":
				return false;
			case "cd":
				loadHandler.cd(inargs);
				break;
			case "help":
			case "h":
			case "?":
				loadHandler.help(inargs);
				break;
			case "man":
			case "m":
				loadHandler.man(inargs);
				break;
			case "ls":
			case "l":
				loadHandler.ls(inargs);
				break;
			case "ld":
			case "load":
				loadHandler.load(inargs);
				break;
			case "prog":
			case "p":
				loadHandler.prog(inargs);
				lastProg = in;
				break;
			case "c":
			case "clear":
				loadHandler.clear();
				break;
			default:
				c.printDelay("Unknown cmd '" + inargs[0] + "', use '(h)elp' or '?' for help\n", Cons.DELAY_QUICK);
				break;
		}
		return true;
	}

	private void exit() {
		c.print("Terminating");
		c.printLoading('.', 200);
		c.printDelay("\n", 0);
	}

	public static void main(String[] args) {
		Main m = new Main(args);
		m.run();
		m.exit();
	}


}
