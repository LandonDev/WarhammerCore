package landon.jurassiccore.utils;
import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;

public class ManagedFile {
    public ManagedFile(String filename) {
        this.file = new File(JurassicCore.getInstance().getDataFolder(), filename);
        if (this.file.exists()) {
            try {
                if (checkForVersion(this.file, JurassicCore.getInstance().getDescription().getVersion()) && !this.file.delete()) {
                    throw new IOException("Could not delete file " + this.file.toString());
                }
            }
            catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        if (!this.file.exists())
            try {
                copyResourceAscii("/" + filename, this.file);
            }
            catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "ITEMS.CSV FILE NOT LOADED!");
            }
    }
    private static final int BUFFERSIZE = 8192; private final File file;

    public static void copyResourceAscii(String resourceName, File file) throws IOException {
        try (final InputStreamReader reader = new InputStreamReader(ManagedFile.class.getResourceAsStream(resourceName))) {
            final MessageDigest digest = getDigest();
            try (final DigestOutputStream digestStream = new DigestOutputStream(new FileOutputStream(file), digest)) {
                try (final OutputStreamWriter writer = new OutputStreamWriter(digestStream)) {
                    final char[] buffer = new char[BUFFERSIZE];
                    do {
                        final int length = reader.read(buffer);
                        if (length >= 0) {
                            writer.write(buffer, 0, length);
                        } else {
                            break;
                        }
                    } while (true);
                    writer.write("\n");
                    writer.flush();
                    final BigInteger hashInt = new BigInteger(1, digest.digest());
                    digestStream.on(false);
                    digestStream.write('#');
                    digestStream.write(hashInt.toString(16).getBytes());
                }
            }
        }
    }

    public static boolean checkForVersion(File file, String version) throws IOException {
        if (file.length() < 33L) {
            return false;
        }
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] buffer = new byte[(int)file.length()];
            int position = 0;
            do {
                int length = bis.read(buffer, position, Math.min((int)file.length() - position, 8192));
                if (length < 0) {
                    break;
                }
                position += length;
            } while (position < file.length());
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            if (bais.skip(file.length() - 33L) != file.length() - 33L) {
                return false;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(bais));
            try {
                String hash = reader.readLine();
                if (hash != null && hash.matches("#[a-f0-9]{32}")) {
                    hash = hash.substring(1);
                    bais.reset();
                    String versionline = reader.readLine();
                    if (versionline != null && versionline.matches("#version: .+")) {
                        String versioncheck = versionline.substring(10);
                        if (!versioncheck.equalsIgnoreCase(version)) {
                            bais.reset();
                            MessageDigest digest = getDigest();
                            DigestInputStream digestStream = new DigestInputStream(bais, digest);
                            try {
                                byte[] bytes = new byte[(int)file.length() - 33];
                                digestStream.read(bytes);
                                BigInteger correct = new BigInteger(hash, 16);
                                BigInteger test = new BigInteger(1, digest.digest());
                                if (correct.equals(test)) {
                                    return true;
                                }
                                Bukkit.getLogger().warning("File " + file.toString() + " has been modified by user and file version differs, please update the file manually.");
                            } finally {

                                digestStream.close();
                            }
                        }
                    }
                }
            } finally {

                reader.close();
            }
        } finally {

            bis.close();
        }
        return false;
    }

    public static MessageDigest getDigest() throws IOException {
        try {
            return MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }

    public List<String> getLines() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.file));
            try {
                List<String> lines = new ArrayList<String>();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    lines.add(line);
                }
                return lines;
            } finally {

                reader.close();
            }

        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }
}
