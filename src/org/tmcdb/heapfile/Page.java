package org.tmcdb.heapfile;

import org.tmcdb.engine.data.Row;

/**
 * Created by Arseny Mayorov.
 * Date: 22.10.12
 */

public interface Page {
    Row getRecord(int slotId);

    boolean insertRecord(int slotId, Row record);

    void deleteRecord(int slotId);
}
