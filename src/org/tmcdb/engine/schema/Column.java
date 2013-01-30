package org.tmcdb.engine.schema;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.data.Type;

import java.io.Serializable;

/**
 * @author Pavel Talanov
 */
public final class Column implements Serializable {

    @NotNull
    private final Type type;

    @NotNull
    private final String name;

    public Column(@NotNull String name, @NotNull Type type) {
        this.name = name;
        this.type = type;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Column column = (Column) o;

        if (!name.equals(column.name)) return false;
        if (!type.equals(column.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
