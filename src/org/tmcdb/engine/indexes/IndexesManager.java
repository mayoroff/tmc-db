package org.tmcdb.engine.indexes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tmcdb.engine.schema.Column;
import org.tmcdb.utils.jdbm.RecordManager;
import org.tmcdb.utils.jdbm.RecordManagerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.tmcdb.utils.DirectoryUtils.checkIsDirectory;

/**
 * Created by Arseny Mayorov.
 * Date: 22.02.13
 */
public final class IndexesManager<IndexType> {
    @NotNull
    private final File indexesDir;

    private RecordManager recman = null;

    public IndexesManager(@NotNull File indexesDir) throws IOException {
        checkIsDirectory(indexesDir);
        this.indexesDir = indexesDir;
        this.recman = RecordManagerFactory.createRecordManager(indexesDir.getAbsolutePath());
    }

    public void deinitialize() {
        try {
            recman.commit();
            recman.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Nullable
    public IndexType getIndexFor(@NotNull String tableName, @NotNull String columnName) {
        assert (false);
        return null;
    }

    @Nullable
    public IndexType createIndexFor(@NotNull String tableName, @NotNull List<Column> columnNames,
                                    @NotNull String indexName, @NotNull String indexStructure) {
        String columnsString = new String();
        for (Column c : columnNames) {
            columnsString = columnsString + c.getName() + ".";
        }

        if (indexStructure.equals("BTREE")) {
            return (IndexType) new TreeIndex(indexesDir + "\\" + indexName + "." + tableName + "." + columnsString);
        }
        if (indexStructure.equals("HASH")) {
            assert (false);
        }
        return null;
    }
}
