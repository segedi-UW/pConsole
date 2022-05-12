import java.io.File;

public class Logs extends Prog {

	public void run(String[] args) {
		File dir = args.length < 1 ? loadHandler.getDirectory().toFile()
			: loadHandler.getDirectory().resolve(args[0])
			.normalize().toFile();
		File[] files = dir.listFiles();
		if (files == null) {
			c.errorln("Directory does not exist: " + dir.getAbsolutePath());
			return;
		}

		for (File f : files) {
			if (f.getName().endsWith(".log"))
				c.println("- " + f.getName());
		}
	}
}
