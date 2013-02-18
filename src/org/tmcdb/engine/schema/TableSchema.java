package org.tmcdb.engine.schema;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Talanov
 */
public final class TableSchema implements Serializable {

    @NotNull
    private final String tableName;
    @NotNull
    private final List<Column> columns;
    @NotNull
    private final Map<String, Column> columnByName = new HashMap<String, Column>();

    public TableSchema(@NotNull String tableName, @NotNull List<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;
        for (Column column : columns) {
            this.columnByName.put(column.getName(), column);
        }
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }

    @NotNull
    public List<Column> getColumns() {
        return columns;
    }

    @Nullable
    public Column getColumn(@NotNull String name) {
        return columnByName.get(name);
    }

    public int getRowSize() {
        int rowSize = 0;
        for (Column column : columns) {
            rowSize += column.getType().getSize();
        }
        return rowSize;
    }
}
