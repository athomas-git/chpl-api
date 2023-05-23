package gov.healthit.chpl.subscription;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.dao.DeveloperDAO;
import gov.healthit.chpl.dao.ProductDAO;
import gov.healthit.chpl.exception.ValidationException;
import gov.healthit.chpl.search.ListingSearchService;
import gov.healthit.chpl.subscription.dao.SubscriberDao;
import gov.healthit.chpl.subscription.dao.SubscriptionDao;
import gov.healthit.chpl.subscription.domain.Subscriber;
import gov.healthit.chpl.subscription.domain.SubscriptionObjectType;
import gov.healthit.chpl.subscription.domain.SubscriptionReason;
import gov.healthit.chpl.subscription.domain.SubscriptionRequest;
import gov.healthit.chpl.subscription.service.SubscriberMessagingService;
import gov.healthit.chpl.subscription.service.SubscriptionLookupUtil;
import gov.healthit.chpl.subscription.validation.SubscriptionRequestValidationContext;
import gov.healthit.chpl.subscription.validation.SubscriptionRequestValidationService;
import gov.healthit.chpl.util.ErrorMessageUtil;

@Component
public class SubscriptionManager {
    private SubscriberDao subscriberDao;
    private SubscriptionDao subscriptionDao;
    private SubscriberMessagingService subscriberMessagingService;
    private DeveloperDAO developerDao;
    private ProductDAO productDao;
    private ListingSearchService listingSearchService;
    private ErrorMessageUtil errorMessageUtil;
    private SubscriptionLookupUtil lookupUtil;
    private SubscriptionRequestValidationService subscriptionValidationService;

    @Autowired
    public SubscriptionManager(SubscriberDao subscriberDao,
            SubscriptionDao subscriptionDao,
            SubscriberMessagingService subscriberMessagingService,
            DeveloperDAO developerDao,
            ProductDAO productDao,
            ListingSearchService listingSearchService,
            ErrorMessageUtil errorMessageUtil,
            SubscriptionLookupUtil lookupUtil) {
        this.subscriberDao = subscriberDao;
        this.subscriptionDao = subscriptionDao;
        this.subscriberMessagingService = subscriberMessagingService;
        this.developerDao = developerDao;
        this.productDao = productDao;
        this.listingSearchService = listingSearchService;
        this.errorMessageUtil = errorMessageUtil;
        this.lookupUtil = lookupUtil;
        this.subscriptionValidationService = new SubscriptionRequestValidationService();
    }

    @Transactional
    public List<SubscriptionReason> getAllReasons() {
        return subscriptionDao.getAllReasons();
    }

    @Transactional
    public List<SubscriptionObjectType> getAllSubscriptionObjectTypes() {
        return subscriptionDao.getAllSubscriptionObjectTypes();
    }

    @Transactional
    public void subscribe(SubscriptionRequest subscriptionRequest) throws ValidationException {
        SubscriptionRequestValidationContext validationContext = getNewValidationContext(subscriptionRequest);
        ValidationException validationException = new ValidationException(subscriptionValidationService.validate(validationContext));
        if (validationException.getErrorMessages().size() > 0) {
            throw validationException;
        }

        Subscriber subscriber = subscriberDao.getSubscriberByEmail(subscriptionRequest.getEmail());
        if (subscriber == null) {
            UUID newSubscriberId = subscriberDao.createSubscriber(subscriptionRequest.getEmail());
            subscriber = subscriberDao.getSubscriberById(newSubscriberId);
        }

        if (subscriber.getStatus().getId().equals(lookupUtil.getPendingSubscriberStatusId())) {
            subscriberMessagingService.sendConfirmation(subscriber);
        }

        subscriptionDao.createSubscription(subscriber.getId(), subscriptionRequest.getSubscribedObjectTypeId(),
                subscriptionRequest.getSubscribedObjectId(), subscriptionRequest.getReasonId());
    }

    @Transactional
    public void confirm(UUID subscriberUuid) {
        subscriberDao.confirmSubscriber(subscriberUuid);
    }

    private SubscriptionRequestValidationContext getNewValidationContext(SubscriptionRequest subscriptionRequest) {
        return new SubscriptionRequestValidationContext(
                subscriptionRequest,
                subscriptionDao,
                developerDao,
                productDao,
                listingSearchService,
                errorMessageUtil,
                lookupUtil);
    }
}
