package org.tmcdb.heapfile;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.schema.TableSchema;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arseny Mayorov
 * @author Pavel Talanov
 */
public class HeapFile implements DataFile {

    public static final int DEFAULT_INITIAL_PAGES = 16;
    public static final int PAGE_SIZE = 4096;

    @NotNull
    private final RandomAccessFile file;
    @NotNull
    private final TableSchema tableSchema;
    @NotNull
    private final Map<Integer, HeapFilePage> idToPage = new HashMap<Integer, HeapFilePage>();

    public HeapFile(@NotNull String pathToFile, @NotNull TableSchema tableSchema) throws IOException {
        this.tableSchema = tableSchema;
        File file = new File(pathToFile);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        this.file = new RandomAccessFile(pathToFile, "rw");
    }

    /*
        Creates page if it doesn't exist
     */
    @Override
    public Page getPage(int pageId) throws IOException {
        HeapFilePage page = idToPage.get(pageId);
        if (page != null) {
            return page;
        }
        return createPage(pageId);
    }

    @NotNull
    private Page createPage(int pageId) throws IOException {
        MappedByteBuffer pageBuffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE, pageId * PAGE_SIZE, PAGE_SIZE);
        pageBuffer.limit(PAGE_SIZE);
        HeapFilePage page = new HeapFilePage(pageBuffer, tableSchema);
        idToPage.put(pageId, page);
        return page;
    }

    public void deinitialize() {
        try {
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createEmptyHeapFile(String path) throws IOException {
        createEmptyHeapFile(path, DEFAULT_INITIAL_PAGES);
    }

    public static void createEmptyHeapFile(String path, int initialPages) throws IOException {
        RandomAccessFile file = new RandomAccessFile(path, "rw");
        file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, initialPages * PAGE_SIZE);
        file.close();
    }
}
