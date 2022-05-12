import java.util.HashSet;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Arrays;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.lang.StringBuilder;

public class LoadHandler {

	public HashMap<String, Callable> loaded;
	public HashMap<String, Callable> manpages;
	private Path directory;
	private Path base;
	
	private HashMap<String, Callable> help;
	private Cons c;

	public LoadHandler(String[] args, Cons c) {
		try {
			this.base = Paths.get(".").normalize().toRealPath().toAbsolutePath();
			this.directory = this.base;
		} catch (IOException e) {
			c.errorln("Exception loading dir: " + e.getMessage() + " from " + e.getCause());
			c.println("\nboot: \u001b[31mfailure\u001b[0m");
			System.exit(1);
		}
		this.c = c;
		loaded = new HashMap<>();
		help = new HashMap<>();
		manpages = Loader.loadMan("man.man", c);
		help = Loader.loadMan("help.man", c);

		c.println("loading progs:");
		initProgs(args);
		if (loaded.size() > 0) {
			printProgs();
		}
	}

	public void loadProg(String progName) {
		File f;
		int l;
		String n;
		f = new File(progName);
		if (!f.exists()) {
			c.errorln("file \u001b[0m'\u001b[33m" + f.getName() + "\u001b[0m'\u001b[31m does not exist");
			return;
		}
		Callable prog = Loader.loadProg(progName, this, c);
		l = progName.lastIndexOf("/") + 1;
		if (l < 0) l = 0;

		int di = progName.lastIndexOf(".");
		if (di < 0) di = progName.length();
		String key = progName.substring(l, di);
		String ext = progName.substring(di+1, progName.length());
		if (ext.equals("java")) {
			if (prog == null)
				c.errorln("Failed to create Prog");
			if (loaded.containsKey(key))
				c.println("Overwriting '" + key + "'");
			loaded.put(Character.toLowerCase(key.charAt(0)) + key.substring(1), prog);
			manpages.putAll(Loader.loadMan(progName.substring(0, di) + ".man", c));
		} else if (ext.equals("man")) {
			if (manpages.containsKey(key))
				c.println("Rekeying manpage: '" + key + "'");
			manpages.putAll(Loader.loadMan(key + ".man", c));
		} else {
			c.errorln("Unrecognized extension: " + ext);
		}
	}

	private void initProgs(String[] args) {
		for (String arg : args) {
			loadProg(arg);
		}
	}

	public boolean checkLen(String[] args, int min, int max, String msg) {
		boolean chk = checkLen(args, min, max);
		if (!chk) 
			c.errorDelayln("Bad num of args: " + msg, Cons.DELAY_MED);
		return chk;
	}

	public boolean checkLen(String[] args, int min, int max) {
		return args.length >= min && args.length <= max;
	}

	public void help(String[] inargs) {
		String s = "";
		Callable call;
		if (inargs.length < 2)
			s = "\u001b[35m(h)elp|? [cmd]\n(e)xit\n(p)rog [prog]\n(m)an [directive]\n(c)lear\ncd <directory>\n(l)s [directory]\n(load|ld <path>\u001b[0m";
		else if (inargs.length == 2 && (call = help.get(inargs[1])) != null) {
			call.run(inargs);
		} else s = "\u001b[31mUnrecognized input for: \u001b[35m(h)elp|? [directive]\u001b[0m";
		if (s != null) c.println(s);
	}

	public void prog(String[] args) {
		if (args.length < 2) {
			printProgs();
			return;
		}
		if (!loaded.containsKey(args[1])) {
			c.println("No such prog: '\u001b[33m" + args[1] + "\u001b[0m'");
			return;
		}
		String p;

		Callable prog = loaded.get(args[1]);
		if (prog == null) 
			c.errorln("ERROR: no entry for prog:\u001b[0m '\u001b[33m" + args[1] + "'");
		else {
			String[] pargs = Arrays.copyOfRange(args, 2, args.length);
			prog.run(pargs);
		}

	}

	public void printProgs() {
		if (loaded.isEmpty())
			c.println("No progs loaded. See '\u001b[33mhelp prog\u001b[0m'");
		StringBuilder buf = new StringBuilder("\u001b[33m");
		for (String key : loaded.keySet())
			buf.append("* ").append(key).append('\n');
		buf.append("\u001b[0m");
		c.print(buf.toString());
	}

	public void clear() {
		// prints clear screen ANSI code
		// then prints cursor to 0,0 ANSI
		System.out.print("\u001b[2J\u001b[;H");
	}

	public void man(String[] args) {
		String h = args.length > 1 ? args[1] : "";
		Callable m = manpages.get(h);
		if (m == null) c.println("No man page for '" + h + "'");
		else m.run(null);
	}

	public void load(String[] args) {
		if (args.length < 2) {
			c.println("Expected load|ld <progName>");
		}
		String dir = getRelativeDirectory().resolve(args[1]).toString();
		loadProg(dir);
	}

	public void ls(String[] args) {
		File dir = args.length < 2 ? directory.toFile() : getRelativeDirectory().resolve(args[1]).toFile();
		File[] files = dir.listFiles();
		if (files.length == 0) {
			c.println("");
			return;
		}
		var buf = new StringBuilder();
		buf.append("\u001b[36m");
		for (File f : files) {
			if (f.isDirectory())
				buf.append(f.getName()).append('\n');
		}
		buf.append("\u001b[0m");
		c.print(buf.toString());
	}

	public void cd(String[] args) {
		if (!checkLen(args, 2, 2, "Exp: cd <directory>"))
			return;
		try {
			Path p = Paths.get(directory.toString(), args[1]).toRealPath().toAbsolutePath();
			if (p.compareTo(base) < 0) {
				c.errorln("Can not navigate below pConsole dir");
				return;
			}
			p = base.resolve(p);
			directory = p;
			directory = directory.normalize();
		} catch (InvalidPathException | IOException e) {
			c.errorln("Bad path: " + e.getMessage());
		}
	}

	public Path getDirectory() {
		return directory;
	}

	public Path getRelativeDirectory() {
		return base.relativize(directory);
	}

	public Path getBase() {
		return base;
	}

}
