package org.tmcdb.heapfile;

import org.tmcdb.engine.data.Page;

/**
 * Created by Arseny Mayorov.
 * Date: 22.10.12
 */

public abstract interface DataFile {
    Page getPage(int pageId);
}
