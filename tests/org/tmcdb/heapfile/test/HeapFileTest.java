package org.tmcdb.heapfile.test;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Test;
import org.tmcdb.engine.data.NumericType;
import org.tmcdb.engine.data.Row;
import org.tmcdb.engine.data.VarChar;
import org.tmcdb.engine.schema.Column;
import org.tmcdb.engine.schema.TableSchema;
import org.tmcdb.heapfile.HeapFile;
import org.tmcdb.heapfile.Page;
import org.tmcdb.heapfile.cursor.Cursor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.tmcdb.utils.TestUtils.cleanDirectory;

/**
 * @author Arseny Mayorov
 * @author Pavel Talanov
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
        HeapFile.createEmptyHeapFile(TEST_DATA_DIR + "/test-creation", 16);
        assertTrue(new File(TEST_DATA_DIR + "/test-creation").exists());
        assertEquals(65536, new File(TEST_DATA_DIR + "/test-creation").length());
    }

    @Test
    public void getExistingPage() throws IOException {
        HeapFile.createEmptyHeapFile(TEST_DATA_DIR + "/test-creation", 16);
        HeapFile dataFile = new HeapFile(TEST_DATA_DIR + "/test-creation", TEST_SIMPLE_SCHEMA);
        Page page = dataFile.getPage(15);
        assertNotNull(page);
        dataFile.deinitialize();
    }

    @Test
    public void getNonExistingRecord() throws IOException {
        HeapFile.createEmptyHeapFile(TEST_DATA_DIR + "/test-creation", 16);
        HeapFile dataFile = new HeapFile(TEST_DATA_DIR + "/test-creation", TEST_SIMPLE_SCHEMA);
        Page page = dataFile.getPage(15);
        assertNull(page.getRecord(4));
        dataFile.deinitialize();
    }

    @Test
    public void insertRecordAndRetrieveRecord() throws Exception {
        HeapFile.createEmptyHeapFile(TEST_DATA_DIR + "/test-insert", 16);
        HeapFile dataFile = new HeapFile(TEST_DATA_DIR + "/test-insert", TEST_SIMPLE_SCHEMA);
        Page page = dataFile.getPage(0);
        page.insertRecord(0, new Row(Collections.singletonList(DOUBLE_COLUMN), Collections.<Object>singletonList(3.0)));
        Row record = page.getRecord(0);
        assertNotNull(record);
        assertEquals(TEST_SIMPLE_SCHEMA.getColumns().size(), record.getColumns().size());
        assertEquals(3.0, record.getValueForColumn(DOUBLE_COLUMN));
        dataFile.deinitialize();
        assertEquals(65536, new File(TEST_DATA_DIR + "/test-insert").length());
    }

    @Test
    public void insertMultipleColumns() throws Exception {
        HeapFile.createEmptyHeapFile(TEST_DATA_DIR + "/test-insert-multiple-columns", 16);
        HeapFile dataFile = new HeapFile(TEST_DATA_DIR + "/test-insert-multiple-columns", TEST_SIMPLE_SCHEMA);
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
        assertEquals(65536, new File(TEST_DATA_DIR + "/test-insert-multiple-columns").length());
    }

    @Test
    public void dataPersists() throws Exception {
        HeapFile.createEmptyHeapFile(TEST_DATA_DIR + "/test-persists", 16);
        HeapFile dataFile = new HeapFile(TEST_DATA_DIR + "/test-persists", TEST_SIMPLE_SCHEMA);
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
        assertEquals(65536, new File(TEST_DATA_DIR + "/test-persists").length());
        dataFile = new HeapFile(TEST_DATA_DIR + "/test-persists", TEST_SIMPLE_SCHEMA);
        for (Integer slot : slots) {
            record = page.getRecord(slot);
            assertNotNull(record);
            assertEquals(TEST_SIMPLE_SCHEMA.getColumns().size(), record.getColumns().size());
            assertEquals(doubleValue, record.getValueForColumn(DOUBLE_COLUMN));
            assertEquals(intValue, record.getValueForColumn(INT_COLUMN));
        }

        dataFile.deinitialize();
        assertEquals(65536, new File(TEST_DATA_DIR + "/test-persists").length());
    }

    @Test
    public void defaultValues() throws Exception {
        HeapFile.createEmptyHeapFile(TEST_DATA_DIR + "/test-default-values", 16);
        HeapFile dataFile = new HeapFile(TEST_DATA_DIR + "/test-default-values", TEST_SIMPLE_SCHEMA);
        Page page = dataFile.getPage(0);
        Row record = new Row(Collections.<Column>emptyList(), Collections.emptyList());
        List<Integer> slots = new ArrayList<Integer>();
        slots.add(3);
        slots.add(7);
        slots.add(5);
        slots.add(2);
        slots.add(10);
        for (Integer slot : slots) {
            page.insertRecord(slot, record);
        }
        for (Integer slot : slots) {
            record = page.getRecord(slot);
            assertNotNull(record);
            assertEquals(TEST_SIMPLE_SCHEMA.getColumns().size(), record.getColumns().size());
            assertEquals(NumericType.DOUBLE.getDefaultValue(), record.getValueForColumn(DOUBLE_COLUMN));
            assertEquals(NumericType.INT.getDefaultValue(), record.getValueForColumn(INT_COLUMN));
        }

        assertEquals(65536, new File(TEST_DATA_DIR + "/test-default-values").length());
        dataFile.deinitialize();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void insertingAndReadingVarcharValues() throws Exception {
        ArrayList<Column> columns = new ArrayList<Column>();
        Column column1 = new Column("str1", new VarChar(20));
        columns.add(column1);
        Column column2 = new Column("str2", new VarChar(30));
        columns.add(column2);
        TableSchema varcharSchema = new TableSchema("testVarchar", columns);
        HeapFile.createEmptyHeapFile(TEST_DATA_DIR + "/test-varchar", 16);
        HeapFile dataFile = new HeapFile(TEST_DATA_DIR + "/test-varchar", varcharSchema);
        Page page = dataFile.getPage(0);
        ArrayList<Object> values = new ArrayList<Object>();
        values.add("aaa");
        values.add("bbbbb");
        Row record = new Row(varcharSchema.getColumns(), values);
        boolean inserted = page.insertRecord(2, record);
        assertTrue(inserted);
        record = page.getRecord(2);
        assertEquals("aaa", record.getValueForColumn(column1));
        assertEquals("bbbbb", record.getValueForColumn(column2));

        inserted = page.insertRecord(5, new Row(varcharSchema.getColumns(), Arrays.<Object>asList(" some string ", "  some other string  ")));
        Assert.assertTrue(inserted);
        record = page.getRecord(5);
        assertEquals(" some string ", record.getValueForColumn(column1));
        assertEquals("  some other string  ", record.getValueForColumn(column2));

        dataFile.deinitialize();
        assertEquals(65536, new File(TEST_DATA_DIR + "/test-varchar").length());
    }


    @Test
    public void cursorForOnePage() throws Exception {
        HeapFile.createEmptyHeapFile(TEST_DATA_DIR + "/test-cursor1", 16);
        HeapFile dataFile = new HeapFile(TEST_DATA_DIR + "/test-cursor1", TEST_SIMPLE_SCHEMA);
        Page page = dataFile.getPage(0);
        Row record = new Row(Collections.singletonList(DOUBLE_COLUMN), Collections.<Object>singletonList(3.0));
        page.insertRecord(0, record);
        page.insertRecord(3, record);
        page.insertRecord(100, record);
        Cursor cursor = page.getCursor();
        int records = 0;
        for (Row extractedRecord = cursor.next(); extractedRecord != null; extractedRecord = cursor.next(), ++records) {
            assertNotNull(extractedRecord);
            assertEquals(TEST_SIMPLE_SCHEMA.getColumns().size(), extractedRecord.getColumns().size());
            assertEquals(3.0, extractedRecord.getValueForColumn(DOUBLE_COLUMN));
        }
        assertEquals(3, records);
        assertNull(dataFile.getPage(2).getCursor().next());
        assertNull(dataFile.getPage(3).getCursor().next());
        dataFile.deinitialize();
        assertEquals(65536, new File(TEST_DATA_DIR + "/test-insert").length());
    }

    @AfterClass
    public static void cleanup() throws IOException {
        cleanDirectory(TEST_DATA_DIR);
    }

}
