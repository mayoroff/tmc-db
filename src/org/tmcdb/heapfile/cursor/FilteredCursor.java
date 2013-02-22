package org.tmcdb.heapfile.cursor;

import org.jetbrains.annotations.Nullable;
import org.tmcdb.engine.data.Row;
import org.tmcdb.engine.schema.Column;

import java.io.IOException;

/**
 *
 * @author  Ilya Averyanov
 */

public class FilteredCursor implements Cursor {

    private Cursor cursor;

    private final Column column;

    private final Object value;

    public FilteredCursor(Cursor cursor, Column column, Object value) {
        this.cursor = cursor;
        this.column = column;
        this.value = value;
    }

    @Nullable
    @Override
    public Row next() throws IOException {
        Row result = cursor.next();
        while (result != null) {
            if (result.getValueForColumn(column).toString().equals(value)){
                return result;
            } else {
                result = cursor.next();
            }
        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() {
        cursor.close();
    }
}
