package org.tmcdb.heapfile.cursor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tmcdb.engine.data.Row;
import org.tmcdb.heapfile.HeapFile;

import java.io.IOException;

/**
 * @author Pavel Talanov
 * @author Arseny Mayorov
 */
public final class HeapFileCursor implements Cursor {

    @Nullable
    private HeapFile file;
    private int currentPageNumber = 0;
    @NotNull
    private Cursor currentPageCursor;

    public HeapFileCursor(@NotNull HeapFile file) throws IOException {
        this.file = file;
        this.currentPageCursor = file.getPage(currentPageNumber).getCursor();
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    @Override
    public Row next() throws IOException {
        assert file != null : "Cursor already closed";
        assert currentPageNumber < file.pagesNumber();
        Row result = currentPageCursor.next();
        while (result == null) {
            currentPageNumber++;
            if (currentPageNumber >= file.pagesNumber()) {
                return null;
            }
            currentPageCursor = file.getPage(currentPageNumber).getCursor();
            result = currentPageCursor.next();
        }
        return result;
    }

    @Override
    public void close() {
        file = null;
    }
}
