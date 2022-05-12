import java.io.File;

public class LoadAll extends Prog {

	public void run(String[] args) {
		if (args.length < 1) {
			c.println("Expected LoadAll <dirName>");
			return;
		}
		File dir = loadHandler.getRelativeDirectory().resolve(args[0]).normalize().toFile();
		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.getName().endsWith(".java"))
				loadHandler.loadProg(f.getAbsolutePath());
		}
	}
}
