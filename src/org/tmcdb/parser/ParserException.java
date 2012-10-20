package org.tmcdb.parser;

import org.jetbrains.annotations.NotNull;

/**
 * @author Pavel Talanov
 */
//NOTE: messages should contain text presentable to the user
public final class ParserException extends RuntimeException {

    public ParserException(@NotNull String message) {
        super(message);
    }
}
