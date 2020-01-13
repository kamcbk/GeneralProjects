package gitlite;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;


/** Assorted utilities.
 *  @author P. N. Hilfinger
 */
class Utils {

    /*******SHA-1 HASH VALUES*******/

    /** The length of a complete SHA-1 UID as a hexadecimal numeral. */
    static final int UID_LENGTH = 40;

    /** Returns the SHA-1 hash of the concatenation of VALS, which may
     *  be any mixture of byte arrays and Strings. */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }


    /******READING AND WRITING FILE CONTENTS****/

    /** Return the entire contents of FILE as a byte array.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Same as above, except input is a String.*/
    static byte[] readContents(String path) {
        return readContents(new File(path));
    }

    /** Return the entire contents of FILE as a String.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /**Same as above, except input is a String.*/
    static String readContentsAsString(String str) {
        return readContentsAsString(new File(str));
    }

    /** Write the result of concatenating the bytes in CONTENTS to FILE,
     *  creating or overwriting it as needed.  Each object in CONTENTS may be
     *  either a String or a byte array.  Throws IllegalArgumentException
     *  in case of problems. */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                    new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**Same as above. Takes in a string instead.*/
    static void writeContents(String str, Object... contents) {
        writeContents(new File(str), contents);
    }

    /*********DELETING STAGING AREA**********/

    /**Clearing staging area.*/
    static void clearStagingArea() {
        File stagingArea = new File(".gitlite/staging-area/");
        File[] stagingAreaFiles = stagingArea.listFiles();
        for (File stagingAreaFile : stagingAreaFiles) {
            stagingAreaFile.delete();
        }
    }

    /*********JOIN UTILITIES**********/

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {java.nio.file.Paths.#get(String, String[])}
     *  method. */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {java.nio.file.Paths.#get(String, String[])}
     *  method. */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }


    /********SERIALIZATION UTILITIES*********/

    /**Serialize an object into a file (writing bytes to a file).*/
    static void serialize(File file, Object obj) {
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(file));

            out.writeObject(obj);
            out.close();
        } catch (IOException excp) {
            throw new GitliteException("Unable to serialize.");
        }
    }
    /**Serialize an object given it's path as a string.*/
    static void serialize(String path, Object obj) {
        File temp = new File(path);
        serialize(temp, obj);

    }

    /**De-Serialize an object (bytes) from a file. Returns object.*/
    static Object deserialize(File file) {
        Object obj;
        try {
            ObjectInputStream inp =
                    new ObjectInputStream(new FileInputStream(file));
            obj = (Object) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            obj = null;
        }
        return obj;
    }

    /**De-Serialize an object given it's path as a string*/
    static Object deserialize(String path) {
        File temp = new File(path);
        return deserialize(temp);
    }

    /**Compare two files' contents given their path files.*/
    static boolean compare(File file1, File file2) {
        byte[] content0 = Utils.readContents(file1);
        byte[] content1 = Utils.readContents(file2);
        return Arrays.equals(content0, content1);
    }

    /**Compare two files' contents given their string files*/
    static boolean compare(String path1, String path2) {
        return compare(new File(path1), new File(path2));
    }


    /********PRINTING COMMIT INFORMATION*********/


    /**Format to print messages from a UnitCommit*/
    static void printMessage(UnitCommit unitCommit) {
        System.out.println("===");
        System.out.println("commit " + unitCommit.returnSha1ID());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "E LLL d HH:mm:ss yyyy Z").withZone(ZoneId.systemDefault());

        System.out.println("Date: " + formatter.format(unitCommit.returnTimeStamp()));
        System.out.println(unitCommit.returnCommitMsg());
    }

}
