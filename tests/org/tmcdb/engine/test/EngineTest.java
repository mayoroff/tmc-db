package org.tmcdb.engine.test;

import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmcdb.engine.Engine;
import org.tmcdb.engine.LogicException;
import org.tmcdb.engine.data.NumericType;
import org.tmcdb.engine.data.Row;
import org.tmcdb.engine.schema.Column;
import org.tmcdb.heapfile.cursor.Cursor;
import org.tmcdb.parser.instructions.CreateTableInstruction;
import org.tmcdb.parser.instructions.InsertInstruction;
import org.tmcdb.parser.instructions.SelectInstruction;
import org.tmcdb.parser.instructions.Where;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static junit.framework.Assert.*;
import static org.tmcdb.utils.TestUtils.cleanDirectory;

/**
 * @author Pavel Talanov
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public final class EngineTest {
    private static final String TEST_DATA_DIR = "testData/engine/engine";
    public static final Column COLUMN = new Column("column", NumericType.DOUBLE);
    public static final CreateTableInstruction SIMPLE_CREATE_TABLE =
            new CreateTableInstruction("table", Collections.singletonList(COLUMN));
    public static final double VALUE = 2.6;
    public static final InsertInstruction SIMPLE_INSERT =
            new InsertInstruction("table", Collections.singletonList(new InsertInstruction.ColumnNameAndData("column", VALUE)));
    public static final SelectInstruction SIMPLE_SELECT = new SelectInstruction("table",new Where());

    @Test
    public void сreateTable() throws Exception {
        Engine engine = new Engine(createTestDir("1"));
        try {
            engine.initialize();
            engine.createTable(SIMPLE_CREATE_TABLE);
        } finally {
            engine.deinitialize();
        }
    }

    @NotNull
    private File createTestDir(@NotNull String name) {
        File testDir = new File(TEST_DATA_DIR + "/" + name);
        testDir.mkdirs();
        return testDir;
    }

    @Test(expected = LogicException.class)
    public void сreateAlreadyExistingTable() throws Exception {
        Engine engine = new Engine(createTestDir("2"));
        try {
            engine.initialize();
            engine.createTable(SIMPLE_CREATE_TABLE);
            engine.createTable(SIMPLE_CREATE_TABLE);
        } finally {
            engine.deinitialize();
        }
    }

    @Test
    public void insert() throws Exception {
        Engine engine = new Engine(createTestDir("3"));
        try {
            engine.initialize();
            engine.createTable(SIMPLE_CREATE_TABLE);
            engine.insert(SIMPLE_INSERT);
        } finally {
            engine.deinitialize();
        }
    }

    @Test
    public void select() throws Exception {
        Engine engine = new Engine(createTestDir("4"));
        try {
            engine.initialize();
            engine.createTable(SIMPLE_CREATE_TABLE);
            engine.insert(SIMPLE_INSERT);
            Cursor cursor = engine.select(SIMPLE_SELECT);
            Row row = cursor.next();
            assertNotNull(row);
            Object valueForColumn = row.getValueForColumn(COLUMN);
            assertEquals(VALUE, valueForColumn);
            assertNull(cursor.next());
        } finally {
            engine.deinitialize();
        }
    }

    @Test(expected = LogicException.class)
    public void selectFromNonExistingTable() throws Exception {
        Engine engine = new Engine(createTestDir("5"));
        try {
            engine.initialize();
            engine.createTable(SIMPLE_CREATE_TABLE);
            engine.insert(SIMPLE_INSERT);
            engine.select(new SelectInstruction("wrong_table", new Where()));
        } finally {
            engine.deinitialize();
        }
    }

    @BeforeClass
    public static void prepare() throws IOException {
        new File(TEST_DATA_DIR).mkdirs();
        cleanup();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        cleanDirectory(TEST_DATA_DIR);
    }
}
