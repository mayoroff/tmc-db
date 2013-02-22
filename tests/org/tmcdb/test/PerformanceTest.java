package org.tmcdb.test;

import org.tmcdb.Database;
import org.tmcdb.utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author Pavel Talanov
 */
public final class PerformanceTest {
    private static final String SANDBOX_ROOT = "testData/performance";

    public static void main(String[] args) throws IOException {
        performTest(400000);
        /*TestUtils.cleanDirectory(SANDBOX_ROOT);
        for (int i = 0; i <= 200000; i += 10000) {
            performTest(i);
        }  */
        TestUtils.cleanDirectory(SANDBOX_ROOT);
    }

    private static void performTest(int insertionsNumber) throws IOException {
        File file = new File(SANDBOX_ROOT + "/" + insertionsNumber);
        file.mkdirs();
        //PrintStream dummyOut = new PrintStream(new ByteOutputStream());
        PrintStream dummyOut = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                //do nothing
            }
        });
        Database database = new Database(file, dummyOut);
        try {
            database.initialize();
            database.exec("CREATE TABLE a(a INT, b INT, c VARCHAR(80), d VARCHAR(80))");
            long start = System.currentTimeMillis();
            database.exec("CREATE INDEX myind ON a(ASC a) USING BTREE");
            for (int i = 0; i < insertionsNumber; ++i) {
                database.exec("INSERT INTO a(a, b) VALUES(3, 5)");
            }
            database.exec("CREATE INDEX myind ON a(ASC a) USING BTREE");
            long end = System.currentTimeMillis();
            System.out.println(insertionsNumber + " insertions: " + (end - start) + " millis");
        } finally {
            database.deinitialize();
        }
    }
}
