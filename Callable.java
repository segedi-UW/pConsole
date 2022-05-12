import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.io.File;

public abstract class Callable {
	private static String lmstr = "Modified:";
	private static String lrstr = "Cached:";
	private static int lmn = lmstr.length();

	protected File file;
	private long lastLoad;
	protected Cons c;

	public Callable(String filename, Cons c) {
		this.file = new File(filename);
		this.c = c;
		lastLoad = 0;
	}

	public void run(String[] args) {
		long lastMod = file.lastModified();
		if (lastLoad < lastMod) {
			String s = String.format("%" + lmn + "s %2$tT %2$tF\n", lmstr, 
					LocalDateTime.ofInstant(Instant.ofEpochMilli(lastMod), 
						ZoneOffset.UTC));
			if (lastLoad != 0)
				s += String.format("%"+ lmn + "s %2$tT %2$tF\n", lrstr, 
						LocalDateTime.ofInstant(Instant.ofEpochMilli(lastLoad), 
							ZoneOffset.UTC));
			else
				s += String.format("%" + lmn + "s %s\n", lrstr, "never");
			c.printDelay(s, 1);
			load();
		}
		lastLoad = System.currentTimeMillis();
	}

	public abstract void load();
}
