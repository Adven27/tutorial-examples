package javaeetutorial.batch.messagesender.beans;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static javax.batch.runtime.BatchRuntime.getJobOperator;
import static javax.batch.runtime.BatchStatus.COMPLETED;
import static javax.ejb.LockType.READ;

@Singleton
@Lock(READ)
@LocalBean
public class BatchMetrics {
    ConcurrentHashMap<Long, Long> jobs = new ConcurrentHashMap<>();

    public Long metric(long jobExecution, String step, Metric.MetricType metric) {
        Long stub = jobs.get(jobExecution);
        if (stub != null) {
            return stub;
        }
        return metrics(jobExecution, step).get(metric);
    }

    public boolean completed(long jobExecution) {
        Long stub = jobs.get(jobExecution);
        if (stub != null) {
            return stub == 499;
        }
        return getJobOperator().getJobExecution(jobExecution).getBatchStatus() == COMPLETED;
    }

    public Map<Metric.MetricType, Long> metrics(long jobExecution, String step) {
        for (StepExecution se : getJobOperator().getStepExecutions(jobExecution)) {
            if (se.getStepName().equals(step)) {
                return getMetricsMap(se.getMetrics());
            }
        }
        return new HashMap<>();
    }

    private static Map<Metric.MetricType, Long> getMetricsMap(Metric[] metrics) {
        Map<Metric.MetricType, Long> metricsMap = new HashMap<>();
        for (Metric metric : metrics) {
            metricsMap.put(metric.getType(), metric.getValue());
        }
        return metricsMap;
    }

    @Asynchronous
    public void start(Long job) {
        jobs.put(job, 0L);
        for (int i = 0; i < 500; i++) {
            sleep();
            jobs.put(job, (long) i);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}