package org.tmcdb.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;

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
            boolean success = f.delete();
            while (!success) {
                success = f.delete();
            }
        }
    }
}
