package gov.healthit.chpl.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;

import gov.healthit.chpl.activity.ActivityMetadataBuilder;
import gov.healthit.chpl.activity.ActivityMetadataBuilderFactory;
import gov.healthit.chpl.dao.ActivityDAO;
import gov.healthit.chpl.dao.CertificationBodyDAO;
import gov.healthit.chpl.dao.TestingLabDAO;
import gov.healthit.chpl.domain.activity.ActivityConcept;
import gov.healthit.chpl.domain.activity.ActivityMetadata;
import gov.healthit.chpl.dto.ActivityDTO;
import gov.healthit.chpl.exception.EntityRetrievalException;
import gov.healthit.chpl.manager.impl.SecuredManager;

@Service("activityMetadataManager")
public class ActivityMetadataManager extends SecuredManager {
    private static final Logger LOGGER = LogManager.getLogger(ActivityMetadataManager.class);

    private ActivityDAO activityDAO;
    private CertificationBodyDAO acbDao;
    private TestingLabDAO atlDao;
    private ActivityMetadataBuilderFactory metadataBuilderFactory;

    @Autowired
    public ActivityMetadataManager(@Qualifier("activityDAO") ActivityDAO activityDAO, CertificationBodyDAO acbDao,
            TestingLabDAO atlDao, ActivityMetadataBuilderFactory metadataBuilderFactory) {
        this.activityDAO = activityDAO;
        this.acbDao = acbDao;
        this.atlDao = atlDao;
        this.metadataBuilderFactory = metadataBuilderFactory;
    }

    @Transactional
    public List<ActivityMetadata> getActivityMetadataByObject(Long objectId, ActivityConcept concept,
            final Date startDate, final Date endDate) throws JsonParseException, IOException {

        return getActivityMetadataByObjectWithoutSecurity(objectId, concept, startDate, endDate);
    }

    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_METADATA_BY_ACB, #acbId)")
    @Transactional
    public List<ActivityMetadata> getCertificationBodyActivityMetadata(Long acbId, Date startDate,
            final Date endDate) throws EntityRetrievalException, JsonParseException, IOException {
        acbDao.getById(acbId); // throws not found exception for invalid id
        return getActivityMetadataByObjectWithoutSecurity(acbId, ActivityConcept.CERTIFICATION_BODY, startDate,
                endDate);
    }

    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_METADATA_BY_ATL, #atlId)")
    @Transactional
    public List<ActivityMetadata> getTestingLabActivityMetadata(Long atlId, Date startDate,
            final Date endDate) throws EntityRetrievalException, JsonParseException, IOException {
        atlDao.getById(atlId); // throws not found exception for invalid id
        return getActivityMetadataByObjectWithoutSecurity(atlId, ActivityConcept.TESTING_LAB, startDate, endDate);
    }

    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_COMPLAINT_METADATA)")
    @PostFilter("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_COMPLAINT_METADATA, filterObject)")
    @Transactional
    public List<ActivityMetadata> getComplaintActivityMetadata(Date startDate, Date endDate)
            throws IOException {
        return getActivityMetadataByConceptWithoutSecurity(ActivityConcept.COMPLAINT, startDate, endDate);
    }

    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_QUARTERLY_REPORT_METADATA)")
    @PostFilter("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_QUARTERLY_REPORT_METADATA, filterObject)")
    @Transactional
    public List<ActivityMetadata> getQuarterlyReportActivityMetadata(Date startDate, Date endDate)
            throws IOException {
        return getActivityMetadataByConceptWithoutSecurity(ActivityConcept.QUARTERLY_REPORT, startDate, endDate);
    }

    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_QUARTERLY_REPORT_METADATA)")
    @PostFilter("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_QUARTERLY_REPORT_METADATA, filterObject)")
    @Transactional
    public List<ActivityMetadata> getQuarterlyReportListingActivityMetadata(Date startDate, Date endDate)
            throws IOException {
        return getActivityMetadataByConceptWithoutSecurity(ActivityConcept.QUARTERLY_REPORT_LISTING, startDate,
                endDate);
    }

    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_ANNUAL_REPORT_METADATA)")
    @PostFilter("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_ANNUAL_REPORT_METADATA, filterObject)")
    @Transactional
    public List<ActivityMetadata> getAnnualReportActivityMetadata(Date startDate, Date endDate)
            throws IOException {
        return getActivityMetadataByConceptWithoutSecurity(ActivityConcept.ANNUAL_REPORT, startDate, endDate);
    }

    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_CHANGE_REQUEST_METADATA)")
    @PostFilter("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_CHANGE_REQUEST_METADATA, filterObject)")
    @Transactional
    public List<ActivityMetadata> getChangeRequestActivityMetadata(Date startDate, Date endDate)
            throws IOException {
        return getActivityMetadataByConceptWithoutSecurity(ActivityConcept.CHANGE_REQUEST, startDate, endDate);
    }

    private List<ActivityMetadata> getActivityMetadataByConceptWithoutSecurity(ActivityConcept concept,
            Date startDate, Date endDate) throws JsonParseException, IOException {
        LOGGER.info("Getting " + concept.name() + " activity from " + startDate + " through " + endDate);
        // get the activity
        List<ActivityDTO> activityDtos = activityDAO.findByConcept(concept, startDate, endDate);
        List<ActivityMetadata> activityMetas = new ArrayList<>();
        ActivityMetadataBuilder builder = null;
        if (activityDtos != null && activityDtos.size() > 0) {
            LOGGER.info("Found " + activityDtos.size() + " activity events");
            // excpect all dtos to have the same
            // since we've searched based on activity concept
            builder = metadataBuilderFactory.getBuilder(activityDtos.get(0));
            // convert to domain object
            for (ActivityDTO dto : activityDtos) {
                ActivityMetadata activityMeta = builder.build(dto);
                activityMetas.add(activityMeta);
            }
        } else {
            LOGGER.info("Found no activity events");
        }
        return activityMetas;
    }

    private List<ActivityMetadata> getActivityMetadataByObjectWithoutSecurity(Long objectId,
            ActivityConcept concept, Date startDate, Date endDate)
            throws JsonParseException, IOException {

        LOGGER.info("Getting " + concept.name() + " activity for id " + objectId + " from " + startDate + " through "
                + endDate);
        // get the activity
        List<ActivityDTO> activityDtos = activityDAO.findByObjectId(objectId, concept, startDate, endDate);
        List<ActivityMetadata> activityMetas = new ArrayList<ActivityMetadata>();
        ActivityMetadataBuilder builder = null;
        if (activityDtos != null && activityDtos.size() > 0) {
            // excpect all dtos to have the same
            // since we've searched based on activity concept
            builder = metadataBuilderFactory.getBuilder(activityDtos.get(0));
            // convert to domain object
            for (ActivityDTO dto : activityDtos) {
                ActivityMetadata activityMeta = builder.build(dto);
                activityMetas.add(activityMeta);
            }
        }
        return activityMetas;
    }

}
