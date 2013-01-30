package org.tmcdb.heapfile;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.schema.TableSchema;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arseny Mayorov
 * @author Pavel Talanov
 */
public class HeapFile implements DataFile {

    @NotNull
    private final MappedByteBuffer buffer;
    @NotNull
    private final RandomAccessFile file;
    static final int PAGE_SIZE = 4096;
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
        long size = file.length();

        this.file = new RandomAccessFile(pathToFile, "rw");
        this.buffer = this.file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
    }

    @Override
    public Page getPage(int pageId) throws IllegalArgumentException {
        if ((pageId + 1) * PAGE_SIZE > buffer.capacity()) {
            throw new IllegalArgumentException();
        }
        HeapFilePage page = idToPage.get(pageId);
        if (page != null) {
            return page;
        }
        buffer.position(pageId * PAGE_SIZE);
        ByteBuffer pageBuffer = buffer.slice();
        pageBuffer.limit(PAGE_SIZE);
        page = new HeapFilePage(pageBuffer, tableSchema);
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

    public static void createHeapFile(String path, int initialPages) throws IOException {
        RandomAccessFile file = new RandomAccessFile(path, "rw");
        file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, initialPages * PAGE_SIZE);
        file.close();
    }
}
