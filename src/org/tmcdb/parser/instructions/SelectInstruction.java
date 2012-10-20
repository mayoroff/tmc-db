package org.tmcdb.parser.instructions;

import org.jetbrains.annotations.NotNull;

/**
 * @author Pavel Talanov
 */
public final class SelectInstruction implements Instruction {

    @NotNull
    private final String tableName;

    public SelectInstruction(@NotNull String tableName) {
        this.tableName = tableName;
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }
}
