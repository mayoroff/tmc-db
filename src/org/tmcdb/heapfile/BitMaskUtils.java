package org.tmcdb.heapfile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Pavel Talanov
 */
public final class BitMaskUtils {

    private static final int SIZE = Byte.SIZE;

    public static boolean getIsOccupied(byte[] bitMask, int slotNumber) {
        assert bitMask.length >= slotNumber / SIZE;
        int byteNumber = slotNumber / SIZE;
        int bitNumber = slotNumber % SIZE;
        int mask = 1 << bitNumber;
        int value = bitMask[byteNumber];
        return (value & mask) != 0;
    }

    public static void setIsOccupied(byte[] bitMask, int slotNumber, boolean isOccupied) {
        assert bitMask.length >= slotNumber / SIZE;
        int byteNumber = slotNumber / SIZE;
        int bitNumber = slotNumber % SIZE;
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

    @NotNull
    public static Collection<Integer> getAllOccupiedSlots(byte[] bitMask, int slotsNumber) {
        Collection<Integer> result = new ArrayList<Integer>();
        for (int slotNumber = 0; slotNumber < slotsNumber; ++slotNumber) {
            int byteNumber = slotNumber / SIZE;
            int bitNumber = slotNumber % SIZE;
            int mask = 1 << bitNumber;
            if ((bitMask[byteNumber] & mask) != 0) {
                result.add(slotNumber);
            }
        }
        return result;
    }

    @NotNull
    public static Collection<Integer> getAllNonOccupiedSlots(byte[] bitMask, int slotsNumber) {
        Collection<Integer> result = new ArrayList<Integer>();
        for (int slotNumber = 0; slotNumber < slotsNumber; ++slotNumber) {
            int byteNumber = slotNumber / SIZE;
            int bitNumber = slotNumber % SIZE;
            int mask = 1 << bitNumber;
            if ((bitMask[byteNumber] & mask) == 0) {
                result.add(slotNumber);
            }
        }
        return result;
    }
}
