package org.talend.dataprep.dataset.store.hdfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.talend.dataprep.api.dataset.DataSetContent;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.dataset.exception.DataSetErrorCodes;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.schema.FormatGuess;
import org.talend.dataprep.schema.Serializer;

@org.springframework.context.annotation.Configuration
@ConditionalOnProperty(name = "dataset.content.store", havingValue = "hdfs", matchIfMissing = false)
public class HDFSContentStore implements DataSetContentStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSContentStore.class);

    private static final String HDFS_DIRECTORY = "talend/tdp/datasets/"; //$NON-NLS-1$

    private final FileSystem fileSystem;

    @Autowired
    private FormatGuess.Factory factory;

    @Value("${dataset.content.store.hdfs.location}")
    private String hdfsStoreLocation;

    public HDFSContentStore() {
        try {
            fileSystem = FileSystem.get(new URI(hdfsStoreLocation), new Configuration());
            LOGGER.info("HDFS file system: {} ({}).", fileSystem.getClass(), fileSystem.getUri());
        } catch (Exception e) {
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_CONNECT_TO_HDFS, e, TDPExceptionContext.build().put("location",
                    hdfsStoreLocation));
        }
    }

    private static Path getPath(DataSetMetadata dataSetMetadata) {
        return new Path(HDFS_DIRECTORY + dataSetMetadata.getId());
    }

    @Override
    public void storeAsRaw(DataSetMetadata dataSetMetadata, InputStream dataSetContent) {
        try (FSDataOutputStream outputStream = fileSystem.create(getPath(dataSetMetadata))) {
            IOUtils.copy(dataSetContent, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_STORE_DATASET_CONTENT, e, TDPExceptionContext.build().put("id",
                    dataSetMetadata.getId()));
        }
    }

    @Override
    public InputStream get(DataSetMetadata dataSetMetadata) {
        DataSetContent content = dataSetMetadata.getContent();
        if (content.getFormatGuessId() != null) {
            Serializer serializer = factory.getFormatGuess(content.getFormatGuessId()).getSerializer();
            return serializer.serialize(getAsRaw(dataSetMetadata), dataSetMetadata);
        } else {
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public InputStream getAsRaw(DataSetMetadata dataSetMetadata) {
        try {
            return fileSystem.open(getPath(dataSetMetadata));
        } catch (Exception e) {
            LOGGER.warn("File '{}' does not exist.", getPath(dataSetMetadata));
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public void delete(DataSetMetadata dataSetMetadata) {
        try {
            fileSystem.delete(getPath(dataSetMetadata), true);
        } catch (IOException e) {
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_DELETE_DATASET, e, TDPExceptionContext.build().put("dataSetId",
                    dataSetMetadata.getId()));
        }
    }

    @Override
    public void clear() {
        try {
            fileSystem.delete(new Path(HDFS_DIRECTORY), true);
        } catch (IOException e) {
            throw new TDPException(DataSetErrorCodes.UNABLE_TO_CLEAR_DATASETS, e);
        }
    }
}