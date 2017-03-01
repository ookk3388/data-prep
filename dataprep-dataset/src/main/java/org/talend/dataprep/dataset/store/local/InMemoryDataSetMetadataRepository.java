package org.talend.dataprep.dataset.store.local;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.talend.dataprep.DistributedLock;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;

public class InMemoryDataSetMetadataRepository implements DataSetMetadataRepository {

    private final Map<String, DataSetMetadata> store = new HashMap<>();

    @Autowired
    private ApplicationContext appcontext;

    @Override
    public Iterable<DataSetMetadata> list() {
        return store.values();
    }

    @Override
    public synchronized void add(DataSetMetadata dataSetMetadata) {
        store.put(dataSetMetadata.getId(), dataSetMetadata);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public int size() {
        return store.size();
    }

    @Override
    public DataSetMetadata get(String id) {
        return store.get(id);
    }

    @Override
    public void remove(String id) {
        store.remove(id);
    }

    @Override
    public DistributedLock createDatasetMetadataLock(String id) {
        return appcontext.getBean(DistributedLock.class, DATASET_LOCK_PREFIX + id);
    }
}