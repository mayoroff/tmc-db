package org.tmcdb.heapfile.cursor;

import org.jetbrains.annotations.Nullable;
import org.tmcdb.engine.data.Row;

import java.io.IOException;

/**
 * @author Pavel Talanov
 */
public interface Cursor {
    @Nullable
    Row next() throws IOException;

    int getCurrentPageNumber();

    void close();
}
