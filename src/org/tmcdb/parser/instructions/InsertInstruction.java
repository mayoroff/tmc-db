package org.tmcdb.parser.instructions;

import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * @author Pavel Talanov
 */
public class InsertInstruction implements Instruction {

    public static class ColumnNameAndData {

        @NotNull
        private final String columnName;
        @NotNull
        private final Object data;

        public ColumnNameAndData(@NotNull String columnName, @NotNull Object data) {
            this.columnName = columnName;
            this.data = data;
        }

        public String getColumnName() {
            return columnName;
        }

        public Object getData() {
            return data;
        }
    }

    @NotNull
    private final String tableName;

    @NotNull
    private final List<ColumnNameAndData> columnNamesWithData;

    public InsertInstruction(@NotNull String tableName, @NotNull List<ColumnNameAndData> columnNamesWithData) {
        this.tableName = tableName;
        this.columnNamesWithData = columnNamesWithData;
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }

    @NotNull
    public List<ColumnNameAndData> getColumnNamesWithData() {
        return columnNamesWithData;
    }
}
