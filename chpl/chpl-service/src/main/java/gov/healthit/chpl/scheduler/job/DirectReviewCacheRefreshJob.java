package gov.healthit.chpl.scheduler.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import gov.healthit.chpl.exception.JiraRequestFailedException;
import gov.healthit.chpl.service.DirectReviewService;

@DisallowConcurrentExecution
public class DirectReviewCacheRefreshJob extends DownloadableResourceCreatorJob {
    private static final Logger LOGGER = LogManager.getLogger("directReviewCacheRefreshJobLogger");

    @Autowired
    private Environment env;

    @Autowired
    private DirectReviewService directReviewService;

    public DirectReviewCacheRefreshJob() throws Exception {
        super(LOGGER);
    }

    @Override
    public void execute(final JobExecutionContext jobContext) throws JobExecutionException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        LOGGER.info("********* Starting the Direct Review Cache Refresh job. *********");
        try {
            directReviewService.populateDirectReviewsCache();
        } catch (JiraRequestFailedException ex) {
            LOGGER.error("Request to Jira to populate all direct reviews failed.", ex);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            LOGGER.info("********* Completed the Direct Review Cache Refresh job. *********");
        }
    }
}