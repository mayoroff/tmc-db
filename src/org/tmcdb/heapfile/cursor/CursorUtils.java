package org.tmcdb.heapfile.cursor;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.data.Row;
import org.tmcdb.engine.schema.Column;

import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Pavel Talanov
 */
public final class CursorUtils {

    public static void toCSV(@NotNull Cursor cursor, @NotNull PrintStream output) throws IOException {
        Row row = cursor.next();
        while (row != null) {
            output.println(rowToCSV(row));
            row = cursor.next();
        }
    }

    @NotNull
    private static String rowToCSV(@NotNull Row row) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Column column : row.getColumns()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(row.getValueForColumn(column));
        }
        return sb.toString();
    }
}
