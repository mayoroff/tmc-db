package org.tmcdb;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Pavel Talanov
 */
public final class Console {
    private Console() {
    }

    public static void main(String[] args) {
        File dbDir = new File("test.db");
        runDb(dbDir);
    }

    private static void runDb(@NotNull File dbDir) {
        //noinspection ResultOfMethodCallIgnored
        dbDir.mkdirs();
        Database db = new Database(dbDir, System.out);
        try {
            db.initialize();
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(System.in));
            try {
                String query = in.readLine();
                while (query != null) {
                    db.exec(query);
                    query = in.readLine();
                }
            } catch (IOException e) {
                System.err.println("Error reading from stdin");
            }
        } finally {
            db.deinitialize();
        }
    }
}
