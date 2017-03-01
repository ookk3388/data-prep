package org.talend.dataprep.api.preparation;

import java.util.Collection;

/**
 * Base interface for preparation repositories (mongodb & in memory).
 *
 * This repository manage both Preparation and Step, hence the use of Idenfiable.
 *
 * @see Preparation
 * @see Step
 */
public interface PreparationRepository {

    void add(Identifiable object);

    <T extends Identifiable> T get(String id, Class<T> clazz);

    /**
     * @param dataSetId the wanted dataset id.
     * @return all preparations used by a dataset or an empty list if there's none.
     */
    Collection<Preparation> getByDataSet(String dataSetId);

    <T extends Identifiable> Collection<T> listAll(Class<T> clazz);

    void clear();

    void remove(Identifiable object);
}