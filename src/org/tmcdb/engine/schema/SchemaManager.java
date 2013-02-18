package org.tmcdb.engine.schema;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.tmcdb.utils.DirectoryUtils.checkIsDirectory;

/**
 * @author Pavel Talanov
 */
public final class SchemaManager {
    @NotNull
    private final File schemasDir;
    @Nullable
    private List<TableSchema> tables = null;

    public SchemaManager(@NotNull File schemasDir) {
        checkIsDirectory(schemasDir);
        this.schemasDir = schemasDir;
    }

    public void initialize() {
        File[] files = schemasDir.listFiles();
        if (files == null) {
            throw new IllegalStateException("Error reading schemas");
        }
        tables = new ArrayList<TableSchema>();
        for (File file : files) {
            if (file.isFile()) {
                tables.add(readSchema(file));
            } else {
                throw new IllegalStateException("Schema directory content is corrupted");
            }
        }
    }

    @NotNull
    private TableSchema readSchema(@NotNull File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                ObjectInputStream serializationInputStream = new ObjectInputStream(fileInputStream);
                try {
                    Object deserializedObject = serializationInputStream.readObject();
                    assert deserializedObject instanceof TableSchema;
                    return (TableSchema) deserializedObject;
                } finally {
                    serializationInputStream.close();
                }
            } finally {
                fileInputStream.close();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error deserializing data from " + file.getAbsolutePath(), e);
        }
    }

    public void deinitilize() {
        writeSchemas();
    }

    private void writeSchemas() {
        assert tables != null : "Initialize() was not called or failed.";
        for (TableSchema table : tables) {
            File schemaFile = new File(schemasDir, table.getTableName());
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(schemaFile);
                try {
                    ObjectOutputStream serializationOutputStream = new ObjectOutputStream(fileOutputStream);
                    try {
                        serializationOutputStream.writeObject(table);
                    } finally {
                        serializationOutputStream.close();
                    }
                } finally {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                throw new IllegalStateException("Error deserializing data from " + schemaFile.getAbsolutePath(), e);
            }

        }
    }

    @NotNull
    public List<TableSchema> getAllTables() {
        assert tables != null : "Initialize() was not called or failed.";
        return new ArrayList<TableSchema>(tables);
    }

    public void addNewSchema(@NotNull TableSchema schema) {
        //TODO: check for duplication
        assert tables != null : "Initialize() was not called or failed.";
        tables.add(schema);
    }
}
