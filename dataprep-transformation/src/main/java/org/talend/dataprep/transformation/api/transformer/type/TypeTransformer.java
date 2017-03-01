package org.talend.dataprep.transformation.api.transformer.type;

import org.talend.dataprep.transformation.api.transformer.input.TransformerConfiguration;

/**
 * Definition of a Dataset content Type (column, records, ...) transformer/serializer.
 */
public interface TypeTransformer {

    /**
     * Apply the transformation.
     *
     * @param configuration The transformer configuration that holds all the needed information for the transformation.
     */
    void process(final TransformerConfiguration configuration);
}