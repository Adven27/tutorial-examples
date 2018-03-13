package javaeetutorial.batch.messagesender.beans;

import javaeetutorial.batch.messagesender.items.Message;

import javax.annotation.Resource;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

@Named
@ManagedBean(name="index")
@SessionScoped
@TransactionManagement(TransactionManagementType.CONTAINER)
public class JsfBean implements Serializable {

    private long execID;
    private JobOperator jobOperator;
    @PersistenceContext
    EntityManager em;
    private static final long serialVersionUID = 6775054787257816151L;

//    @Resource
//    private UserTransaction userTransaction;

    public String startBatchJob() throws Exception {
        Message message = Message.builder().title("Message!").datetime(new Date()).build();
//        userTransaction.begin();
        em.persist(message);
//        userTransaction.commit();

        Properties runtimeParameters = new Properties();
        runtimeParameters.setProperty("messageId", String.valueOf(message.getId()));

        jobOperator = BatchRuntime.getJobOperator();
        execID = jobOperator.start("messagesender", runtimeParameters);
        return "jobstarted";
    }

    /* Get the status of the job from the batch runtime */
    public String getJobStatus() {
        return jobOperator.getJobExecution(execID).getBatchStatus().toString();
    }

    public boolean isCompleted() {
        return (getJobStatus().compareTo("COMPLETED") == 0);
    }

    public List<List<String>> getRowList() throws FileNotFoundException, IOException {
        List<List<String>> rowList = new ArrayList<>();

        if (isCompleted()) {
            String query = "SELECT b FROM Message b";
            Query q = em.createQuery(query);

            for (Object billObject : q.getResultList()) {
                Message msg = (Message) billObject;
                System.out.println(msg);
                rowList.add(asList(msg.toString()));
            }
        }
        return rowList;
    }
}