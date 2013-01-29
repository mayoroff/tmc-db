package org.tmcdb.engine.data;

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
    public Row getRecord(List<Column> columns, int rid) {
        short step = 0;
        for (int i = 0; i < rid; ++i) {
            short currentStep = buffer.getShort(step);
            if (currentStep == 0)
                return null;
            step += currentStep;
        }
        if (0 == buffer.getShort(step)) {
            return null;
        }

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
        return new Row(columns, values);
    }

    @Override
    public boolean insertRecord(Row record) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteRecord(int rid) {
        //To change body of implemented methods use File | Settings | File Templates.
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
