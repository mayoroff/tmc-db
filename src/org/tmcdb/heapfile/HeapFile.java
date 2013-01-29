package org.tmcdb.heapfile;

import org.tmcdb.engine.data.HeapFilePage;
import org.tmcdb.engine.data.Page;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Arseny Mayorov.
 * Date: 22.10.12
 */
public class HeapFile implements DataFile {

    private MappedByteBuffer buffer = null;
    private RandomAccessFile file = null;
    static final String DATA_FOLDER_NAME = "./data/";
    static final int PAGE_SIZE = 4096;

    public HeapFile(String tableName) throws IOException {
        File file = new File(DATA_FOLDER_NAME + tableName);
        if (!file.exists())
            throw new FileNotFoundException();
        long size = file.length();

        this.file = new RandomAccessFile(DATA_FOLDER_NAME + tableName, "rw");
        buffer = this.file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
    }

    @Override
    public Page getPage(int pageId) throws IllegalArgumentException {
        if ((pageId + 1) * PAGE_SIZE > buffer.capacity())
            throw new IllegalArgumentException();

        buffer.position(pageId * PAGE_SIZE);
        ByteBuffer pageBuffer = buffer.slice();
        pageBuffer.limit(PAGE_SIZE);

        return new HeapFilePage(pageBuffer);
    }

    public static void createHeapFile(String tableName, int initialPages) throws IOException {
        new RandomAccessFile(DATA_FOLDER_NAME + tableName, "rw").getChannel().map(FileChannel.MapMode.READ_WRITE, 0, initialPages * PAGE_SIZE);
    }
}
