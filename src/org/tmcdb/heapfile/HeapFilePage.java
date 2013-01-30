package org.tmcdb.heapfile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tmcdb.engine.data.NumericType;
import org.tmcdb.engine.data.Row;
import org.tmcdb.engine.data.Type;
import org.tmcdb.engine.data.VarChar;
import org.tmcdb.engine.schema.Column;
import org.tmcdb.engine.schema.TableSchema;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.tmcdb.heapfile.HeapFile.PAGE_SIZE;

/**
 * @author: Arseny Mayorov
 * @author: Pavel Talanov
 */

/**
 * Heap file page organised in the following manner:
 * first comes a bitmap indicating which slots are empty(0) and which are occupied(1)
 * then come several fixed size slots, which are addressed by slotId
 * One slot can contain one row of the table
 */
public class HeapFilePage implements Page {

    @NotNull
    private final ByteBuffer buffer;
    private final int bitMaskSize;
    private final int slotsNumber;
    @NotNull
    private final TableSchema schema;

    public HeapFilePage(@NotNull ByteBuffer buffer, @NotNull TableSchema schema) {
        this.schema = schema;
        //NOTE: not optimal
        this.bitMaskSize = PAGE_SIZE / schema.getRowSize();
        this.slotsNumber = (PAGE_SIZE - this.bitMaskSize) / schema.getRowSize();
        this.buffer = buffer;
    }

    @Nullable
    public Row getRecord(int slotId) {
        if (!isOccupied(slotId)) {
            return null;
        }
        buffer.position(getSlotPosition(slotId));
        return new Row(schema.getColumns(), readValues());
    }

    private int getSlotPosition(int slotId) {
        int offsetFromBufferStart = bitMaskSize + slotId * schema.getRowSize();
        assert offsetFromBufferStart < PAGE_SIZE - schema.getRowSize();
        return offsetFromBufferStart;
    }

    @NotNull
    private List<Object> readValues() {
        List<Object> values = new ArrayList<Object>();
        for (Column column : schema.getColumns()) {
            Type type = column.getType();
            if (type == NumericType.INT) {
                values.add(buffer.getInt());
            } else if (type == NumericType.DOUBLE) {
                values.add(buffer.getDouble());
            } else {
                assert type instanceof VarChar;
                byte[] strData = new byte[type.getSize()];
                buffer.get(strData);
                try {
                    values.add(new String(strData, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    values.add(null);
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    @Override
    public boolean insertRecord(int slotId, @NotNull Row record) {
        checkRecordIsCorrect(record);
        if (isOccupied(slotId)) {
            return false;
        }
        buffer.position(getSlotPosition(slotId));
        writeValues(record);
        setIsOccupied(slotId, true);
        return true;
    }

    private void writeValues(@NotNull Row record) {
        for (Column column : schema.getColumns()) {
            Object valueForColumn = record.getValueForColumn(column);
            Type type = column.getType();
            if (valueForColumn == null) {
                valueForColumn = type.getDefaultValue();
            }
            if (type == NumericType.INT) {
                buffer.putInt((Integer) valueForColumn);
            } else if (type == NumericType.DOUBLE) {
                buffer.putDouble((Double) valueForColumn);
            } else {
                assert type instanceof VarChar;
                String stringValue = (String) valueForColumn;
                //TODO: charset?
                //TODO: correct number of bytes
                buffer.put(stringValue.getBytes());
            }
        }
    }

    private void checkRecordIsCorrect(@NotNull Row record) {
        for (Column column : record.getColumns()) {
            if (!schema.getColumns().contains(column)) {
                throw new IllegalStateException("Trying to insert column " + column.getName() +
                        " to page for table which doesn't contain such column");
            }
        }
    }

    @Override
    public void deleteRecord(int slotId) {
        if (!isOccupied(slotId)) {
            //TODO:
            throw new IllegalStateException("Trying to delete record already deleted");
        }
        setIsOccupied(slotId, false);
    }

    private boolean isOccupied(int slotId) {
        return getIsOccupied(getBitMask(), slotId);
    }

    private void setIsOccupied(int slotId, boolean isOccupied) {
        byte[] bitMask = getBitMask();
        setIsOccupied(bitMask, slotId, isOccupied);
        writeBitMask(bitMask);
    }

    private byte[] getBitMask() {
        byte[] bitMask = new byte[bitMaskSize];
        buffer.position(0);
        buffer.get(bitMask);
        return bitMask;
    }

    private void writeBitMask(byte[] bitMask) {
        buffer.position(0);
        buffer.put(bitMask);
    }

    private boolean getIsOccupied(byte[] bitMask, int slotNumber) {
        assert slotNumber < slotsNumber;
        assert bitMask.length >= slotNumber / 8;
        int byteNumber = slotNumber / 8;
        int bitNumber = slotNumber % 8;
        int mask = 1 << bitNumber;
        int value = bitMask[byteNumber];
        return (value & mask) != 0;
    }

    private void setIsOccupied(byte[] bitMask, int slotNumber, boolean isOccupied) {
        assert slotNumber < slotsNumber;
        assert bitMask.length >= slotNumber / 8;
        int byteNumber = slotNumber / 8;
        int bitNumber = slotNumber % 8;
        int mask = 1 << bitNumber;
        int value = bitMask[byteNumber];
        //TODO: test this code more, not really sure it's the right way to implement bitmasks
        if (isOccupied) {
            // assert (value | mask) <= Byte.MAX_VALUE;
            bitMask[byteNumber] = (byte) (value | mask);
        } else {
            //   assert (value & mask) <= Byte.MAX_VALUE;
            bitMask[byteNumber] = (byte) (value & ~mask);
        }
    }
}
