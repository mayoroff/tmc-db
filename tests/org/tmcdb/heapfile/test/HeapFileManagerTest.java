package org.tmcdb.heapfile.test;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmcdb.engine.schema.Column;
import org.tmcdb.engine.schema.TableSchema;
import org.tmcdb.heapfile.HeapFile;
import org.tmcdb.heapfile.HeapFileManager;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static org.tmcdb.utils.TestUtils.cleanDirectory;

/**
 * @author Pavel Talanov
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public final class HeapFileManagerTest {

    private static final String TEST_DATA_DIR = "testData/engine/heapFileManager";
    public static final TableSchema TEST_SCHEMA_A = new TableSchema("a", Collections.<Column>emptyList());
    public static final TableSchema TEST_SCHEMA_B = new TableSchema("b", Collections.<Column>emptyList());

    @Test(expected = RuntimeException.class)
    public void getNonExistingFile() {
        File testDir = new File(TEST_DATA_DIR);
        HeapFileManager heapFileManager = new HeapFileManager(testDir);
        try {
            heapFileManager.getExistingFile(TEST_SCHEMA_A);
        } finally {
            heapFileManager.deinitialize();
        }
    }

    @Test
    public void filesGetCached() {
        File testDir = new File(TEST_DATA_DIR);
        HeapFileManager heapFileManager = new HeapFileManager(testDir);
        heapFileManager.createNewHeapFile(TEST_SCHEMA_A);
        HeapFile a = heapFileManager.getExistingFile(TEST_SCHEMA_A);
        heapFileManager.createNewHeapFile(TEST_SCHEMA_B);
        assertEquals(a, heapFileManager.getExistingFile(TEST_SCHEMA_A));
        assertNotSame(a, heapFileManager.getExistingFile(TEST_SCHEMA_B));
        heapFileManager.deinitialize();
    }

    @BeforeClass
    public static void prepare() throws IOException {
        new File(TEST_DATA_DIR).mkdirs();
    }

    @After
    public void cleanup() throws IOException {
        cleanDirectory(TEST_DATA_DIR);
    }

}
