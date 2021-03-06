package org.tmcdb.parser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.data.NumericType;
import org.tmcdb.engine.data.Type;
import org.tmcdb.engine.data.VarChar;
import org.tmcdb.engine.schema.Column;
import org.tmcdb.parser.instructions.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Pavel Talanov
 */
public final class Parser {

    private Parser() {
    }

    @NotNull
    public static Instruction parse(@NotNull String query) throws ParserException {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        try {
            return parseStatement(parserManager.parse(new StringReader(query)));
        } catch (JSQLParserException e) {
            //TODO: better message
            throw new ParserException("Invalid SQL statement");
        }
    }

    @NotNull
    private static Instruction parseStatement(@NotNull Statement parsedStatement) throws ParserException {
        if (parsedStatement instanceof CreateTable) {
            return parseCreateTable((CreateTable) parsedStatement);
        }
        if (parsedStatement instanceof CreateIndex) {
            return parseCreateIndex((CreateIndex) parsedStatement);
        }
        if (parsedStatement instanceof Select) {
            return parseSelectStatement((Select) parsedStatement);
        }
        if (parsedStatement instanceof Insert) {
            return parseInsert((Insert) parsedStatement);
        }
        throw new ParserException("Unsupported SQL statement " + parsedStatement);
    }

    private static Instruction parseInsert(Insert parsedStatement) throws ParserException {
        String tableName = parsedStatement.getTable().getName();
        List<InsertInstruction.ColumnNameAndData> result = parseColumnNamesAndValues(parsedStatement);
        return new InsertInstruction(tableName, result);
    }

    @NotNull
    private static List<InsertInstruction.ColumnNameAndData> parseColumnNamesAndValues(@NotNull Insert parsedStatement) throws ParserException {
        ItemsList itemsList = parsedStatement.getItemsList();
        if (!(itemsList instanceof ExpressionList)) {
            throw new ParserException("Unsupported complex INSERT statement " + parsedStatement);
        }
        List expressions = ((ExpressionList) itemsList).getExpressions();
        ArrayList<InsertInstruction.ColumnNameAndData> result = new ArrayList<InsertInstruction.ColumnNameAndData>();
        Iterator expressionsIterator = expressions.iterator();
        Iterator columnsIterator = parsedStatement.getColumns().iterator();
        while (columnsIterator.hasNext() && expressionsIterator.hasNext()) {
            Object column = columnsIterator.next();
            assert column instanceof net.sf.jsqlparser.schema.Column;
            String columnName = ((net.sf.jsqlparser.schema.Column) column).getColumnName();
            result.add(new InsertInstruction.ColumnNameAndData(columnName, parseValue(expressionsIterator.next())));
        }
        if (columnsIterator.hasNext() || expressionsIterator.hasNext()) {
            throw new ParserException("Unmatched number of column and value expressions");
        }
        return result;
    }

    @NotNull
    private static Object parseValue(@NotNull Object expression) throws ParserException {
        if (expression instanceof LongValue) {
            long longValue = ((LongValue) expression).getValue();
            return (int) longValue;
        }
        if (expression instanceof StringValue) {
            return ((StringValue) expression).getValue();
        }
        if (expression instanceof DoubleValue) {
            return ((DoubleValue) expression).getValue();
        }
        throw new ParserException("Invalid value expression: " + expression);
    }

    @NotNull
    private static Instruction parseSelectStatement(@NotNull Select parsedStatement) throws ParserException {
        SelectBody selectBody = parsedStatement.getSelectBody();
        if (!(selectBody instanceof PlainSelect)) {
            throw new ParserException("Unsupported complex SELECT statement " + parsedStatement);
        }
        FromItem fromItem = ((PlainSelect) selectBody).getFromItem();
        if (!(fromItem instanceof Table)) {
            throw new ParserException("Unsupported complex SELECT statement " + parsedStatement);
        }
        Expression expression = ((PlainSelect) selectBody).getWhere();
        Where whereItem;
        if (expression != null) {
            if (!(expression instanceof BinaryExpression)) {
                throw new ParserException("Unsupported complex SELECT statement ");
            }
            String left = ((BinaryExpression) expression).getLeftExpression().toString();
            String right = ((BinaryExpression) expression).getRightExpression().toString();
            String operation = ((BinaryExpression) expression).getStringExpression();
            whereItem = new Where(left, right, operation);
        } else {
            whereItem = new Where();
        }

        String tableName = ((Table) fromItem).getName();
        return new SelectInstruction(tableName, whereItem);
    }

    @NotNull
    private static Instruction parseCreateTable(@NotNull CreateTable parsedStatement) throws ParserException {
        String tableName = parsedStatement.getTable().getName();
        List columnDefinitions = parsedStatement.getColumnDefinitions();
        if (columnDefinitions == null) {
            throw new ParserException("CREATE TABLE statement must contain column definitions");
        }
        List<Column> columns = parseColumnDefinitions(columnDefinitions);
        return new CreateTableInstruction(tableName, columns);
    }

    @NotNull
    private static Instruction parseCreateIndex(@NotNull CreateIndex parsedStatement) throws ParserException {
        String indexName = parsedStatement.getIndex().getName();
        String tableName = parsedStatement.getIndex().getTableName();
        String indexStructure = parsedStatement.getIndex().getStructure();
        List columnsNames = parsedStatement.getIndex().getColumnsNames();
        if (columnsNames == null) {
            throw new ParserException("CREATE INDEX statement must contain column names");
        }
        return new CreateIndexInstruction(indexName, tableName, indexStructure, columnsNames);
    }

    @NotNull
    private static List<Column> parseColumnDefinitions(@NotNull List columnDefinitions) throws ParserException {
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
    private static Type parseType(@NotNull ColDataType dataType) throws ParserException {
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
