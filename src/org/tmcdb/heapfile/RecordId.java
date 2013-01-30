package org.tmcdb.heapfile;

/**
 * @author Pavel Talanov
 */
public final class RecordId {
    private final int pageId;
    private final int slotNumber;

    public RecordId(int pageId, int slotNumber) {
        this.pageId = pageId;
        this.slotNumber = slotNumber;
    }

    public int getPageId() {
        return pageId;
    }

    public int getSlotNumber() {
        return slotNumber;
    }


}
