import java.util.HashMap;
import java.io.File;
import java.lang.StringBuilder;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class Loader {

	public static Callable loadProg(String filename, LoadHandler loadHandler, Cons c) {
		return new ProgCall(filename, loadHandler, c);
	}

	public static HashMap<String, Callable> loadMan(String filename, Cons c) {
		String in;
		String[] args;
		String[] keys;
		ManCall mc;
		var map = new HashMap<String, Callable>();
		try (Scanner s = new Scanner(new File(filename))) {
			while (s.hasNextLine()) {
				in = s.nextLine();
				args = in.split(",");
				keys = args[0].split("\\|");
				mc = new ManCall(filename, keys[0], c);
				for (String k : keys) {
					map.put(k, mc);
				}
				while (s.hasNextLine() && in.endsWith("\\"))
					in = s.nextLine();
			}
		} catch (FileNotFoundException e) {
			c.println("\u001b[33mNo file by name '" + filename + "'\u001b[0m");
		}
		return map;
	}

}
