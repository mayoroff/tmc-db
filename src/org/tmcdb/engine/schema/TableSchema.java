package org.tmcdb.engine.schema;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * @author Pavel Talanov
 */
public final class TableSchema implements Serializable {

    @NotNull
    private final String tableName;
    @NotNull
    private final List<Column> columns;

    public TableSchema(@NotNull String tableName, @NotNull List<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }

    @NotNull
    public List<Column> getColumns() {
        return columns;
    }

    public int getRowSize() {
        int rowSize = 0;
        for (Column column : columns) {
            rowSize += column.getType().getSize();
        }
        return rowSize;
    }
}
