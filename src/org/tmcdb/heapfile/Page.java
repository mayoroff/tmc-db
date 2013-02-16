package org.tmcdb.heapfile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tmcdb.engine.data.Row;
import org.tmcdb.heapfile.cursor.Cursor;

/**
 * @author Arseny Mayorov
 * @author Pavel Talanov
 */
public interface Page {
    @Nullable
    Row getRecord(int slotId);

    boolean insertRecord(int slotId, Row record);

    void deleteRecord(int slotId);

    @NotNull
    Cursor getCursor();
}
