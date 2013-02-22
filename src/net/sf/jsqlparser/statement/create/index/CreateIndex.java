package net.sf.jsqlparser.statement.create.index;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.Index;

/**
 * A "CREATE INDEX" statement
 */
public class CreateIndex implements Statement {

    private Index index;

    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

    /**
     * The name of the table to be created
     */
    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }


    public String toString() {
        String sql = "";

        sql = "CREATE INDEX " + index + " (";
        /*
sql += PlainSelect.getStringList(columnDefinitions, true, false);
if (indexes != null && indexes.size() != 0) {
sql += ", ";
sql += PlainSelect.getStringList(indexes);
}
sql += ") ";
sql += PlainSelect.getStringList(tableOptionsStrings, false, false);
        */
        return sql;
    }
}