package org.tmcdb.engine.schema;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Pavel Talanov
 */
public final class TableSchema {

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
}
