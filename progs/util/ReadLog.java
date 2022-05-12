public class ReadLog extends Prog {

	public void run(String[] args) {
		if (args[0] < 1) {
			c.println("Expects: ReadLog <logPath>");
			return;
		}
		c.printRead(loadHandler.getDirectory().resolve(args[0]).normalize().toString());
	}
}
