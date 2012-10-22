package org.tmcdb.heapfile.test;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmcdb.engine.data.Column;
import org.tmcdb.engine.data.NumericType;
import org.tmcdb.engine.data.Page;
import org.tmcdb.heapfile.DataFile;
import org.tmcdb.heapfile.HeapFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Arseny Mayorov.
 * Date: 22.10.12
 */
public final class HeapFileTest {

    @Test(expected = FileNotFoundException.class)
    public void openFileForNonExistingTable() throws IOException {
        new HeapFile("not-exists");
    }

    @Test
    public void openExistingNullSizeFile() throws IOException {

        Assert.assertNotNull(new HeapFile("exists"));
    }

    @Test
    public void testHeapFileCreation() throws IOException {
        HeapFile.createHeapFile("test-creation", 16);
        assertTrue(new File("./data/test-creation").exists());
        assertEquals(new File("./data/test-creation").length(), 65536);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNonExistingPage() throws IOException {
        DataFile dataFile = new HeapFile("exists");
        Page page = dataFile.getPage(0);
    }

    @Test
    public void getExistingPage() throws IOException {
        HeapFile.createHeapFile("test-creation", 16);
        DataFile dataFile = new HeapFile("test-creation");
        Page page = dataFile.getPage(15);
        assertNotNull(page);
    }

    @Test
    public void getNonExistingRecord() throws IOException {
        HeapFile.createHeapFile("test-creation", 16);
        DataFile dataFile = new HeapFile("test-creation");
        Page page = dataFile.getPage(15);
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("id", NumericType.INT));
        assertNull(page.getRecord(columns, 4));
    }

    @BeforeClass
    public static void prepare() throws IOException {
        new File("./data/").mkdirs();
        File file = new File("./data/exists");
        file.createNewFile();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        File folder = new File("./data");
        final File[] files = folder.listFiles();
        for (File f : files) f.delete();
        folder.delete();
    }

}
