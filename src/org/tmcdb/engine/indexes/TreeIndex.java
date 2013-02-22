package org.tmcdb.engine.indexes;

import org.tmcdb.utils.jdbm.PrimaryTreeMap;
import org.tmcdb.utils.jdbm.RecordManager;

import java.util.Collections;
import java.util.List;

/**
 * Created by Arseny Mayorov.
 * Date: 22.02.13
 */
public class TreeIndex implements IndexInterface {

    private String indexFileName;
    private RecordManager recman;
    private PrimaryTreeMap<String, List<Integer>> map;

    public TreeIndex(String indexFileName, RecordManager recman) {
        this.indexFileName = indexFileName;
        this.recman = recman;
        this.map = recman.treeMap(indexFileName);
    }

    @Override
    public List<Integer> getPageNumber(Object key) {
        return this.map.get(key);
    }

    @Override
    public void addPageNumber(Object key, Integer pageNumber) {

        if (!this.map.containsKey(key)) {
            this.map.put(key.toString(), Collections.singletonList(pageNumber));
            return;
        }

        this.map.get(key).add(pageNumber);
    }

    @Override
    public void removePageNumber(Object key, Integer pageNumber) {
        if (!this.map.containsKey(key)) {
            return;
        }

        this.map.get(key).remove(pageNumber);
    }
}
