package gov.healthit.chpl.manager.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import gov.healthit.chpl.manager.ActivityMetadataManager;

@Service("activityMetadataManager")
public class ActivityMetadataManagerImpl extends SecuredManager implements ActivityMetadataManager {
    private static final Logger LOGGER = LogManager.getLogger(ActivityMetadataManagerImpl.class);

    private ActivityDAO activityDAO;
    private CertificationBodyDAO acbDao;
    private TestingLabDAO atlDao;
    private ActivityMetadataBuilderFactory metadataBuilderFactory;

    @Autowired
    public ActivityMetadataManagerImpl(final ActivityDAO activityDAO, final CertificationBodyDAO acbDao,
            final TestingLabDAO atlDao, final ActivityMetadataBuilderFactory metadataBuilderFactory) {
        this.activityDAO = activityDAO;
        this.acbDao = acbDao;
        this.atlDao = atlDao;
        this.metadataBuilderFactory = metadataBuilderFactory;
    }

    @Override
    @Transactional
    public List<ActivityMetadata> getActivityMetadataByConcept(final ActivityConcept concept, final Date startDate,
            final Date endDate) throws JsonParseException, IOException {

        LOGGER.info("Getting " + concept.name() + " activity from " + startDate + " through " + endDate);
        // get the activity
        List<ActivityDTO> activityDtos = activityDAO.findByConcept(concept, startDate, endDate);
        List<ActivityMetadata> activityMetas = new ArrayList<ActivityMetadata>();
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

    @Override
    @Transactional
    public List<ActivityMetadata> getActivityMetadataByObject(final Long objectId, final ActivityConcept concept,
            final Date startDate, final Date endDate) throws JsonParseException, IOException {

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

    @Override
    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_ACB_METADATA)")
    @PostFilter("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_ACB_METADATA, filterObject)")
    @Transactional
    public List<ActivityMetadata> getCertificationBodyActivityMetadata(final Date startDate, final Date endDate)
            throws JsonParseException, IOException {
        // there is very little ACB activity so just get it all for the date range
        // and apply a post filter to remove whatever the current user should not see.
        return getActivityMetadataByConcept(ActivityConcept.CERTIFICATION_BODY, startDate, endDate);
    }

    @Override
    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_METADATA_BY_ACB, #acbId)")
    @Transactional
    public List<ActivityMetadata> getCertificationBodyActivityMetadata(final Long acbId, final Date startDate,
            final Date endDate) throws EntityRetrievalException, JsonParseException, IOException {
        acbDao.getById(acbId); // throws not found exception for invalid id
        return getActivityMetadataByObject(acbId, ActivityConcept.CERTIFICATION_BODY, startDate, endDate);
    }

    @Override
    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_ATL_METADATA)")
    @PostFilter("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_ATL_METADATA, filterObject)")
    @Transactional
    public List<ActivityMetadata> getTestingLabActivityMetadata(final Date startDate, final Date endDate)
            throws JsonParseException, IOException {
        // there is very little ATL activity so just get it all for the date range
        // and apply a post filter to remove whatever the current user should not see.
        return getActivityMetadataByConcept(ActivityConcept.TESTING_LAB, startDate, endDate);
    }

    @Override
    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_METADATA_BY_ATL, #atlId)")
    @Transactional
    public List<ActivityMetadata> getTestingLabActivityMetadata(final Long atlId, final Date startDate,
            final Date endDate) throws EntityRetrievalException, JsonParseException, IOException {
        atlDao.getById(atlId); // throws not found exception for invalid id
        return getActivityMetadataByObject(atlId, ActivityConcept.TESTING_LAB, startDate, endDate);
    }

    @Override
    @Transactional
    @PreAuthorize("@permissions.hasAccess(T(gov.healthit.chpl.permissions.Permissions).ACTIVITY, "
            + "T(gov.healthit.chpl.permissions.domains.ActivityDomainPermissions).GET_USER_MAINTENANCE_METADATA)")
    public List<ActivityMetadata> getUserMaintenanceActivityMetadata(final Date startDate, final Date endDate)
            throws JsonParseException, IOException {
        return getActivityMetadataByConcept(ActivityConcept.USER, startDate, endDate);
    }
}
