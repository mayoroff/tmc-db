package org.tmcdb.heapfile;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.schema.TableSchema;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pavel Talanov
 */
public final class HeapFileManager {

    @NotNull
    private final String dataDir;
    @NotNull
    private final Map<TableSchema, HeapFile> fileBySchema = new HashMap<TableSchema, HeapFile>();

    public HeapFileManager(@NotNull String dataDir) {
        this.dataDir = dataDir;
    }

    @NotNull
    public HeapFile getFile(@NotNull TableSchema schema) {
        HeapFile heapFile = fileBySchema.get(schema);
        if (heapFile != null) {
            return heapFile;
        }
        try {
            heapFile = new HeapFile(pathForSchema(schema), schema);
            fileBySchema.put(schema, heapFile);
            return heapFile;
        } catch (IOException e) {
            throw new RuntimeException("Error reading file " + pathForSchema(schema), e);
        }
    }

    public void createNewHeapFile(@NotNull TableSchema schema) {
        try {
            HeapFile.createEmptyHeapFile(pathForSchema(schema));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String pathForSchema(TableSchema schema) {
        return dataDir + "/" + schema.getTableName();
    }


}
