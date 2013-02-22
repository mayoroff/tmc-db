package org.tmcdb.engine.indexes;

/**
 * Created by Arseny Mayorov.
 * Date: 22.02.13
 */
public interface IndexInterface<KeyType> {
    int getPageNumberFor(KeyType key);

    void setPageNumberFor(KeyType key);

    void deletePageNumberFor(KeyType key);
}
