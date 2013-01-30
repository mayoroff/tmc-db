package org.tmcdb.heapfile;

/**
 * Created by Arseny Mayorov.
 * Date: 22.10.12
 */

public abstract interface DataFile {
    Page getPage(int pageId);
}
