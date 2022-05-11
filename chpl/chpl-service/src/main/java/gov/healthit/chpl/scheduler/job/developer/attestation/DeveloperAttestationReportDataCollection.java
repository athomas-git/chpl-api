package gov.healthit.chpl.scheduler.job.developer.attestation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jfree.data.time.DateRange;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.attestation.dao.AttestationDAO;
import gov.healthit.chpl.attestation.domain.AttestationPeriod;
import gov.healthit.chpl.attestation.domain.AttestationSubmittedResponse;
import gov.healthit.chpl.attestation.domain.DeveloperAttestationSubmission;
import gov.healthit.chpl.attestation.manager.AttestationPeriodService;
import gov.healthit.chpl.attestation.report.validation.AttestationValidationService;
import gov.healthit.chpl.changerequest.dao.DeveloperCertificationBodyMapDAO;
import gov.healthit.chpl.dao.CertificationBodyDAO;
import gov.healthit.chpl.dao.DeveloperDAO;
import gov.healthit.chpl.domain.CertificationBody;
import gov.healthit.chpl.domain.CertificationStatus;
import gov.healthit.chpl.domain.CertificationStatusEvent;
import gov.healthit.chpl.domain.Developer;
import gov.healthit.chpl.domain.compliance.DirectReviewNonConformity;
import gov.healthit.chpl.domain.concept.CertificationEditionConcept;
import gov.healthit.chpl.entity.CertificationStatusType;
import gov.healthit.chpl.exception.ValidationException;
import gov.healthit.chpl.search.ListingSearchService;
import gov.healthit.chpl.search.domain.CertifiedProductBasicSearchResult;
import gov.healthit.chpl.search.domain.SearchRequest;
import gov.healthit.chpl.search.domain.SearchResponse;
import gov.healthit.chpl.service.DirectReviewSearchService;
import lombok.extern.log4j.Log4j2;

@Log4j2(topic = "developerAttestationReportJobLogger")
@Component
public class DeveloperAttestationReportDataCollection {
    private static final Integer MAX_PAGE_SIZE = 100;

    private static final Long INFORMATION_BLOCKING_ATTESTATION_ID = 1L;
    private static final Long ASSURANCES_ATTESTATION_ID = 2L;
    private static final Long COMMUNICATIONS_ATTESTATION_ID = 3L;
    private static final Long API_ATTESTATION_ID = 4L;
    private static final Long RWT_ATTESTATION_ID = 5L;

    private static final String RWT_VALIDATION_TRUE = "Has listing(s) with RWT criteria";
    private static final String RWT_VALIDATION_FALSE = "No listings with RWT criteria";
    private static final String ASSURANCES_VALIDATION_TRUE = "Has listing(s) with Assurances criteria (b)(6) or (b)(10)";
    private static final String ASSURANCES_VALIDATION_FALSE = "No listings with Assurances criteria (b)(6) or (b)(10)";
    private static final String API_VALIDATION_TRUE = "Has listing(s) with API criteria (g)(7)-(g)(10)";
    private static final String API_VALIDATION_FALSE = "No listings with API criteria (g)(7)-(g)(10)";

    private DeveloperDAO developerDAO;
    private ListingSearchService listingSearchService;
    private AttestationDAO attestationDAO;
    private DirectReviewSearchService directReviewService;
    private AttestationValidationService attestationValidationService;
    private CertificationBodyDAO certificationBodyDAO;
    private DeveloperCertificationBodyMapDAO developerCertificationBodyMapDAO;
    private AttestationPeriodService attestationPeriodService;

    private Map<Long, List<CertifiedProductBasicSearchResult>> developerListings = new HashMap<Long, List<CertifiedProductBasicSearchResult>>();

    public DeveloperAttestationReportDataCollection(DeveloperDAO developerDAO, ListingSearchService listingSearchService, AttestationDAO attestationDAO,
            DirectReviewSearchService directReviewService, AttestationValidationService attestationValidationService, CertificationBodyDAO certificationBodyDAO,
            DeveloperCertificationBodyMapDAO developerCertificationBodyMapDAO, AttestationPeriodService attestationPeriodService) {
        this.developerDAO = developerDAO;
        this.listingSearchService = listingSearchService;
        this.attestationDAO = attestationDAO;
        this.directReviewService = directReviewService;
        this.attestationValidationService = attestationValidationService;
        this.certificationBodyDAO = certificationBodyDAO;
        this.developerCertificationBodyMapDAO = developerCertificationBodyMapDAO;
        this.attestationPeriodService = attestationPeriodService;
}

    private List<String> activeStatuses = Stream.of(CertificationStatusType.Active.getName(),
            CertificationStatusType.SuspendedByAcb.getName(),
            CertificationStatusType.SuspendedByOnc.getName())
            .collect(Collectors.toList());

    public List<DeveloperAttestationReport> collect(List<Long> selectedAcbIds) {
        AttestationPeriod mostRecentPastPeriod = attestationPeriodService.getMostRecentPastAttestationPeriod();
        LOGGER.info("Most recent past attestation period: {} - {} ", mostRecentPastPeriod.getPeriodStart().toString(), mostRecentPastPeriod.getPeriodEnd().toString());
        LOGGER.info("Selected AcbsId: {}", selectedAcbIds.stream()
                .map(id -> id.toString())
                .collect(Collectors.joining(", ")));

        List<Developer> developers = getAllDevelopers().stream()
                .filter(dev -> doesActiveListingExistDuringAttestationPeriod(getListingDataForDeveloper(dev), mostRecentPastPeriod))
                .toList();

        List<DeveloperAttestationReport> reportRows = developers.stream()
                .filter(dev -> isDeveloperManagedBySelectedAcbs(dev, selectedAcbIds))
                .map(dev -> {
                    DeveloperAttestationSubmission attestation = getDeveloperAttestation(dev.getId(), mostRecentPastPeriod.getId());

                    return DeveloperAttestationReport.builder()
                        .developerName(dev.getName())
                        .developerCode(dev.getDeveloperCode())
                        .developerId(dev.getId())
                        .pointOfContactName(getPointOfContactFullName(dev))
                        .pointOfContactEmail(getPointOfContactEmail(dev))
                        .attestationStatus(attestation != null ? "Published" : "")
                        .attestationPublishDate(attestation != null ? attestation.getDatePublished() : null)
                        .attestationPeriod(String.format("%s - %s", mostRecentPastPeriod.getPeriodStart().toString(),
                                mostRecentPastPeriod.getPeriodEnd().toString()))
                        .informationBlocking(getAttestationResponse(attestation, INFORMATION_BLOCKING_ATTESTATION_ID))
                        .assurances(getAttestationResponse(attestation, ASSURANCES_ATTESTATION_ID))
                        .communications(getAttestationResponse(attestation, COMMUNICATIONS_ATTESTATION_ID))
                        .applicationProgrammingInterfaces(getAttestationResponse(attestation, API_ATTESTATION_ID))
                        .realWorldTesting(getAttestationResponse(attestation, RWT_ATTESTATION_ID))
                        .submitterName(getSubmitterName(attestation))
                        .submitterEmail(getSubmitterEmail(attestation))
                        .totalSurveillanceNonconformities(getTotalSurveillanceNonconformities(dev))
                        .openSurveillanceNonconformities(getOpenSurveillanceNonconformities(dev))
                        .totalDirectReviewNonconformities(getTotalDirectReviewNonconformities(dev))
                        .openDirectReviewNonconformities(getOpenDirectReviewNonconformities(dev))
                        .assurancesValidation(getAssurancesValidation(dev))
                        .realWorldTestingValidation(getRealWorldTestingValidation(dev))
                        .apiValidation(getApiValidation(dev))
                        .activeAcbs(getActiveAcbs())
                        .developerAcbMap(getDeveloperAcbMapping(dev))
                        .build();
                })
                .sorted(Comparator.comparing(DeveloperAttestationReport::getDeveloperName))
                .toList();

        LOGGER.info("Total Report Rows found: {}", reportRows.size());

        return reportRows;
    }

    private Boolean doesActiveListingExistDuringAttestationPeriod(List<CertifiedProductBasicSearchResult> listingsForDeveloper, AttestationPeriod period) {
        return listingsForDeveloper.stream()
                .filter(listing -> isListingActiveDuringPeriod(listing, period))
                .findAny()
                .isPresent();
    }

    private Boolean isListingActiveDuringPeriod(CertifiedProductBasicSearchResult listing, AttestationPeriod period) {
        List<CertificationStatusEvent> statusEvents = listing.getStatusEvents().stream()
                .map(x ->  CertificationStatusEvent.builder()
                        .status(CertificationStatus.builder()
                                .name(x.split(":")[1])
                                .build())
                        .eventDate(toDate(LocalDate.parse(x.split(":")[0])).getTime())
                        .build())
                .sorted(Comparator.comparing(CertificationStatusEvent::getEventDate))
                .toList();

        return isListingActiveDuringAttestationPeriod(statusEvents, period);
    }

    private List<CertifiedProductBasicSearchResult> getListingDataForDeveloper(Developer developer) {
        if (!developerListings.containsKey(developer.getId())) {
            SearchRequest request = SearchRequest.builder()
                    .certificationEditions(Stream.of(CertificationEditionConcept.CERTIFICATION_EDITION_2015.getYear()).collect(Collectors.toSet()))
                    .developer(developer.getName())
                    .pageSize(MAX_PAGE_SIZE)
                    .build();

            try {
                SearchResponse response = listingSearchService.search(request);
                developerListings.put(developer.getId(), response.getResults());
            } catch (ValidationException e) {
                LOGGER.error("Could not retrieve listings for developer {}.", developer.getName());
                LOGGER.error(e);
                developerListings.put(developer.getId(), new ArrayList<CertifiedProductBasicSearchResult>());
            }
        }
        return developerListings.get(developer.getId());
    }

    private List<Developer> getAllDevelopers() {
        return developerDAO.findAll();
    }

    private DeveloperAttestationSubmission getDeveloperAttestation(Long developerId, Long attestationPeriodId) {
        List<DeveloperAttestationSubmission> attestations =
                attestationDAO.getDeveloperAttestationSubmissionsByDeveloperAndPeriod(developerId, attestationPeriodId);

        if (attestations != null && attestations.size() > 0) {
            return attestations.get(0);
        } else {
            return null;
        }
    }

    private boolean isListingActiveDuringAttestationPeriod(List<CertificationStatusEvent> statusEvents, AttestationPeriod period) {
        List<DateRange> activeDateRanges = getDateRangesWithActiveStatus(statusEvents);
        return activeDateRanges.stream()
            .filter(activeDates -> toDate(period.getPeriodStart()).getTime() <= activeDates.getUpperMillis()
                    && toDate(period.getPeriodEnd()).getTime() >= activeDates.getLowerMillis())
            .findAny().isPresent();
    }

    private List<DateRange> getDateRangesWithActiveStatus(List<CertificationStatusEvent> listingStatusEvents) {
        //Assumes statuses are sorted
        return IntStream.range(0, listingStatusEvents.size())
            .filter(i -> listingStatusEvents.get(i) != null && listingStatusEvents.get(i).getStatus() != null
                && !StringUtils.isEmpty(listingStatusEvents.get(i).getStatus().getName()))
            .filter(i -> activeStatuses.contains(listingStatusEvents.get(i).getStatus().getName()))
            .mapToObj(i -> new DateRange(new Date(listingStatusEvents.get(i).getEventDate()),
                    i < (listingStatusEvents.size() - 1) ? new Date(listingStatusEvents.get(i + 1).getEventDate())
                            //Math.max here to handle the case where status is a future date
                            : new Date(Math.max(System.currentTimeMillis(), listingStatusEvents.get(i).getEventDate()))))
            .collect(Collectors.toList());
    }

    private List<CertificationBody> getActiveAcbs() {
        return certificationBodyDAO.findAllActive().stream()
                .map(dto -> new CertificationBody(dto))
                .toList();
    }

    private CertificationBody getAcbByName(String acbName) {
        return new CertificationBody(certificationBodyDAO.getByName(acbName));
    }

    private Date toDate(LocalDate localDate) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        return  Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
    }

    private String getPointOfContactFullName(Developer developer) {
        return (developer != null && developer.getContact() != null && developer.getContact().getFullName() != null) ? developer.getContact().getFullName() : "";
    }

    private String getPointOfContactEmail(Developer developer) {
        return (developer != null && developer.getContact() != null && developer.getContact().getEmail() != null) ? developer.getContact().getEmail() : "";
    }

    private String getAttestationResponse(DeveloperAttestationSubmission attestation, Long attestationId) {
        if (attestation == null) {
            return "";
        } else {
             AttestationSubmittedResponse response = attestation.getResponses().stream()
                    .filter(resp -> resp.getAttestation().getId().equals(attestationId))
                    .findAny()
                    .orElse(null);
            return response != null
                ? String.format("%s : %s", response.getAttestation().getCondition().getName(), response.getResponse().getResponse())
                : "";
        }
    }

    private String getSubmitterName(DeveloperAttestationSubmission attestation) {
        return attestation != null ? attestation.getSignature() : "";
    }

    private String getSubmitterEmail(DeveloperAttestationSubmission attestation) {
        return attestation != null ? attestation.getSignatureEmail() : "";
    }

    private Long getTotalSurveillanceNonconformities(Developer developer) {
        return getListingDataForDeveloper(developer).stream()
                .filter(listing -> activeStatuses.contains(listing.getCertificationStatus()))
                .map(listing -> addOpenAndClosedNonconformityCount(listing))
                .collect(Collectors.summingLong(Long::longValue));
    }

    private Long addOpenAndClosedNonconformityCount(CertifiedProductBasicSearchResult listing) {
        Long closed = listing.getClosedSurveillanceNonConformityCount() != null ? listing.getClosedSurveillanceNonConformityCount() : 0L;
        Long open = listing.getOpenSurveillanceNonConformityCount() != null ? listing.getOpenSurveillanceNonConformityCount() : 0L;
        return closed + open;
    }

    private Long getOpenSurveillanceNonconformities(Developer developer) {
        return getListingDataForDeveloper(developer).stream()
                .filter(listing -> activeStatuses.contains(listing.getCertificationStatus()))
                .map(listing -> listing.getOpenSurveillanceNonConformityCount())
                .collect(Collectors.summingLong(Long::longValue));
    }

    private Long getTotalDirectReviewNonconformities(Developer developer) {
        return directReviewService.getDeveloperDirectReviews(developer.getId()).stream()
                .flatMap(dr -> dr.getNonConformities().stream())
                .count();
    }

    private Long getOpenDirectReviewNonconformities(Developer developer) {
        return directReviewService.getDeveloperDirectReviews(developer.getId()).stream()
                .flatMap(dr -> dr.getNonConformities().stream())
                .filter(nc -> nc.getNonConformityStatus().equalsIgnoreCase(DirectReviewNonConformity.STATUS_OPEN))
                .count();
    }

    private String getRealWorldTestingValidation(Developer developer) {
        if (attestationValidationService.validateRealWorldTesting(developer, getListingDataForDeveloper(developer))) {
            return RWT_VALIDATION_TRUE;
        } else {
            return RWT_VALIDATION_FALSE;
        }
    }

    private String getAssurancesValidation(Developer developer) {
        if (attestationValidationService.validateAssurances(developer, getListingDataForDeveloper(developer))) {
            return ASSURANCES_VALIDATION_TRUE;
        } else {
            return ASSURANCES_VALIDATION_FALSE;
        }
    }

    private String getApiValidation(Developer developer) {
        if (attestationValidationService.validateApi(developer, getListingDataForDeveloper(developer))) {
            return API_VALIDATION_TRUE;
        } else {
            return API_VALIDATION_FALSE;
        }
    }

    private Map<Pair<Long, Long>, Boolean> getDeveloperAcbMapping(Developer developer) {
        Map<Pair<Long, Long>, Boolean> developerAcbMap = new HashMap<Pair<Long, Long>, Boolean>();


        getListingDataForDeveloper(developer).stream()
                .filter(listing -> isListingActiveDuringPeriod(listing, attestationPeriodService.getMostRecentPastAttestationPeriod()))
                .forEach(listing -> developerAcbMap.put(Pair.of(developer.getId(), getAcbByName(listing.getAcb()).getId()), true));

        return developerAcbMap;
    }

    private Boolean isDeveloperManagedBySelectedAcbs(Developer developer, List<Long> acbIds) {
        return developerCertificationBodyMapDAO.getCertificationBodiesForDeveloper(developer.getId()).stream()
                .filter(acb -> acbIds.contains(acb.getId()))
                .findAny()
                .isPresent();
    }
}
