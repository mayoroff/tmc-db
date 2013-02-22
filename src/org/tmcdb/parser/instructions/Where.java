package org.tmcdb.parser.instructions;

import java.lang.Boolean;
import java.lang.String;
/**
 * @author Ilya Averyanov
 */
public class Where {

    private String left = null;

    private String right = null;

    private String operation = null;

    public Boolean isEmpty() {
        return empty;
    }

    public String getOperation() {
        return operation;
    }

    public String getRight() {
        return right;
    }

    public String getLeft() {
        return left;
    }

    private Boolean empty;

    public Where() {
        this.empty = true;
    }

    public Where(String left, String right, String operation) {
        this.left = left;
        this.right = right;
        this.operation = operation;
        this.empty = left.isEmpty() && right.isEmpty() && operation.isEmpty();

    }
}
