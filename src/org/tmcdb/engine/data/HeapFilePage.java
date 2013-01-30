package org.tmcdb.engine.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tmcdb.engine.schema.Column;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arseny Mayorov.
 * Date: 22.10.12
 */
public class HeapFilePage implements Page {

    ByteBuffer buffer = null;

    public HeapFilePage(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    @Nullable
    public Row getRecord(@NotNull List<Column> columns, int rid) {
        short step = 0;
        for (int i = 0; i < rid; ++i) {
            short currentStep = buffer.getShort(step);
            if (currentStep == 0)
                return null;
            step += currentStep;
        }
        if (buffer.getShort(step) == 0) {
            return null;
        }

        return new Row(columns, readValues(columns, step));
    }

    @NotNull
    private List<Object> readValues(@NotNull List<Column> columns, short step) {
        List<Object> values = new ArrayList<Object>();
        for (int i = 0; i < columns.size(); ++i) {
            Type type = columns.get(i).getType();
            if (type == NumericType.INT) {
                values.add(i, buffer.getInt(step));
            } else if (type == NumericType.DOUBLE) {
                values.add(i, buffer.getDouble(step));
            } else {
                byte[] strData = new byte[buffer.getShort(step)];
                step += Short.SIZE / Byte.SIZE;
                buffer.get(strData, step, strData.length);
                try {
                    values.add(i, new String(strData, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    values.add(i, null);
                    e.printStackTrace();
                }
            }

        }
        return values;
    }

    @Override
    public boolean insertRecord(@NotNull Row record) {
        throw new UnsupportedOperationException("insertRecord");
    }

    @Override
    public void deleteRecord(int rid) {
        throw new UnsupportedOperationException("deleteRecord");
    }

    public int numOfRows() {
        short step = 0;
        for (int i = 0; ; ++i) {
            short currentStep = buffer.getShort(step);
            if (currentStep == 0)
                return i;
            step += currentStep;
        }
    }
}
