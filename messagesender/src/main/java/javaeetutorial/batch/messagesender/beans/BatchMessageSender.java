package javaeetutorial.batch.messagesender.beans;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import java.util.concurrent.ConcurrentHashMap;

import static javax.batch.runtime.Metric.MetricType.WRITE_COUNT;

@Singleton
@LocalBean
public class BatchMessageSender {
    private ConcurrentHashMap<Long, Long> messageInProgress = new ConcurrentHashMap<>();

    @EJB
    BatchMetrics batchMetrics;


    public String sendingStatus(Long messageId) {
        Long job = messageInProgress.get(messageId);
        if (job != null) {
            if (!batchMetrics.completed(job)) {
                return String.valueOf(batchMetrics.metric(job, "send", WRITE_COUNT));
            }
            messageInProgress.remove(messageId);
        }
        return "";
    }

    public void registerMessage(Long msg, Long job) {
        messageInProgress.put(msg, job);
        batchMetrics.start(job);
    }
}