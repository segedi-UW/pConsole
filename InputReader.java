import java.lang.StringBuilder;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class InputReader implements Runnable {
	private StringBuilder buffer;
	private InputStream inputStream;
	private Thread thread;
	private Cons c;

	public InputReader(InputStream stream, Cons c) {
		this.buffer = new StringBuilder();
		this.inputStream = stream;
		this.thread = new Thread(this);
		this.thread.setDaemon(true);
		this.thread.start();
	}

	@Override
	public void run() {
		try (InputStreamReader input = new InputStreamReader(inputStream)) {
			while (input.ready()) {
				buffer.append((char)input.read());
			}
		} catch (IOException e) {
			c.println("Failed to read subprocess: " + e.getMessage());
		}
	}

	@Override
	public String toString() {
		try {
			if (thread.isAlive())
				thread.join();
		} catch (InterruptedException e) {
			c.errorln("Waiting prematurely ended.");
		}
		return buffer.toString();
	}
}
