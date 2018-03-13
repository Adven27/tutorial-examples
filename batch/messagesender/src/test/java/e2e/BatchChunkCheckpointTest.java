package e2e;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import java.io.File;
import java.util.Map;
import java.util.Properties;

import static e2e.BatchTestHelper.getMetricsMap;
import static javax.batch.runtime.BatchRuntime.getJobOperator;
import static javax.batch.runtime.BatchStatus.COMPLETED;
import static javax.batch.runtime.Metric.MetricType.*;
import static org.jboss.arquillian.persistence.CleanupStrategy.USED_ROWS_ONLY;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class BatchChunkCheckpointTest {

    @Deployment
    public static WebArchive createDeployment() {
        PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("pom.xml");
        File[] dependencies = resolver.importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile();

        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addClass(BatchTestHelper.class)
                .addPackages(true, "javaeetutorial.batch.messagesender")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(dependencies)
                .addAsResource("META-INF/batch-jobs/messagesender.xml")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("application.properties")
                .addAsResource("baseContext.xml")
                .addAsResource("batch-my.properties")
                .addAsResource("schema-h2.sql");

        System.out.println("\nBatchChunkCheckpointTest test war content: \n" + war.toString(true) + "\n");

        return war;
    }

    @Test
    @Cleanup(strategy = USED_ROWS_ONLY)
    @UsingDataSet({"datasets/messages.yml", "datasets/clients.yml"})
    @ShouldMatchDataSet("datasets/expected-messages.yml")
    public void testBatchChunkCheckpoint() throws Exception {
        Properties params = new Properties();
        params.setProperty("messageId", String.valueOf(111));

        JobOperator jobOperator = getJobOperator();
        Long executionId = jobOperator.start("messagesender", params);
        JobExecution jobExecution = jobOperator.getJobExecution(executionId);

        jobExecution = BatchTestHelper.keepTestAlive(jobExecution);

        for (StepExecution step : jobOperator.getStepExecutions(executionId)) {
            if (step.getStepName().equals("bills")) {
                Map<Metric.MetricType, Long> metrics = getMetricsMap(step.getMetrics());
                assertEquals(5, metrics.get(READ_COUNT).longValue());
                assertEquals(5, metrics.get(WRITE_COUNT).longValue());
                assertEquals(2, metrics.get(COMMIT_COUNT).longValue());
            }
        }
        assertEquals(jobExecution.getBatchStatus(), COMPLETED);
    }
}