package gov.healthit.chpl.validation.listing.reviewer.edition2015;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.certificationCriteria.CertificationCriterion;
import gov.healthit.chpl.domain.CertifiedProductSearchDetails;
import gov.healthit.chpl.domain.ListingMeasure;
import gov.healthit.chpl.service.CertificationCriterionService;
import gov.healthit.chpl.util.ErrorMessageUtil;
import gov.healthit.chpl.util.ValidationUtils;
import gov.healthit.chpl.validation.listing.reviewer.Reviewer;

@Component("measureValidityReviewer")
public class MeasureValidityReviewer implements Reviewer {
    private static final String MEASUREMENT_TYPE_G1 = "G1";
    private static final String MEASUREMENT_TYPE_G2 = "G2";
    private static final String G1_CRITERIA_NUMBER = "170.315 (g)(1)";
    private static final String G2_CRITERIA_NUMBER = "170.315 (g)(2)";

    private CertificationCriterionService criteriaService;
    private ValidationUtils validationUtils;
    private ErrorMessageUtil msgUtil;

    @Autowired
    public MeasureValidityReviewer(CertificationCriterionService criteriaService,
            ValidationUtils validationUtils, ErrorMessageUtil msgUtil) {
        this.criteriaService = criteriaService;
        this.validationUtils = validationUtils;
        this.msgUtil = msgUtil;
    }

    @Override
    public void review(CertifiedProductSearchDetails listing) {
        // if they have attested to G1 or G2 criterion, require at least one measure of that type
        reviewG1RequiredMeasures(listing);
        reviewG2RequiredMeasures(listing);

        for (ListingMeasure measure : listing.getMeasures()) {
            if (measure != null && measure.getMeasure() != null) {
                reviewMeasureHasId(listing, measure);
                reviewMeasureHasAssociatedCriteria(listing, measure);
                reviewMeasureHasCuresAndOriginalAssociatedCriteria(listing, measure);
                reviewMeasureHasOnlyAllowedCriteria(listing, measure);
                if (measure.getMeasure().getRequiresCriteriaSelection() != null
                        && !measure.getMeasure().getRequiresCriteriaSelection()) {
                    reviewMeasureHasAllAllowedCriteria(listing, measure);
                }
            }
        }
    }

    private void reviewG1RequiredMeasures(CertifiedProductSearchDetails listing) {
        List<CertificationCriterion> attestedCriteria = listing.getCertificationResults().stream()
                .filter(certResult -> BooleanUtils.isTrue(certResult.getSuccess()))
                .map(certResult -> certResult.getCriterion())
                .collect(Collectors.toList());
        if (validationUtils.hasCert(G1_CRITERIA_NUMBER, attestedCriteria)) {
            // must have at least one measure of type G1
            long g1MeasureCount = listing.getMeasures().stream()
                    .filter(measure -> measure.getMeasureType() != null
                            && measure.getMeasureType().getName().equals(MEASUREMENT_TYPE_G1))
                    .count();
            if (g1MeasureCount == 0) {
                listing.addBusinessErrorMessage(msgUtil.getMessage("listing.missingG1Measures"));
            }
        }
    }

    private void reviewG2RequiredMeasures(CertifiedProductSearchDetails listing) {
        List<CertificationCriterion> attestedCriteria = listing.getCertificationResults().stream()
                .filter(certResult -> BooleanUtils.isTrue(certResult.getSuccess()))
                .map(certResult -> certResult.getCriterion())
                .collect(Collectors.toList());
        if (validationUtils.hasCert(G2_CRITERIA_NUMBER, attestedCriteria)) {
            // must have at least one measure of type G1
            long g1MeasureCount = listing.getMeasures().stream()
                    .filter(measure -> measure.getMeasureType() != null
                            && measure.getMeasureType().getName().equals(MEASUREMENT_TYPE_G2))
                    .count();
            if (g1MeasureCount == 0) {
                listing.addBusinessErrorMessage(msgUtil.getMessage("listing.missingG2Measures"));
            }
        }
    }

    private void reviewMeasureHasOnlyAllowedCriteria(CertifiedProductSearchDetails listing, ListingMeasure measure) {
        if (measure.getMeasure() == null || measure.getMeasure().getId() == null
                || measure.getAssociatedCriteria() == null
                || measure.getAssociatedCriteria().size() == 0
                || measure.getMeasure().getAllowedCriteria() == null
                || measure.getMeasure().getAllowedCriteria().size() == 0) {
            return;
        }

        Predicate<CertificationCriterion> notInAllowedCriteria = assocCriterion -> !measure.getMeasure().getAllowedCriteria().stream()
                .anyMatch(allowedCriterion -> allowedCriterion.getId().equals(assocCriterion.getId()));

        List<CertificationCriterion> assocCriteriaNotAllowed = measure.getAssociatedCriteria().stream()
                .filter(notInAllowedCriteria)
                .collect(Collectors.toList());

        assocCriteriaNotAllowed.stream().forEach(assocCriterionNotAllowed -> {
            listing.addBusinessErrorMessage(msgUtil.getMessage(
                    "listing.measure.associatedCriterionNotAllowed",
                    measure.getMeasureType().getName(),
                    measure.getMeasure().getName(),
                    measure.getMeasure().getAbbreviation(),
                    CertificationCriterionService.formatCriteriaNumber(assocCriterionNotAllowed)));
        });
    }

    private void reviewMeasureHasAllAllowedCriteria(CertifiedProductSearchDetails listing, ListingMeasure measure) {
        if (measure.getMeasure() == null || measure.getAssociatedCriteria() == null
                || measure.getMeasure().getAllowedCriteria() == null) {
            return;
        }

        doesMeasureHaveAllExpectedCriteria(listing, measure,
                measure.getAssociatedCriteria().stream().collect(Collectors.toList()),
                measure.getMeasure().getAllowedCriteria().stream().collect(Collectors.toList()));
    }

    private void reviewMeasureHasAssociatedCriteria(CertifiedProductSearchDetails listing, ListingMeasure measure) {
        if (measure.getAssociatedCriteria() == null || measure.getAssociatedCriteria().size() == 0) {
            listing.addBusinessErrorMessage(msgUtil.getMessage(
                    "listing.measure.missingAssociatedCriteria",
                    measure.getMeasureType().getName(),
                    measure.getMeasure().getName(),
                    measure.getMeasure().getAbbreviation()));
        }
    }

    private void reviewMeasureHasCuresAndOriginalAssociatedCriteria(CertifiedProductSearchDetails listing, ListingMeasure measure) {
        if (measure.getAssociatedCriteria() == null || measure.getAssociatedCriteria().size() == 0) {
            return;
        }

        measure.getAssociatedCriteria().stream()
                .map(associatedCriterion -> criteriaService.getByNumber(associatedCriterion.getNumber()))
                .filter(criteriaWithNumber -> criteriaWithNumber != null && criteriaWithNumber.size() > 1)
                .forEach(criteriaWithNumber -> doesMeasureHaveAllExpectedCriteria(listing, measure,
                        measure.getAssociatedCriteria().stream().collect(Collectors.toList()),
                        criteriaWithNumber));
    }

    private void doesMeasureHaveAllExpectedCriteria(CertifiedProductSearchDetails listing,
            ListingMeasure measure,
            List<CertificationCriterion> associatedCriteria,
            List<CertificationCriterion> expectedCriteria) {
        Predicate<CertificationCriterion> notInAssociatedCriteria = allowedCriterion -> !associatedCriteria.stream()
                .anyMatch(assocCriterion -> allowedCriterion.getId().equals(assocCriterion.getId()));

        List<CertificationCriterion> missingAllowedCriteria = expectedCriteria.stream()
                .filter(notInAssociatedCriteria)
                .collect(Collectors.toList());

        missingAllowedCriteria.stream().forEach(missingAllowedCriterion -> {
            listing.addBusinessErrorMessage(msgUtil.getMessage(
                    "listing.measure.missingRequiredCriterion",
                    measure.getMeasureType().getName(),
                    measure.getMeasure().getName(),
                    measure.getMeasure().getAbbreviation(),
                    CertificationCriterionService.formatCriteriaNumber(missingAllowedCriterion)));
        });
    }

    private void reviewMeasureHasId(CertifiedProductSearchDetails listing, ListingMeasure measure) {
        if (measure == null) {
            return;
        }

        if (measure.getMeasure().getId() == null) {
            String nameForMsg = "";
            if (measure.getMeasure().getAbbreviation() != null) {
                nameForMsg = measure.getMeasure().getAbbreviation();
            } else if (measure.getMeasure().getName() != null) {
                nameForMsg = measure.getMeasure().getName();
            } else if (measure.getMeasure().getRequiredTest() != null) {
                nameForMsg = measure.getMeasure().getRequiredTest();
            }
            listing.addDataErrorMessage(
                    msgUtil.getMessage("listing.invalidMeasure", nameForMsg));
        }

        if (measure.getMeasureType() == null || measure.getMeasureType().getId() == null) {
            String nameForMsg = measure.getMeasureType() == null ? "null"
                    : measure.getMeasureType().getName();
            listing.addDataErrorMessage(
                    msgUtil.getMessage("listing.invalidMeasureType", nameForMsg));
        }
    }
}
