package org.tmcdb.engine.data;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author Pavel Talanov
 */
public interface Type extends Serializable {

    int getSize();

    @NotNull
    Object getDefaultValue();

    @NotNull
    String getPresentableName();
}
