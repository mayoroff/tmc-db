package org.tmcdb;

import java.io.File;

/**
 * @author Pavel Talanov
 */
public final class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage:\ntmcdb <dir>");
            return;
        }
        String path = args[0];
        File file = new File(path);
        if (!file.isDirectory()) {
            System.out.println(path + " should be a directory.");
        }
        Console.runDb(file);
    }
}
