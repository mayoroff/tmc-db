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
}
