package org.tmcdb.engine;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.schema.SchemaManager;
import org.tmcdb.heapfile.HeapFileManager;

import java.io.File;

import static org.tmcdb.utils.DirectoryUtils.checkIsDirectory;
import static org.tmcdb.utils.DirectoryUtils.ensureSubDirectoryExists;

/**
 * @author Pavel Talanov
 */
public final class Engine {

    public static final String HEAP_FILES_DIR = ".data";
    public static final String SCHEMAS_DIR = ".schema";
    @NotNull
    private final HeapFileManager heapFileManager;
    @NotNull
    private final SchemaManager schemaManager;


    public Engine(@NotNull File workingDirectory) {
        checkIsDirectory(workingDirectory);
        this.heapFileManager = new HeapFileManager(ensureSubDirectoryExists(workingDirectory, HEAP_FILES_DIR));
        this.schemaManager = new SchemaManager(ensureSubDirectoryExists(workingDirectory, SCHEMAS_DIR));
    }

}
