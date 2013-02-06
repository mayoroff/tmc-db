package org.tmcdb.heapfile;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.schema.TableSchema;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pavel Talanov
 */
public final class HeapFileManager {

    @NotNull
    private final File dataDir;
    @NotNull
    private final Map<TableSchema, HeapFile> fileBySchema = new HashMap<TableSchema, HeapFile>();

    public HeapFileManager(@NotNull File dataDir) {
        this.dataDir = dataDir;
    }

    @NotNull
    public HeapFile getExistingFile(@NotNull TableSchema schema) {
        HeapFile heapFile = fileBySchema.get(schema);
        if (heapFile != null) {
            return heapFile;
        }
        try {
            heapFile = new HeapFile(pathForSchema(schema), schema);
            fileBySchema.put(schema, heapFile);
            return heapFile;
        } catch (IOException e) {
            //TODO: better exception?
            throw new RuntimeException("Error reading file " + pathForSchema(schema), e);
        }
    }

    public void deinitialize() {
        for (HeapFile heapFile : fileBySchema.values()) {
            heapFile.deinitialize();
        }
    }

    public void createNewHeapFile(@NotNull TableSchema schema) {
        try {
            HeapFile.createEmptyHeapFile(pathForSchema(schema));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private String pathForSchema(@NotNull TableSchema schema) {
        return dataDir.getAbsolutePath() + "/" + schema.getTableName() + ".data";
    }


}
