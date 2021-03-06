package org.tmcdb.parser.instructions;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.schema.Column;

import java.util.List;

/**
 * Created by Arseny Mayorov.
 * Date: 22.02.13
 */
public class CreateIndexInstruction implements Instruction {
    @NotNull
    private final String indexName;
    @NotNull
    private final String tableName;
    @NotNull
    private final String indexStructure;
    @NotNull
    private final List<Column> columns;

    public CreateIndexInstruction(@NotNull String indexName, @NotNull String tableName, @NotNull String indexStructure,
                                  @NotNull List<Column> columns) {
        this.indexName = indexName;
        this.tableName = tableName;
        this.columns = columns;
        this.indexStructure = indexStructure;
    }

    @NotNull
    public String getIndexName() {
        return indexName;
    }

    @NotNull
    public List<Column> getColumns() {
        return columns;
    }

    @NotNull
    public String getTableName() {
        return tableName;
    }

    @NotNull
    public String getIndexStructure() {
        return indexStructure;
    }

}
