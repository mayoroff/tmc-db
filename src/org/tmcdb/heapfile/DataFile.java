package org.tmcdb.heapfile;

import java.io.IOException;

/**
 * Created by Arseny Mayorov.
 * Date: 22.10.12
 */

public abstract interface DataFile {
    Page getPage(int pageId) throws IOException;
}
