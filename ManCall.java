import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.lang.StringBuilder;

public class ManCall extends Callable {

	private StringBuffer t;
	protected final String key;
	private String txt;

	public ManCall(String filename, String key, Cons c) {
		super(filename, c);
		this.key = key;
		this.t = new StringBuffer();
	}

	@Override
	public void run(String[] args) {
		super.run(args);
		c.printDelayln(txt, Cons.DELAY_QUICK);
	}

	@Override
	public void load() {
		c.println("Caching " + file.getName() + " [" + key + "]");
		txt = "";
		boolean found = false;
		String in;
		String[] args;
		String[] keys;
		int ibk;
		t.setLength(0);
		t.append("\n");
		try (Scanner s = new Scanner(file)) {
			ANSIParser splicer = new ANSIParser();
			while (s.hasNextLine()) {
				in = s.nextLine();
				if (in.isBlank()) continue;
				args = in.split(",");
				keys = args[0].split("\\|");
				for (String k : keys) {
					if (k.equals(this.key)) {
						found = true;
						break;
					}
				}
				if (!found) {
					while (s.hasNextLine() && in.endsWith("\\"))
						in = s.nextLine();
					continue;
				} else {
					for (int i = 1; i < args.length; i++) {
						t.append(args[i]);
						if (i < args.length - 1)
							t.append(',');
					}
				}
				t.append("\n");
				while (in.endsWith("\\")) {
					ibk = t.lastIndexOf("\\");
					if (ibk > -1)
						t.deleteCharAt(ibk);
					in = s.nextLine();
					t.append(in);
					t.append("\n");
				}
				txt = t.toString();
				txt = splicer.insertANSI(txt);
				break;
			}
		} catch (FileNotFoundException e) {
			c.println("Failed to load. No file by name '" + file.getName() + "'");
		}
	}

}
