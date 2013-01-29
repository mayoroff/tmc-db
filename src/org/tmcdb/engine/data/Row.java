package org.tmcdb.engine.data;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.schema.Column;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Arseny Mayorov.
 * Date: 23.10.12
 */
public class Row {
    @NotNull
    private List<Column> columns;
    private List<Object> values;

    public Row(List<Column> columns, List<Object> values) {
        this.columns = columns;
        this.values = values;
    }

    public int sizeInBytes() {
        int size = 0;
        for (int i = 0; i < columns.size(); ++i) {
            if (columns.get(i).getType() == NumericType.INT) {
                return Integer.SIZE / Byte.SIZE;
            } else if (columns.get(i).getType() == NumericType.DOUBLE) {
                return Double.SIZE / Byte.SIZE;
            } else {
                try {
                    return ((String) values.get(i)).getBytes("UTF-8").length + Short.SIZE / Byte.SIZE;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return size;
    }

    public String getStringRepresentationAt(int index) {
        Type type = columns.get(index).getType();
        Object value = values.get(index);

        if (type == NumericType.INT) {
            return String.format("%d", (Integer) value);
        } else if (type == NumericType.DOUBLE) {
            return String.format("%f", (Double) value);
        } else return (String) value;
    }
}
