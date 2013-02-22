package org.tmcdb;

import java.io.File;
import java.io.IOException;

/**
 * @author Pavel Talanov
 */
public final class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage:\ntmcdb <dir>");
            return;
        }
        String path = args[0];
        Console.runDb(new File(path));
    }
}
