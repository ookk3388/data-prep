// ============================================================================
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.api.service.command.dataset;

import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.command.Defaults;
import org.talend.dataprep.command.GenericCommand;

import java.io.InputStream;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * Command to list dataset import types.
 */
@Component
@Scope(SCOPE_PROTOTYPE)
public class DataSetGetImports extends GenericCommand<InputStream> {

    public DataSetGetImports() {
        super(GenericCommand.DATASET_GROUP);
        execute(() -> new HttpGet(datasetServiceUrl + "/datasets/imports"));
        on(HttpStatus.OK).then(Defaults.pipeStream());
    }

}
