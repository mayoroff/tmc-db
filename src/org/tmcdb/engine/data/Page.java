package org.tmcdb.engine.data;

import org.tmcdb.engine.schema.Column;

import java.util.List;

/**
 * Created by Arseny Mayorov.
 * Date: 22.10.12
 */

public interface Page {
    Row getRecord(List<Column> columns, int rid);

    boolean insertRecord(Row record);

    void deleteRecord(int rid);

    int numOfRows();
}
