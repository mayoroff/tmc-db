package org.tmcdb.heapfile.cursor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tmcdb.engine.data.Row;
import org.tmcdb.heapfile.HeapFilePage;

import java.util.Iterator;

/**
 * @author Pavel Talanov
 */
public final class HeapFilePageCursor implements Cursor {

    @Nullable
    private HeapFilePage page;
    @NotNull
    private final Iterator<Integer> occupiedSlotsIterator;

    public HeapFilePageCursor(@NotNull HeapFilePage page) {
        this.page = page;
        this.occupiedSlotsIterator = page.getOccupiedSlots().iterator();
    }

    @Override
    public Row next() {
        assert page != null : "Cursor already closed";
        if (occupiedSlotsIterator.hasNext()) {
            return page.getRecord(occupiedSlotsIterator.next());
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        page = null;
    }

    public int getCurrentPageNumber() {
        return 0;
    }
}
