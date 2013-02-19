package org.tmcdb.engine.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tmcdb.engine.schema.Column;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arseny Mayorov
 * @author Pavel Talanov
 */
public class Row {
    @NotNull
    private final Map<Column, Object> columnToValue;

    public Row(@NotNull List<Column> columns, @NotNull List<Object> values) {
        assert columns.size() == values.size();
        this.columnToValue = new LinkedHashMap<Column, Object>();
        int i = 0;
        for (Column column : columns) {
            columnToValue.put(column, values.get(i++));
        }
    }

    @Nullable
    public Object getValueForColumn(@NotNull Column column) {
        return columnToValue.get(column);
    }

    @NotNull
    public Collection<Column> getColumns() {
        return columnToValue.keySet();
    }
}
