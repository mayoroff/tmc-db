package org.tmcdb.heapfile;

import org.jetbrains.annotations.NotNull;
import org.tmcdb.engine.data.Row;
import org.tmcdb.engine.schema.TableSchema;
import org.tmcdb.heapfile.cursor.Cursor;
import org.tmcdb.heapfile.cursor.HeapFileCursor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arseny Mayorov
 * @author Pavel Talanov
 */
public class HeapFile {

    public static final int DEFAULT_INITIAL_PAGES = 16;
    public static final int PAGE_SIZE = 4096;

    @NotNull
    private final RandomAccessFile file;
    @NotNull
    private final TableSchema tableSchema;
    @NotNull
    private final Map<Integer, HeapFilePage> idToPage = new HashMap<Integer, HeapFilePage>();
    @NotNull
    private int firstPageWithAnEmptySlot = 0;

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
    public HeapFilePage getPage(int pageId) throws IOException {
        HeapFilePage page = idToPage.get(pageId);
        if (page != null) {
            return page;
        }
        return createPage(pageId);
    }

    @NotNull
    private HeapFilePage createPage(int pageId) throws IOException {
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

    public int pagesNumber() throws IOException {
        long fileSize = file.getChannel().size();
        assert fileSize != 0;
        assert fileSize % PAGE_SIZE == 0;
        return (int) (fileSize / PAGE_SIZE);
    }

    @NotNull
    public Cursor getCursor() throws IOException {
        return new HeapFileCursor(this);
    }

    public void insertRecord(@NotNull Row record) throws IOException {
        RecordId emptySlot = findEmptySlot();
        getPage(emptySlot.getPageId()).insertRecord(emptySlot.getSlotNumber(), record);
    }

    @NotNull
    private RecordId findEmptySlot() throws IOException {
        for (int pageNumber = firstPageWithAnEmptySlot; pageNumber < pagesNumber(); ++pageNumber) {
            HeapFilePage page = getPage(pageNumber);
            Collection<Integer> emptySlots = page.getEmptySlots();
            if (!emptySlots.isEmpty()) {
                Integer slotNumber = emptySlots.iterator().next();
                return new RecordId(pageNumber, slotNumber);
            } else {
                firstPageWithAnEmptySlot = pageNumber;
            }
        }
        int newPageId = pagesNumber();
        HeapFilePage newPage = createPage(newPageId);
        assert newPage.getEmptySlots().contains(0);
        return new RecordId(newPageId, 0);
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
