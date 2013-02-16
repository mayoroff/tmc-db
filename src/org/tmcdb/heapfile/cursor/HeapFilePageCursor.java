package org.tmcdb.heapfile.cursor;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.data.Row;
import org.tmcdb.heapfile.HeapFilePage;

import java.util.Iterator;

/**
 * @author Pavel Talanov
 */
public final class HeapFilePageCursor implements Cursor {

    @NotNull
    private final HeapFilePage page;
    @NotNull
    private final Iterator<Integer> occupiedSlotsIterator;

    public HeapFilePageCursor(@NotNull HeapFilePage page) {
        this.page = page;
        this.occupiedSlotsIterator = page.getOccupiedSlots().iterator();
    }

    @Override
    public Row next() {
        if (occupiedSlotsIterator.hasNext()) {
            return page.getRecord(occupiedSlotsIterator.next());
        } else {
            return null;
        }
    }
}
