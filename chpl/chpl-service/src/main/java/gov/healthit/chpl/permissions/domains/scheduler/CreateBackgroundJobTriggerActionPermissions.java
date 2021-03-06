package gov.healthit.chpl.permissions.domains.scheduler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import gov.healthit.chpl.domain.schedule.ChplOneTimeTrigger;
import gov.healthit.chpl.permissions.domains.ActionPermissions;
import gov.healthit.chpl.scheduler.job.ListingUploadValidationJob;
import gov.healthit.chpl.scheduler.job.RealWorldTestingUploadJob;
import gov.healthit.chpl.scheduler.job.SplitDeveloperJob;
import gov.healthit.chpl.scheduler.job.SurveillanceUploadJob;
import gov.healthit.chpl.scheduler.job.surveillanceReport.AnnualReportGenerationJob;
import gov.healthit.chpl.scheduler.job.surveillanceReport.QuarterlyReportGenerationJob;

@Component(value = "schedulerCreateBackgroundJobTriggerActionPermissions")
public class CreateBackgroundJobTriggerActionPermissions extends ActionPermissions {

    private static final List<String> BACKGROUND_JOBS_ACB_CAN_CREATE = new ArrayList<String>();

    @PostConstruct
    public void init() {
        BACKGROUND_JOBS_ACB_CAN_CREATE.add(SplitDeveloperJob.JOB_NAME);
        BACKGROUND_JOBS_ACB_CAN_CREATE.add(RealWorldTestingUploadJob.JOB_NAME);
        BACKGROUND_JOBS_ACB_CAN_CREATE.add(QuarterlyReportGenerationJob.JOB_NAME);
        BACKGROUND_JOBS_ACB_CAN_CREATE.add(AnnualReportGenerationJob.JOB_NAME);
        BACKGROUND_JOBS_ACB_CAN_CREATE.add(ListingUploadValidationJob.JOB_NAME);
        BACKGROUND_JOBS_ACB_CAN_CREATE.add(SurveillanceUploadJob.JOB_NAME);
    }

    @Override
    public boolean hasAccess() {
        return false;
    }

    @Override
    public boolean hasAccess(Object obj) {
        if (getResourcePermissions().isUserRoleAdmin()
                || getResourcePermissions().isUserRoleOnc()
                || getResourcePermissions().isUserRoleOncStaff()) {
            return true;
        } else if (getResourcePermissions().isUserRoleAcbAdmin()) {
            if (obj instanceof ChplOneTimeTrigger) {
                ChplOneTimeTrigger trigger = (ChplOneTimeTrigger) obj;
                if (trigger.getJob() != null && canAcbCreateJob(trigger.getJob().getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canAcbCreateJob(String jobName) {
        return BACKGROUND_JOBS_ACB_CAN_CREATE.stream()
                .filter(job -> job.equalsIgnoreCase(jobName))
                .findAny()
                .isPresent();
    }
}
