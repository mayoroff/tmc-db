package org.tmcdb.heapfile.cursor;

import org.jetbrains.annotations.Nullable;
import org.tmcdb.engine.data.Row;

/**
 * @author Pavel Talanov
 */
public interface Cursor {
    @Nullable
    Row next();

    void close();
}
