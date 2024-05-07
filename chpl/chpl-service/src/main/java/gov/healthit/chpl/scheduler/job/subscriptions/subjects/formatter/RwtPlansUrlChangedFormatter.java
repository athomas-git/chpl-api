package gov.healthit.chpl.scheduler.job.subscriptions.subjects.formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.activity.history.ListingActivityUtil;
import gov.healthit.chpl.dao.ActivityDAO;
import gov.healthit.chpl.domain.CertifiedProductSearchDetails;
import gov.healthit.chpl.dto.ActivityDTO;
import gov.healthit.chpl.subscription.domain.SubscriptionObservation;
import gov.healthit.chpl.util.DateUtil;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2(topic = "subscriptionObservationsNotificationJobLogger")
public class RwtPlansUrlChangedFormatter extends ObservationSubjectFormatter {
    private static final String DESCRIPTION_REMOVED = "RWT Plans URL %s was removed.";
    private static final String DESCRIPTION_ADDED_UNFORMATTED = "RWT Plans URL %s was added.";
    private static final String DESCRIPTION_UPDATED_UNFORMATTED = "RWT Plans URL was changed from %s to %s";

    @Autowired
    public RwtPlansUrlChangedFormatter(@Qualifier("activityDAO") ActivityDAO activityDao,
            ListingActivityUtil listingActivityUtil) {
        super(activityDao, listingActivityUtil);
    }

    @Override
    public List<List<String>> toListsOfStrings(SubscriptionObservation observation) {
        ActivityDTO activity = getActivity(observation.getActivityId());

        CertifiedProductSearchDetails before = getListing(activity.getOriginalData());
        CertifiedProductSearchDetails after = getListing(activity.getNewData());

        if (before == null || after == null) {
            LOGGER.error("There was a problem turning activityID " + activity.getId() + " into listing details objects.");
            return null;
        }

        String formattedObservation = null;
        if (!StringUtils.isEmpty(before.getRwtPlansUrl()) && StringUtils.isEmpty(after.getRwtPlansUrl())) {
            formattedObservation = String.format(DESCRIPTION_REMOVED, before.getRwtPlansUrl());
        } else if (StringUtils.isEmpty(before.getRwtPlansUrl()) && !StringUtils.isEmpty(after.getRwtPlansUrl())) {
            formattedObservation = String.format(DESCRIPTION_ADDED_UNFORMATTED, after.getRwtPlansUrl());
        } else {
            formattedObservation = String.format(DESCRIPTION_UPDATED_UNFORMATTED, before.getRwtPlansUrl(), after.getRwtPlansUrl());
        }

        List<List<String>> formattedObservations = new ArrayList<List<String>>();
        formattedObservations.add(Stream.of(observation.getSubscription().getSubject().getSubject(),
                formattedObservation,
                DateUtil.formatInEasternTime(activity.getActivityDate())).toList());
        return formattedObservations;
    }
}
