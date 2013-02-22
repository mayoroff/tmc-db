package org.tmcdb.engine;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.data.Row;
import org.tmcdb.engine.indexes.IndexesManager;
import org.tmcdb.engine.indexes.TreeIndex;
import org.tmcdb.engine.schema.Column;
import org.tmcdb.engine.schema.SchemaManager;
import org.tmcdb.engine.schema.TableSchema;
import org.tmcdb.heapfile.HeapFile;
import org.tmcdb.heapfile.HeapFileManager;
import org.tmcdb.heapfile.cursor.Cursor;
import org.tmcdb.heapfile.cursor.FilteredCursor;
import org.tmcdb.parser.instructions.CreateIndexInstruction;
import org.tmcdb.parser.instructions.CreateTableInstruction;
import org.tmcdb.parser.instructions.InsertInstruction;
import org.tmcdb.parser.instructions.SelectInstruction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.tmcdb.utils.DirectoryUtils.checkIsDirectory;
import static org.tmcdb.utils.DirectoryUtils.ensureSubDirectoryExists;

/**
 * @author Pavel Talanov
 * @author Arseny Mayorov
 */
public final class Engine {

    public static final String HEAP_FILES_DIR = ".data";
    public static final String SCHEMAS_DIR = ".schema";
    public static final String INDEXES_DIR = ".indexes";
    @NotNull
    private final HeapFileManager heapFileManager;
    @NotNull
    private final SchemaManager schemaManager;

    @NotNull
    private final IndexesManager<TreeIndex> treeIndexManager;


    public Engine(@NotNull File workingDirectory) throws IOException {
        checkIsDirectory(workingDirectory);
        this.heapFileManager = new HeapFileManager(ensureSubDirectoryExists(workingDirectory, HEAP_FILES_DIR));
        this.schemaManager = new SchemaManager(ensureSubDirectoryExists(workingDirectory, SCHEMAS_DIR));
        this.treeIndexManager = new IndexesManager<TreeIndex>(ensureSubDirectoryExists(workingDirectory, INDEXES_DIR));
    }

    public void initialize() {
        schemaManager.initialize();
    }

    public void deinitialize() {
        heapFileManager.deinitialize();
        schemaManager.deinitialize();
        treeIndexManager.deinitialize();
    }

    @NotNull
    public Cursor select(@NotNull SelectInstruction selectInstruction) throws IOException, LogicException {
        if (!selectInstruction.getWhere().isEmpty()) {
            return new FilteredCursor(getHeapFile(selectInstruction.getTableName()).getCursor()
                    , schemaManager.getSchema(selectInstruction.getTableName()).getColumn(selectInstruction.getWhere().getLeft()),
                    selectInstruction.getWhere().getRight());
        } else {
            return getHeapFile(selectInstruction.getTableName()).getCursor();
        }
    }

    @NotNull
    private HeapFile getHeapFile(@NotNull String tableName) throws LogicException {
        TableSchema schema = getSchema(tableName);
        return heapFileManager.getExistingFile(schema);
    }

    @NotNull
    private TableSchema getSchema(@NotNull String tableName) throws LogicException {
        TableSchema schema = schemaManager.getSchema(tableName);
        if (schema == null) {
            throw new LogicException("Table " + tableName + " does not exist");
        }
        return schema;
    }

    public void insert(@NotNull InsertInstruction insertInstruction) throws IOException, LogicException {
        HeapFile heapFile = getHeapFile(insertInstruction.getTableName());
        heapFile.insertRecord(constructRowObject(insertInstruction));
    }

    @NotNull
    private Row constructRowObject(InsertInstruction insertInstruction) throws LogicException {
        TableSchema schema = getSchema(insertInstruction.getTableName());
        List<Column> columns = new ArrayList<Column>();
        List<Object> objects = new ArrayList<Object>();
        for (InsertInstruction.ColumnNameAndData columnNameAndData : insertInstruction.getColumnNamesWithData()) {
            columns.add(schema.getColumn(columnNameAndData.getColumnName()));
            objects.add(columnNameAndData.getData());
        }
        return new Row(columns, objects);
    }

    public void createTable(@NotNull CreateTableInstruction createTableInstruction) throws LogicException {
        String tableName = createTableInstruction.getTableName();
        TableSchema schema = schemaManager.getSchema(tableName);
        if (schema != null) {
            throw new LogicException("Table " + tableName + " already exists");
        }
        TableSchema newSchema = new TableSchema(tableName,
                createTableInstruction.getColumns());
        heapFileManager.createNewHeapFile(newSchema);
        schemaManager.addNewSchema(newSchema);
    }

    public void createIndex(@NotNull CreateIndexInstruction createIndexInstruction) throws LogicException {
        String tableName = createIndexInstruction.getTableName();
        String indexName = createIndexInstruction.getIndexName();
        List<Column> columns = createIndexInstruction.getColumns();
        String indexStructure = createIndexInstruction.getIndexStructure();

        if (indexStructure.equals("BTREE")) {
            treeIndexManager.createIndexFor(tableName, columns, indexName, indexStructure);
        } else if (indexStructure.equals("HASH")) {
            assert (false);
            //treeIndexManager.createIndexFor(tableName, columns);
        }
    }

}
