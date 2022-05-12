import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Encrypt extends Prog {

	public void run(String[] args) {
		if (args.length < 1) {
			c.println("Expects: Encrypt <textFile> [encodedFile] [keyFile]");
			c.println("Defaults to textFile.enc");
			return;
		}
		File toEncode = loadHandler.getDirectory().resolve(args[0]).toFile();
		if (!toEncode.exists()) {
			c.println(toEncode.getName() + " does not exist.");
			return;
		}
		String name = toEncode.getName();
		int ni = 0;
		if ((ni = name.lastIndexOf(".")) > 0)
			name = name.substring(0, ni);
		File encoded = new File(toEncode.getParent(), args.length < 2 ? name + ".enc" : args[1]);
		if (encoded.exists()) {
			c.println("The file " + encoded.getName() + " exists.");
			if (!c.confirm("Overwrite %s?", encoded.getName())) {
				return;
			}
		}
		File key = new File("." + name + ".key");

		try (FileInputStream in = new FileInputStream(toEncode)) {
			try (FileOutputStream out = new FileOutputStream(encoded)) {
				try (FileOutputStream k = new FileOutputStream(key)) {
					Random rand = new Random();
					byte b;
					byte[] n = (toEncode.getName() + "\n").getBytes();
					byte[] bk = new byte[n.length];
					rand.nextBytes(bk);
					for (int i = 0, bki = 0; i < n.length; bki++,i++) {
						out.write(bk[bki] ^ n[i]);
						k.write(bk[bki]);
					}
					bk = new byte[Long.BYTES];
					for (int i = 0, bki = 0; in.available() > 0; i++, bki += (bki++) % Long.BYTES) {
						if (bki == 0)
							rand.nextBytes(bk);
						b = (byte)in.read();
						out.write(bk[bki] ^ b);
						k.write(bk[bki]);
						bk[bki] = (byte)0;
					}
				}
			}
		} catch (IOException e) {
			c.errorln("Failed to encrypt: " + e.getMessage());
		}
		c.printf("Encrypted '%s' to '%s'.\n", toEncode.getName(), encoded.getName());
	}

}
