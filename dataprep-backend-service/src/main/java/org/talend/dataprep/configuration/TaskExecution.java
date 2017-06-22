// ============================================================================
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Return the async task execution configuration.
 */
@Configuration
@SuppressWarnings("InsufficientBranchCoverage")
public class TaskExecution {

    /**
     * @return an Authenticated task executor for event multi casting.
     * @see DataPrepEvents
     */
    @Bean(name = "applicationEventMulticaster#executor")
    public TaskExecutor dataPrepAsyncTaskExecutor() {
        final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        taskExecutor.initialize();
        return taskExecutor;
    }

    /**
     * @return A {@link TaskExecutor} for non-blocking HTTP request execution.
     * @see Async
     */
    @Bean("requestMappingHandlerMapping#executor")
    public TaskExecutor asyncTaskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    /**
     * @return A {@link TaskExecutor} for non-blocking CSV serialization.
     * @see org.talend.dataprep.schema.csv.CSVSerializer
     */
    @Bean(name = "serializer#csv#executor")
    TaskExecutor getCsvTaskExecutor() {
        return getAsyncExecutor();
    }

    /**
     * @return A {@link TaskExecutor} for non-blocking HTML serialization.
     * @see org.talend.dataprep.schema.csv.CSVSerializer
     */
    @Bean(name = "serializer#html#executor")
    TaskExecutor getHtmlTaskExecutor() {
        return getAsyncExecutor();
    }

    /**
     * @return A {@link TaskExecutor} for non-blocking CSV serialization.
     * @see org.talend.dataprep.schema.xls.XlsSerializer
     */
    @Bean(name = "serializer#excel#executor")
    TaskExecutor getExcelTaskExecutor() {
        return getAsyncExecutor();
    }

    /**
     * @return A {@link TaskExecutor} for non-blocking JSON serialization.
     */
    @Bean(name = "serializer#json#executor")
    TaskExecutor getJsonTaskExecutor() {
        return getAsyncExecutor();
    }

    /**
     * @return an Authenticated task executor ready to run.
     */
    protected AsyncListenableTaskExecutor getAsyncExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.initialize();
        return executor;
    }

}
