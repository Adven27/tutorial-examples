/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * <p>
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * https://github.com/javaee/tutorial-examples/LICENSE.txt
 */
package javaeetutorial.batch.messagesender;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javaeetutorial.batch.messagesender.items.Client;
import javaeetutorial.batch.messagesender.items.Message;

import javax.batch.api.chunk.ItemWriter;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Dependent
@Named("MessageWriter")
public class MessageWriter implements ItemWriter {

    @PersistenceContext
    EntityManager em;
    @Inject
    private JobContext jobCtx;

    public MessageWriter() {
    }

    @Override
    public void open(Serializable checkpoint) throws Exception {
        /* Clear all bills if this is not a restart of the job */
        if (checkpoint == null)
            em.clear();
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void writeItems(List<Object> clients) throws Exception {
        Message msg = findMessage();
        msg.setText(msg.getText() + prepareText(clients));
    }

    private String prepareText(List<Object> clients) {
        StringBuilder result = new StringBuilder();
        for (Object obj : clients) {
            result.append(((Client) obj).getName());
        }
        return result.toString();
    }

    private Message findMessage() {
        Properties jobParams = BatchRuntime.getJobOperator().getParameters(jobCtx.getExecutionId());
        String messageId = jobParams.getProperty("messageId");
        Message msg = em.find(Message.class, Long.valueOf(messageId));
        if (msg == null) {
            throw new IllegalStateException("Message should exist but not found by " + messageId);
        }
        return msg;
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return new ItemNumberCheckpoint();
    }
}