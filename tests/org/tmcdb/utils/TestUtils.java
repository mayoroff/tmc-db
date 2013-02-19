package org.tmcdb.utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Pavel Talanov
 */
public final class TestUtils {

    private TestUtils() {
    }

    public static void cleanDirectory(@NotNull String directory) {
        System.gc();
        File folder = new File(directory);
        final File[] files = folder.listFiles();
        assert files != null;
        for (File f : files) {
            //behaves very strange sometimes
            if (f.isDirectory()) {
                cleanDirectory(f.getAbsolutePath());
            }
            boolean success = f.delete();
            while (!success) {
                success = f.delete();
            }
        }
    }

    //taken from http://stackoverflow.com/a/326448
    @NotNull
    public static String readFileIntoString(@NotNull String path) throws IOException {
        File file = new File(path);
        StringBuilder fileContents = new StringBuilder((int) file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");
        try {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }


    @NotNull
    public static List<String> readFileIntoList(@NotNull String path) throws IOException {
        BufferedReader in = null;
        List<String> list = new ArrayList<String>();
        try {
            in = new BufferedReader(new FileReader(path));
            String str;
            while ((str = in.readLine()) != null) {
                list.add(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return list;
    }
}
