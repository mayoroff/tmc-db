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
    //TODO:
//    public int sizeInBytes() {
//        int size = 0;
//        for (int i = 0; i < columns.size(); ++i) {
//            if (columns.get(i).getType() == NumericType.INT) {
//                return Integer.SIZE / Byte.SIZE;
//            } else if (columns.get(i).getType() == NumericType.DOUBLE) {
//                return Double.SIZE / Byte.SIZE;
//            } else {
//                try {
//                    return ((String) values.get(i)).getBytes("UTF-8").length + Short.SIZE / Byte.SIZE;
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return size;
//    }
//
//    public String getStringRepresentationAt(int index) {
//        Type type = columns.get(index).getType();
//        Object value = values.get(index);
//
//        if (type == NumericType.INT) {
//            return String.format("%d", (Integer) value);
//        } else if (type == NumericType.DOUBLE) {
//            return String.format("%f", (Double) value);
//        } else return (String) value;
//    }
}
