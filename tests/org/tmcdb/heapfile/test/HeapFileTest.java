package org.tmcdb.heapfile.test;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Test;
import org.tmcdb.engine.data.NumericType;
import org.tmcdb.engine.data.Row;
import org.tmcdb.engine.schema.Column;
import org.tmcdb.engine.schema.TableSchema;
import org.tmcdb.heapfile.HeapFile;
import org.tmcdb.heapfile.Page;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Arseny Mayorov.
 * Date: 22.10.12
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public final class HeapFileTest {

    private static final String TEST_DATA_DIR = "testData/engine/heapFile";

    public static final Column DOUBLE_COLUMN = new Column("double", NumericType.DOUBLE);
    public static final Column INT_COLUMN = new Column("int", NumericType.INT);


    public static final TableSchema TEST_SIMPLE_SCHEMA;
    public static final ArrayList<Column> TEST_SIMPLE_COLUMNS;

    static {
        TEST_SIMPLE_COLUMNS = new ArrayList<Column>();
        TEST_SIMPLE_COLUMNS.add(DOUBLE_COLUMN);
        TEST_SIMPLE_COLUMNS.add(INT_COLUMN);
        TEST_SIMPLE_SCHEMA = new TableSchema("testTable", TEST_SIMPLE_COLUMNS);
    }

    @Test(expected = FileNotFoundException.class)
    public void openFileForNonExistingTable() throws IOException {
        HeapFile heapFile = new HeapFile(TEST_DATA_DIR + "/not-exists", TEST_SIMPLE_SCHEMA);
        heapFile.deinitialize();
    }

    @Test
    public void openExistingNullSizeFile() throws IOException {
        File file = new File(TEST_DATA_DIR + "/exists");
        file.createNewFile();
        HeapFile heapFile = new HeapFile(TEST_DATA_DIR + "/exists", TEST_SIMPLE_SCHEMA);
        Assert.assertNotNull(heapFile);
        heapFile.deinitialize();
    }

    @Test
    public void testHeapFileCreation() throws IOException {
        HeapFile.createHeapFile(TEST_DATA_DIR + "/test-creation", 16);
        assertTrue(new File(TEST_DATA_DIR + "/test-creation").exists());
        assertEquals(65536, new File(TEST_DATA_DIR + "/test-creation").length());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNonExistingPage() throws IOException {
        HeapFile dataFile = new HeapFile(TEST_DATA_DIR + "/exists", TEST_SIMPLE_SCHEMA);
        try {
            dataFile.getPage(0);
        } finally {
            dataFile.deinitialize();
        }
    }

    @Test
    public void getExistingPage() throws IOException {
        HeapFile.createHeapFile(TEST_DATA_DIR + "/test-creation", 16);
        HeapFile dataFile = new HeapFile(TEST_DATA_DIR + "/test-creation", TEST_SIMPLE_SCHEMA);
        Page page = dataFile.getPage(15);
        assertNotNull(page);
        dataFile.deinitialize();
    }

    @Test
    public void getNonExistingRecord() throws IOException {
        HeapFile.createHeapFile(TEST_DATA_DIR + "/test-creation", 16);
        HeapFile dataFile = new HeapFile(TEST_DATA_DIR + "/test-creation", TEST_SIMPLE_SCHEMA);
        Page page = dataFile.getPage(15);
        assertNull(page.getRecord(4));
        dataFile.deinitialize();
    }

    @Test
    public void insertRecordAndRetrieveRecord() throws Exception {
        HeapFile.createHeapFile("test-insert", 16);
        HeapFile dataFile = new HeapFile("test-insert", TEST_SIMPLE_SCHEMA);
        Page page = dataFile.getPage(0);
        page.insertRecord(0, new Row(Collections.singletonList(DOUBLE_COLUMN), Collections.<Object>singletonList(3.0)));
        Row record = page.getRecord(0);
        assertNotNull(record);
        assertEquals(TEST_SIMPLE_SCHEMA.getColumns().size(), record.getColumns().size());
        assertEquals(3.0, record.getValueForColumn(DOUBLE_COLUMN));
        dataFile.deinitialize();
    }

    @Test
    public void insertMultipleColumns() throws Exception {
        HeapFile.createHeapFile("test-insert-multiple-columns", 16);
        HeapFile dataFile = new HeapFile("test-insert-multiple-columns", TEST_SIMPLE_SCHEMA);
        Page page = dataFile.getPage(0);
        List<Column> columns = new ArrayList<Column>();
        columns.add(DOUBLE_COLUMN);
        columns.add(INT_COLUMN);
        List<Object> values = new ArrayList<Object>();
        double doubleValue = 5.3;
        int intValue = -23;
        values.add(doubleValue);
        values.add(intValue);
        Row record = new Row(columns, values);
        List<Integer> slots = new ArrayList<Integer>();
        slots.add(3);
        slots.add(4);
        slots.add(5);
        slots.add(6);
        slots.add(10);
        for (Integer slot : slots) {
            page.insertRecord(slot, record);
        }
        for (Integer slot : slots) {
            record = page.getRecord(slot);
            assertNotNull(record);
            assertEquals(TEST_SIMPLE_SCHEMA.getColumns().size(), record.getColumns().size());
            assertEquals(doubleValue, record.getValueForColumn(DOUBLE_COLUMN));
            assertEquals(intValue, record.getValueForColumn(INT_COLUMN));
        }

        dataFile.deinitialize();
    }

    @Test
    public void dataPersists() throws Exception {
        HeapFile.createHeapFile("test-insert-multiple-columns", 16);
        HeapFile dataFile = new HeapFile("test-insert-multiple-columns", TEST_SIMPLE_SCHEMA);
        Page page = dataFile.getPage(0);
        List<Column> columns = new ArrayList<Column>();
        columns.add(DOUBLE_COLUMN);
        columns.add(INT_COLUMN);
        List<Object> values = new ArrayList<Object>();
        double doubleValue = 5.3;
        int intValue = -23;
        values.add(doubleValue);
        values.add(intValue);
        Row record = new Row(columns, values);
        List<Integer> slots = new ArrayList<Integer>();
        slots.add(3);
        slots.add(7);
        slots.add(5);
        slots.add(2);
        slots.add(10);
        for (Integer slot : slots) {
            page.insertRecord(slot, record);
        }
        dataFile.deinitialize();
        dataFile = new HeapFile("test-insert-multiple-columns", TEST_SIMPLE_SCHEMA);
        for (Integer slot : slots) {
            record = page.getRecord(slot);
            assertNotNull(record);
            assertEquals(TEST_SIMPLE_SCHEMA.getColumns().size(), record.getColumns().size());
            assertEquals(doubleValue, record.getValueForColumn(DOUBLE_COLUMN));
            assertEquals(intValue, record.getValueForColumn(INT_COLUMN));
        }

        dataFile.deinitialize();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        System.gc();
        File folder = new File(TEST_DATA_DIR);
        final File[] files = folder.listFiles();
        assert files != null;
        for (File f : files) {
            boolean success = f.delete();
            assertTrue("Could not delete " + f.getAbsolutePath(), success);
        }
    }

}
