package org.tmcdb.parser.test;

import com.sun.istack.internal.NotNull;
import org.junit.Test;
import org.tmcdb.engine.data.NumericType;
import org.tmcdb.engine.data.Type;
import org.tmcdb.engine.data.VarChar;
import org.tmcdb.engine.schema.Column;
import org.tmcdb.parser.Parser;
import org.tmcdb.parser.ParserException;
import org.tmcdb.parser.instructions.*;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Pavel Talanov
 */
public final class ParserTest {

    @Test
    public void simpleCreateTable() throws ParserException {
        Instruction instruction = Parser.parse("CREATE TABLE mytable(a DOUBLE)");
        assertTrue(instruction instanceof CreateTableInstruction);
        CreateTableInstruction createTableInstruction = (CreateTableInstruction) instruction;
        assertEquals(createTableInstruction.getTableName(), "mytable");
        assertEquals(createTableInstruction.getColumns().size(), 1);
        checkColumn(createTableInstruction, 0, "a", NumericType.DOUBLE);
    }

    @Test
    public void createTableLowerCase() throws ParserException {
        Instruction instruction = Parser.parse("create table MYTABLE(A double)");
        assertTrue(instruction instanceof CreateTableInstruction);
        CreateTableInstruction createTableInstruction = (CreateTableInstruction) instruction;
        assertEquals(createTableInstruction.getTableName(), "MYTABLE");
        assertEquals(createTableInstruction.getColumns().size(), 1);
        checkColumn(createTableInstruction, 0, "A", NumericType.DOUBLE);
    }

    @Test
    public void createTreeIndex() throws ParserException {
        Instruction instruction = Parser.parse("create index myindex ON mytable2 (col1) USING BTREE");
        assertTrue(instruction instanceof CreateIndexInstruction);
        CreateIndexInstruction createIndexInstruction = (CreateIndexInstruction) instruction;
        assertEquals(createIndexInstruction.getTableName(), "mytable2");
        assertEquals(createIndexInstruction.getIndexName(), "myindex");
        assertEquals(createIndexInstruction.getIndexStructure(), "BTREE");

    }

    @Test
    public void createHashIndex() throws ParserException {
        Instruction instruction = Parser.parse("create index myindex ON mytable2 (col1) USING HASH");
        assertTrue(instruction instanceof CreateIndexInstruction);
        CreateIndexInstruction createIndexInstruction = (CreateIndexInstruction) instruction;
        assertEquals(createIndexInstruction.getTableName(), "mytable2");
        assertEquals(createIndexInstruction.getIndexName(), "myindex");
        assertEquals(createIndexInstruction.getIndexStructure(), "HASH");
    }

    @Test
    public void createUniqueIndex() throws ParserException {
        Instruction instruction = Parser.parse("create unique index myindex ON mytable2 (col1) USING HASH");
        assertTrue(instruction instanceof CreateIndexInstruction);
        CreateIndexInstruction createIndexInstruction = (CreateIndexInstruction) instruction;
        assertEquals(createIndexInstruction.getTableName(), "mytable2");
        assertEquals(createIndexInstruction.getIndexName(), "myindex");
        assertEquals(createIndexInstruction.getIndexStructure(), "HASH");
    }

    @Test
    public void severalTypes() throws ParserException {
        Instruction instruction = Parser.parse("CREATE TABLE thatTable (a DOUBLE, b INT, name VARCHAR(20), Address VARCHAR(300))");
        assertTrue(instruction instanceof CreateTableInstruction);
        CreateTableInstruction createTableInstruction = (CreateTableInstruction) instruction;
        assertEquals(createTableInstruction.getTableName(), "thatTable");
        assertEquals(createTableInstruction.getColumns().size(), 4);
        checkColumn(createTableInstruction, 0, "a", NumericType.DOUBLE);
        checkColumn(createTableInstruction, 1, "b", NumericType.INT);
        checkColumn(createTableInstruction, 2, "name", new VarChar(20));
        checkColumn(createTableInstruction, 3, "Address", new VarChar(300));
        assertEquals(((VarChar) createTableInstruction.getColumns().get(3).getType()).getNumberOfChars(), 300);
    }

    @Test(expected = ParserException.class)
    public void createTableWithoutColumns() throws ParserException {
        Parser.parse("CREATE TABLE tableWithoutColumns");
    }

    @Test(expected = ParserException.class)
    public void createTableWithWrongType() throws ParserException {
        Parser.parse("CREATE TABLE tableWithoutColumns(a TRIPLE)");
    }

    @Test(expected = ParserException.class)
    public void invalidSQL() throws ParserException {
        Parser.parse("completely invalid sql");
    }

    private void checkColumn(@NotNull CreateTableInstruction instruction, int index, @NotNull String name, @NotNull Type type) {
        Column column = instruction.getColumns().get(index);
        assertEquals(column.getName(), name);
        assertEquals(column.getType(), type);
    }

    @Test
    public void select() throws ParserException {
        Instruction i = Parser.parse("SELECT * FROM tableName");
        assertTrue(i instanceof SelectInstruction);
        SelectInstruction si = (SelectInstruction) i;
        assertEquals(si.getTableName(), "tableName");

        Instruction i2 = Parser.parse("SELECT * FROM chair");
        assertTrue(i2 instanceof SelectInstruction);
        SelectInstruction si2 = (SelectInstruction) i2;
        assertEquals(si2.getTableName(), "chair");
    }

    @Test(expected = ParserException.class)
    public void selectWrongSyntax() throws ParserException {
        Parser.parse("SELECT # FROM tableName");
    }

    @Test
    public void simpleInsert() throws ParserException {
        Instruction instruction = Parser.parse("INSERT INTO t(a, b, c) VALUES (1, 2.3, 'abc')");
        assertTrue(instruction instanceof InsertInstruction);
        InsertInstruction insertInstruction = (InsertInstruction) instruction;
        assertEquals(insertInstruction.getTableName(), "t");
        List<InsertInstruction.ColumnNameAndData> columnNamesWithData = insertInstruction.getColumnNamesWithData();
        assertEquals(columnNamesWithData.get(0).getColumnName(), "a");
        assertEquals(columnNamesWithData.get(1).getColumnName(), "b");
        assertEquals(columnNamesWithData.get(2).getColumnName(), "c");
        assertEquals(columnNamesWithData.get(0).getData(), 1);
        assertEquals(columnNamesWithData.get(1).getData(), 2.3);
        assertEquals(columnNamesWithData.get(2).getData(), "abc");
    }

    @Test
    public void singleInsert() throws ParserException {
        Instruction instruction = Parser.parse("INSERT INTO t(a) VALUES ('abc')");
        assertTrue(instruction instanceof InsertInstruction);
        InsertInstruction insertInstruction = (InsertInstruction) instruction;
        assertEquals(insertInstruction.getTableName(), "t");
        List<InsertInstruction.ColumnNameAndData> columnNamesWithData = insertInstruction.getColumnNamesWithData();
        assertEquals(columnNamesWithData.get(0).getColumnName(), "a");
        assertEquals(columnNamesWithData.get(0).getData(), "abc");
    }

    @Test(expected = ParserException.class)
    public void emptyInsert() throws ParserException {
        Parser.parse("INSERT INTO t VALUES ");
    }

    @Test(expected = ParserException.class)
    public void unmatchedNumberOfValueAndColumnExpressions() throws ParserException {
        Parser.parse("INSERT INTO t(a, b, c, d) VALUES (1, 2.3, 'abc')");
    }

    @Test(expected = ParserException.class)
    public void unmatchedNumberOfValueAndColumnExpressions2() throws ParserException {
        Parser.parse("INSERT INTO t(a, b, c) VALUES (1, 2.3, 'abc', 3)");
    }
}
