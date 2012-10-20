package org.tmcdb.parser.instructions;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.data.Column;

import java.util.List;

/**
 * @author Pavel Talanov
 */
public final class CreateTableInstruction implements Instruction {

    @NotNull
    private final String tableName;
    @NotNull
    private final List<Column> columns;

    public CreateTableInstruction(@NotNull String tableName, @NotNull List<Column> columns) {
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
