package org.tmcdb.engine.data;

import org.jetbrains.annotations.NotNull;

/**
 * @author Pavel Talanov
 */
public enum NumericType implements Type {
    INT(4) {
        @NotNull
        @Override
        public Object getDefaultValue() {
            return 0;
        }
    },
    DOUBLE(8) {
        @NotNull
        @Override
        public Object getDefaultValue() {
            return 0.0;
        }
    };

    private final int size;

    private NumericType(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }
}
