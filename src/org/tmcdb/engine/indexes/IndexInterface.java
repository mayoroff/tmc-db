package org.tmcdb.engine.indexes;

import java.util.List;

/**
 * Created by Arseny Mayorov.
 * Date: 22.02.13
 */
public interface IndexInterface<KeyType> {
    List<Integer> getPageNumber(KeyType key);

    void addPageNumber(KeyType key, Integer pageNumber);

    void removePageNumber(KeyType key, Integer pageNumber);
}
