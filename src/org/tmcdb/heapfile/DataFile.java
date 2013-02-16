package org.tmcdb.heapfile;

import java.io.IOException;

/**
 * @author Arseny Mayorov
 * @author Pavel Talanov
 */
public abstract interface DataFile {
    Page getPage(int pageId) throws IOException;
}
