package org.tmcdb.parser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.data.Column;
import org.tmcdb.engine.data.NumericType;
import org.tmcdb.engine.data.Type;
import org.tmcdb.engine.data.VarChar;
import org.tmcdb.parser.instructions.CreateTableInstruction;
import org.tmcdb.parser.instructions.Instruction;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pavel Talanov
 */
public final class Parser {

    @NotNull
    public static Instruction parse(@NotNull String query) {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        try {
            return parseStatement(parserManager.parse(new StringReader(query)));
        } catch (JSQLParserException e) {
            //TODO: better message
            throw new ParserException("Invalid SQL statement");
        }
    }

    @NotNull
    private static Instruction parseStatement(@NotNull Statement parsedStatement) {
        if (parsedStatement instanceof CreateTable) {
            return parseCreateTable((CreateTable) parsedStatement);
        }
        throw new ParserException("Unsupported SQL statement " + parsedStatement);
    }

    @NotNull
    private static Instruction parseCreateTable(@NotNull CreateTable parsedStatement) {
        String tableName = parsedStatement.getTable().getName();
        List columnDefinitions = parsedStatement.getColumnDefinitions();
        if (columnDefinitions == null) {
            throw new ParserException("CREATE TABLE statement must contain column definitions.");
        }
        List<Column> columns = parseColumnDefinitions(columnDefinitions);
        return new CreateTableInstruction(tableName, columns);
    }

    @NotNull
    private static List<Column> parseColumnDefinitions(@NotNull List columnDefinitions) {
        List<Column> columns = new ArrayList<Column>();
        for (Object columnDefinition : columnDefinitions) {
            assert columnDefinition instanceof ColumnDefinition;
            String columnName = ((ColumnDefinition) columnDefinition).getColumnName();
            ColDataType dataType = ((ColumnDefinition) columnDefinition).getColDataType();
            columns.add(new Column(columnName, parseType(dataType)));
        }
        return columns;
    }

    @NotNull
    private static Type parseType(@NotNull ColDataType dataType) {
        String typeName = dataType.getDataType().toUpperCase();
        for (NumericType type : NumericType.values()) {
            if (type.toString().equals(typeName)) {
                return type;
            }
        }
        if (!typeName.equals(VarChar.uppercaseTypeName())) {
            throw new ParserException("Illegal type expression: " + dataType);
        }
        List argumentList = dataType.getArgumentsStringList();
        if (argumentList == null || argumentList.size() != 1) {
            throw new ParserException("Illegal type expression: " + dataType);
        }
        Object elementNumber = argumentList.iterator().next();
        assert elementNumber instanceof String;
        try {
            Integer elementNum = Integer.valueOf((String) elementNumber);
            return new VarChar(elementNum);
        } catch (NumberFormatException e) {
            throw new ParserException("Illegal type expression: " + dataType);
        }
    }
}
