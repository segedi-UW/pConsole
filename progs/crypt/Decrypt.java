import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Decrypt extends Prog {

	public void run(String[] args) throws Throwable {
		if (args.length < 1) {
			c.println("Expects: Decrypt <encodedFile> [keyFile]");
			return;
		}
		File toDecode = loadHandler.getDirectory().resolve(args[0]).toFile();
		if (!toDecode.exists()) {
			c.println(toDecode.getName() + " does not exist.");
			return;
		}
		String name = toDecode.getName();
		int ni = 0;
		if ((ni = name.lastIndexOf(".")) > 0)
			name = name.substring(0, ni);
		File key = new File(args.length < 2 ? "." + name + ".key" : args[1]);
		if (!key.exists()) {
			c.println(args.length < 2 ? "There is no default key for " + args[0] : "The key " + args[1] + " does not exist");
			return;
		}

		if (key.length() != toDecode.length()) {
			c.errorln("Key length != " + toDecode.getName() + "'s length");
			return;
		}

		File decoded;
		try (FileInputStream in = new FileInputStream(toDecode)) {
			try (FileInputStream k = new FileInputStream(key)) {
				FilterInputStream fin = (FilterInputStream) loadSubclass(this, "Filter", new Object[]{in, k});
				BufferedReader reader = new BufferedReader(new InputStreamReader(fin));

				String n = "";
				char ch;
				// read the name out of the stream
				while ((ch = (char)reader.read()) != '\n') {
					n += ch;
				}

				decoded = new File(toDecode.getParent(), n);
				if (decoded.exists()) {
					c.println("The file " + decoded.getName() + " exists.");
					if (!c.confirm("Overwrite %s?", decoded.getName())) {
						return;
					}
				}
				try (FileWriter out = new FileWriter(decoded)) {
					while (reader.ready()) {
						out.write(reader.read());
					}
				}

			}			
			c.printf("Decrypted '%s' to '%s'.\n", toDecode.getName(), decoded.getName());
		} catch (IOException e) {
			c.errorln("Failed to decrypt: " + e.getMessage());
			return;
		}
	}

	/**
	 * xor's the input of the input stream with the key input stream
	 */
	public static class Filter extends FilterInputStream {

		private InputStream kin;

		// InputStream in, InputStream kin
		public Filter(Object[] args) {
			super((InputStream)args[0]);
			this.kin = (InputStream)args[1];
		}

		@Override
		public int read() throws IOException {
			return in.read() ^ kin.read();
		}

		@Override
		public int read(byte[] b) throws IOException {
			return read(b, 0, b.length);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			len = in.read(b, off, len);
			for (int i = off; i < len; i++) {
				b[i] ^= kin.read();
			}
			return len;
		}
	}

}
