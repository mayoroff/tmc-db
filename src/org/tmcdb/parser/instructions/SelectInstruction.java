package org.tmcdb.parser.instructions;

import org.jetbrains.annotations.NotNull;

/**
 * @author Pavel Talanov
 */
public final class SelectInstruction implements Instruction {

    @NotNull
    private final String tableName;

    private final Where where;

    public Where getWhere() {
        return where;
    }

    public SelectInstruction(@NotNull String tableName, Where where) {
        this.tableName = tableName;
        this.where = where;
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }
}
