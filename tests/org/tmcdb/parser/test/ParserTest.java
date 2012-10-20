package org.tmcdb.parser.test;

import com.sun.istack.internal.NotNull;
import org.junit.Test;
import org.tmcdb.engine.data.Column;
import org.tmcdb.engine.data.NumericType;
import org.tmcdb.engine.data.Type;
import org.tmcdb.engine.data.VarChar;
import org.tmcdb.parser.Parser;
import org.tmcdb.parser.ParserException;
import org.tmcdb.parser.instructions.CreateTableInstruction;
import org.tmcdb.parser.instructions.Instruction;
import org.tmcdb.parser.instructions.SelectInstruction;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Pavel Talanov
 */
public final class ParserTest {

    @Test
    public void simpleCreateTable() {
        Instruction instruction = Parser.parse("CREATE TABLE mytable(a DOUBLE)");
        assertTrue(instruction instanceof CreateTableInstruction);
        CreateTableInstruction createTableInstruction = (CreateTableInstruction) instruction;
        assertEquals(createTableInstruction.getTableName(), "mytable");
        assertEquals(createTableInstruction.getColumns().size(), 1);
        checkColumn(createTableInstruction, 0, "a", NumericType.DOUBLE);
    }

    @Test
    public void createTableLowerCase() {
        Instruction instruction = Parser.parse("create table MYTABLE(A double)");
        assertTrue(instruction instanceof CreateTableInstruction);
        CreateTableInstruction createTableInstruction = (CreateTableInstruction) instruction;
        assertEquals(createTableInstruction.getTableName(), "MYTABLE");
        assertEquals(createTableInstruction.getColumns().size(), 1);
        checkColumn(createTableInstruction, 0, "A", NumericType.DOUBLE);
    }

    @Test
    public void severalTypes() {
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
    public void createTableWithoutColumns() {
        Parser.parse("CREATE TABLE tableWithoutColumns");
    }

    @Test(expected = ParserException.class)
    public void createTableWithWrongType() {
        Parser.parse("CREATE TABLE tableWithoutColumns(a TRIPLE)");
    }

    @Test(expected = ParserException.class)
    public void invalidSQL() {
        Parser.parse("completely invalid sql");
    }

    private void checkColumn(@NotNull CreateTableInstruction instruction, int index, @NotNull String name, @NotNull Type type) {
        Column column = instruction.getColumns().get(index);
        assertEquals(column.getName(), name);
        assertEquals(column.getType(), type);
    }

    @Test
    public void select() {
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
    public void selectWrongSyntax() {
        Parser.parse("SELECT # FROM tableName");
    }
}
