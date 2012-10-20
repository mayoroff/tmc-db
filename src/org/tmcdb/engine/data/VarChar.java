package org.tmcdb.engine.data;

import org.jetbrains.annotations.NotNull;

/**
 * @author Pavel Talanov
 */
public final class VarChar implements Type {
    private final int numberOfChars;

    public VarChar(int numberOfChars) {
        assert numberOfChars > 0;
        this.numberOfChars = numberOfChars;
    }

    public int getNumberOfChars() {
        return numberOfChars;
    }

    @NotNull
    public static String uppercaseTypeName() {
        return "VARCHAR";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VarChar varChar = (VarChar) o;

        return numberOfChars == varChar.numberOfChars;
    }

    @Override
    public int hashCode() {
        return numberOfChars;
    }
}
