package org.tmcdb.engine;

import org.jetbrains.annotations.NotNull;

/**
 * @author Pavel Talanov
 *         <p/>
 *         Represents a logic error during query processing:
 *         For example, inserting records from non-existent table
 *         Messages should contain text presentable to the user
 */

public class LogicException extends Exception {

    public LogicException(@NotNull String message) {
        super(message);
    }

    public LogicException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }

    public LogicException(@NotNull Throwable cause) {
        super(cause);
    }
}
