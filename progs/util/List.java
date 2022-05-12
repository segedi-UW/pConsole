import java.io.File;
import java.util.Iterator;
import java.lang.StringBuilder;

public class List extends Prog {

	public void run(String[] args) {
		File dir = args.length < 1 ? loadHandler.getDirectory().toFile() : 
			loadHandler.getRelativeDirectory().resolve(args[0]).toFile();
		String[] names = dir.list();
		StringBuilder sb = new StringBuilder();
		sb.append("\u001b[33m");
		for (String s : names) {
			if (s.endsWith(".java"))
				sb.append(s).append('\n');
		}
		sb.append("\u001b[35m");
		for (String s : names) {
			if (s.endsWith(".man"))
				sb.append(s).append('\n');
		}
		sb.append("\u001b[0m");
		c.print(sb.toString());
	}
}
