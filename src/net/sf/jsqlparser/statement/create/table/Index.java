package net.sf.jsqlparser.statement.create.table;

import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.List;

/**
 * An index (unique, primary etc.) in a CREATE TABLE statement
 */
public class Index {

    private String type;
    private String structure;
    private List columnsNames;
    private String name;
    private String tableName;

    /**
     * A list of strings of all the columns regarding this index
     */
    public List getColumnsNames() {
        return columnsNames;
    }

    public String getName() {
        return name;
    }

    /**
     * The type of this index: "PRIMARY KEY", "UNIQUE", "INDEX"
     */
    public String getType() {
        return type;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String s) {
        structure = s;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String s) {
        tableName = s;
    }

    public void setColumnsNames(List list) {
        columnsNames = list;
    }

    public void setName(String string) {
        name = string;
    }

    public void setType(String string) {
        type = string;
    }

    public String toString() {
        return type + " " + PlainSelect.getStringList(columnsNames, true, true) + (name != null ? " " + name : "");
    }
}