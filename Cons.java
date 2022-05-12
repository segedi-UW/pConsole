import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.util.Iterator;
import java.util.Scanner;
import java.util.List;
import java.util.LinkedList;

public class Cons {

	public static int DELAY_QUICK = 5;
	public static int DELAY_SHORT = 10;
	public static int DELAY_MED = 20;
	public static int DELAY_LONG = 30;

	private int defaultDelay;
	private final Scanner in;

	public Cons(int defaultDelay) {
		in = new Scanner(System.in);
		this.defaultDelay = defaultDelay;
	}

	public void setDefaultDelay(int defaultDelay) {
		this.defaultDelay = defaultDelay;
	}

	public int getDefaultDelay() {
		return this.defaultDelay;
	}

	public void printDelay(String msg, int charDelay) {
		char[] chars = msg.toCharArray();
		for (char c : chars) {
			try {
				Thread.sleep(charDelay);
			} catch (InterruptedException ignored) {
			}
			System.out.printf("%c", c);
		}
	}

	public void error(String err) {
		print("\u001b[31m" + err + "\u001b[0m");
	}

	public void errorln(String err) {
		println("\u001b[31m" + err + "\u001b[0m");
	}

	public void errorDelay(String err, int charDelay) {
		printDelay("\u001b[31m" + err + "\u001b[0m", charDelay);
	}

	public void errorDelayln(String err, int charDelay) {
		printDelayln("\u001b[31m" + err + "\u001b[0m", charDelay);
	}

	public void printDelayf(String msg, int charDelay, Object ... args) {
		printDelay(String.format(msg, args), charDelay);
	}

	public void printf(String msg, Object ... args) {
		printDelayf(msg, defaultDelay, args);
	}

	public void print(String msg) {
		printDelay(msg, defaultDelay);
	}

	public void println(String msg) {
		printDelay(msg + "\n", defaultDelay);
	}

	public void printDelayln(String msg, int delay) {
		printDelay(msg + "\n", delay);
	}

	public void printLoading(char fill, long millis) {
		long start = System.nanoTime();
		while (System.nanoTime() - start < millis * 1000000) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ignored){
			}
			System.out.printf("%c", fill);
		}
	}

	public String readLine(String msg, Object ... args) {
		printf(msg, args);
		return in.nextLine().trim();
	}

	public boolean confirm(String msg, Object ... args) {
		String in;
		do {
			in = readLine(msg + " (y/n): ", args);
			in = in.trim();
		} while (!in.equalsIgnoreCase("y") && !in.equalsIgnoreCase("n"));
		return in.equalsIgnoreCase("y");
	}

	public void printRead(String path) {
		ReadPrinter rp = new ReadPrinter(path);
		Thread t = new Thread(rp);
		t.setDaemon(true);
		println("Press <enter> to skip...");
		t.start();
		in.nextLine();
		if (t.isAlive()) {
			rp.setRunning(false);
			t.interrupt();
			try {
				t.join();
			} catch (InterruptedException ignored) {
			}
		}
		System.out.println("\u001b[0m");
	}

	private class ReadPrinter implements Runnable {

		private volatile boolean isRunning;
		private String path;

		public ReadPrinter(String path) {
			this.path = path;
		}

		public void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
		}

		@Override
		public void run() {
			isRunning = true;
			try {
				Iterator<String> s = Files.lines(Path.of(path)).iterator();
				ANSIParser parser = new ANSIParser();
				// 260 wpm, est. 
				// avg 5 chars / word, 1300 cpm
				// ~25% whitespace, 1625 cpm (whitespace = 0 time)
				// 27 chars / s
				String ln;
				long start, slp;
				char[] chars;
				long waitMillis;
				while (isRunning && s.hasNext()) {
					ln = s.next();
					waitMillis = ln.length() * 1000 / 27;
					ln = parser.insertANSI(ln + "\n");
					start = System.currentTimeMillis();
					chars = ln.toCharArray();
					for (char c : chars) {
						if (!isRunning) break;
						try {
							Thread.sleep(defaultDelay);
						} catch (InterruptedException ignored) {
							if (!isRunning) break;
						}
						System.out.printf("%c", c);
					}
					slp = waitMillis - System.currentTimeMillis() - start;
					if (isRunning && slp > 0)
						Thread.sleep(slp);
				}
				if (isRunning)
					System.out.print("<enter> to continue");
			} catch (InterruptedException ignored) {
			} catch (IOException e) {
				errorln("Failed to printRead: " + e.getMessage());
			}
		}

	}

}
