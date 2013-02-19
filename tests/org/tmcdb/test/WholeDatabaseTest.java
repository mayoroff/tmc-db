package org.tmcdb.test;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import junit.framework.Assert;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmcdb.Database;
import org.tmcdb.utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static org.tmcdb.utils.TestUtils.cleanDirectory;

/**
 * @author Pavel Talanov
 */
public class WholeDatabaseTest {

    private static final String SANDBOX = "testData/integration/sandbox";

    private static final String CASES = "testData/integration/cases";


    @Test
    public void simpleCreateTable() throws Exception {
        ByteOutputStream out = new ByteOutputStream();
        List<String> queries = TestUtils.readFileIntoList(CASES + "/simpleCreate/in.txt");
        Database db = new Database(new File(SANDBOX), new PrintStream(out));
        try {
            db.initialize();
            for (String query : queries) {
                db.exec(query);
            }
            Assert.assertEquals(TestUtils.readFileIntoString(CASES + "/simpleCreate/out.txt"), out.toString());
        } finally {
            db.deinitialize();
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        new File(SANDBOX).mkdirs();
    }

    @After
    public void cleanup() throws IOException {
        cleanDirectory(SANDBOX);
    }
}
