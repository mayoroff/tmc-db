package org.tmcdb;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.Engine;
import org.tmcdb.heapfile.cursor.Cursor;
import org.tmcdb.heapfile.cursor.CursorUtils;
import org.tmcdb.parser.Parser;
import org.tmcdb.parser.instructions.CreateTableInstruction;
import org.tmcdb.parser.instructions.InsertInstruction;
import org.tmcdb.parser.instructions.Instruction;
import org.tmcdb.parser.instructions.SelectInstruction;

import java.io.File;
import java.io.PrintStream;

/**
 * @author Pavel Talanov
 */
public final class Database {

    public static final String SUCCESS = "OK";
    public static final String FAILURE = "ERROR";

    @NotNull
    private final Engine engine;
    @NotNull
    private final PrintStream outputStream;

    public Database(@NotNull File workingDirectory, @NotNull PrintStream outputStream) {
        this.outputStream = outputStream;
        this.engine = new Engine(workingDirectory);
    }

    public void exec(@NotNull String query) {
        try {
            Instruction instruction = Parser.parse(query);
            if (instruction instanceof SelectInstruction) {
                Cursor cursor = engine.select((SelectInstruction) instruction);
                CursorUtils.toCSV(cursor, outputStream);
            } else if (instruction instanceof InsertInstruction) {
                engine.insert((InsertInstruction) instruction);
                outputStream.println(SUCCESS + " 1");
            } else if (instruction instanceof CreateTableInstruction) {
                engine.createTable((CreateTableInstruction) instruction);
                outputStream.println(SUCCESS);
            } else {
                throw new IllegalStateException("Unknown instruction: " + instruction.getClass());
            }
        } catch (Exception e) {
            outputStream.println(FAILURE + System.getProperty("line.separator") + e.getMessage());
        }
        outputStream.println();
    }

    public void initialize() {
        engine.initialize();
    }

    public void deinitialize() {
        engine.deinitialize();
    }
}
