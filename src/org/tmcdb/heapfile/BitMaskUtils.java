package org.tmcdb.heapfile;

/**
 * @author Pavel Talanov
 */
public final class BitMaskUtils {
    public static boolean getIsOccupied(byte[] bitMask, int slotNumber) {
        assert bitMask.length >= slotNumber / 8;
        int byteNumber = slotNumber / 8;
        int bitNumber = slotNumber % 8;
        int mask = 1 << bitNumber;
        int value = bitMask[byteNumber];
        return (value & mask) != 0;
    }

    public static void setIsOccupied(byte[] bitMask, int slotNumber, boolean isOccupied) {
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
