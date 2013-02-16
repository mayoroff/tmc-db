package org.tmcdb.heapfile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tmcdb.engine.data.NumericType;
import org.tmcdb.engine.data.Row;
import org.tmcdb.engine.data.Type;
import org.tmcdb.engine.data.VarChar;
import org.tmcdb.engine.schema.Column;
import org.tmcdb.engine.schema.TableSchema;
import org.tmcdb.heapfile.cursor.Cursor;
import org.tmcdb.heapfile.cursor.HeapFilePageCursor;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.tmcdb.heapfile.HeapFile.PAGE_SIZE;

/**
 * @author Arseny Mayorov
 * @author Pavel Talanov
 */

/**
 * Heap file page organised in the following manner:
 * first comes a bitmap indicating which slots are empty(0) and which are occupied(1)
 * then come several fixed size slots, which are addressed by slotId
 * One slot can contain one row of the table
 */
public class HeapFilePage implements Page {

    private static final byte VARCHAR_PLACEHOLDER_VALUE = 0;

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
                values.add(readVarchar((VarChar) type));
            }
        }
        return values;
    }

    @Nullable
    private String readVarchar(@NotNull VarChar type) {
        byte[] strData = new byte[type.getSize()];
        buffer.get(strData);
        int meaningfulBytesCount = 0;
        for (int i = 0; i < strData.length; ++i, ++meaningfulBytesCount) {
            if (strData[i] == VARCHAR_PLACEHOLDER_VALUE) {
                break;
            }
        }
        byte[] meaningfulBytes = Arrays.copyOf(strData, meaningfulBytesCount);
        try {
            return new String(meaningfulBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
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
                writeVarChar((String) valueForColumn, (VarChar) type);
            }
        }
    }

    private void writeVarChar(@NotNull String valueForColumn, @NotNull VarChar type) {
        assert valueForColumn.length() <= type.getNumberOfChars();
        //TODO: charset?
        byte[] meaningfulBytes = valueForColumn.getBytes();
        int numberOfBytesToWrite = type.getNumberOfChars() * (Character.SIZE / Byte.SIZE);
        buffer.put(meaningfulBytes);
        byte[] placeHolderBytes = new byte[numberOfBytesToWrite - meaningfulBytes.length];
        Arrays.fill(placeHolderBytes, VARCHAR_PLACEHOLDER_VALUE);
        buffer.put(placeHolderBytes);
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
        assert slotId < slotsNumber;
        return BitMaskUtils.getIsOccupied(getBitMask(), slotId);
    }

    private void setIsOccupied(int slotId, boolean isOccupied) {
        byte[] bitMask = getBitMask();
        assert slotId < slotsNumber;
        BitMaskUtils.setIsOccupied(bitMask, slotId, isOccupied);
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

    @NotNull
    public Collection<Integer> getOccupiedSlots() {
        return BitMaskUtils.getAllOccupiedSlots(getBitMask(), slotsNumber);
    }

    @NotNull
    public Cursor getCursor() {
        return new HeapFilePageCursor(this);
    }
}
