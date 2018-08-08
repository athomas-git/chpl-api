package gov.healthit.chpl.validation.listing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import gov.healthit.chpl.domain.CertifiedProductSearchDetails;
import gov.healthit.chpl.dto.PendingCertifiedProductDTO;
import gov.healthit.chpl.util.ChplProductNumberUtil;
import gov.healthit.chpl.util.ErrorMessageUtil;

@Service
public class ListingValidatorFactoryImpl implements ListingValidatorFactory {

    private static final Logger LOGGER = LogManager.getLogger(ListingValidatorFactoryImpl.class);

    private static final String PRACTICE_TYPE_AMBULATORY = "AMBULATORY";
    private static final String PRACTICE_TYPE_INPATIENT = "INPATIENT";
    private static final String PRODUCT_CLASSIFICATION_MODULAR = "Modular EHR";
    private static final String PRODUCT_CLASSIFICATION_COMPLETE = "Complete EHR";

    @Autowired
    private AllowedListingValidator allowedValidator;
    
    //pending listing validators (for upload)
    @Autowired
    private AmbulatoryModular2014PendingListingValidator ambulatoryModularPendingValidator;
    @Autowired
    private AmbulatoryComplete2014PendingListingValidator ambulatoryCompletePendingValidator;
    @Autowired
    private InpatientModular2014PendingListingValidator inpatientModularPendingValidator;
    @Autowired
    private InpatientComplete2014PendingListingValidator inpatientCompletePendingValidator;
    @Autowired
    private Edition2015PendingListingValidator pending2015Validator;
    
    //legacy validators (for update of CHP- listings)
    @Autowired
    private AmbulatoryModular2014LegacyListingValidator ambulatoryModularLegacyValidator;
    @Autowired
    private AmbulatoryComplete2014LegacyListingValidator ambulatoryCompleteLegacyValidator;
    @Autowired
    private InpatientModular2014LegacyListingValidator inpatientModularLegacyValidator;
    @Autowired
    private InpatientComplete2014LegacyListingValidator inpatientCompleteLegacyValidator;
    
    //listing validators (for update of listings with new-style IDs)
    @Autowired
    private AmbulatoryModular2014ListingValidator ambulatoryModularValidator;
    @Autowired
    private AmbulatoryComplete2014ListingValidator ambulatoryCompleteValidator;
    @Autowired
    private InpatientModular2014ListingValidator inpatientModularValidator;
    @Autowired
    private InpatientComplete2014ListingValidator inpatientCompleteValidator;
    @Autowired
    private Edition2015ListingValidator edition2015Validator;

    @Autowired
    private ErrorMessageUtil msgUtil;
    @Autowired
    private ChplProductNumberUtil chplProductNumberUtil;
    
    @Override
    public PendingValidator getValidator(PendingCertifiedProductDTO listing) {
        if (listing.getCertificationEdition().equals("2014")) {
            if (listing.getPracticeType().equalsIgnoreCase(PRACTICE_TYPE_AMBULATORY)) {
                if (listing.getProductClassificationName().equalsIgnoreCase(PRODUCT_CLASSIFICATION_MODULAR)) {
                    return ambulatoryModularPendingValidator;
                } else if (listing.getProductClassificationName().equalsIgnoreCase(PRODUCT_CLASSIFICATION_COMPLETE)) {
                    return ambulatoryCompletePendingValidator;
                } else {
                    String errMsg = msgUtil.getMessage("listing.validator.2014AmbulatoryClassificationNotFound");
                    listing.getErrorMessages().add(errMsg);
                    LOGGER.error(errMsg);
                }
            } else if (listing.getPracticeType().equalsIgnoreCase(PRACTICE_TYPE_INPATIENT)) {
                if (listing.getProductClassificationName().equalsIgnoreCase(PRODUCT_CLASSIFICATION_MODULAR)) {
                    return inpatientModularPendingValidator;
                } else if (listing.getProductClassificationName().equalsIgnoreCase(PRODUCT_CLASSIFICATION_COMPLETE)) {
                    return inpatientCompletePendingValidator;
                } else {
                    String errMsg = msgUtil.getMessage("listing.validator.2014InpatientClassificationNotFound");
                    listing.getErrorMessages().add(errMsg);
                    LOGGER.error(errMsg);
                }
            } else {
                String errMsg = msgUtil.getMessage("listing.validator.2014PracticeTypeNotFound");
                listing.getErrorMessages().add(errMsg);
                LOGGER.error(errMsg);
            }
        } else if (listing.getCertificationEdition().equals("2015")) {
            return pending2015Validator;
        } else {
            String errMsg = msgUtil.getMessage("listing.validator.certificationEditionNotFound", listing.getCertificationEdition());;
            listing.getErrorMessages().add(errMsg);
            LOGGER.error(errMsg);
        }
        return null;
    }

    @Override
    public Validator getValidator(CertifiedProductSearchDetails listing) {
        String edition = listing.getCertificationEdition().get("name").toString();
        if (StringUtils.isEmpty(listing.getChplProductNumber()) || 
                StringUtils.isEmpty(edition)) {
            String errMsg = msgUtil.getMessage("listing.validator.editionOrChplNumberNotFound", listing.getId().toString());
            listing.getErrorMessages().add(errMsg);
            LOGGER.error(errMsg);
            return null;
        }
        
        if(chplProductNumberUtil.isLegacy(listing.getChplProductNumber())) {
            //legacy must be a 2011 or 2014 listing
            if (edition.equals("2011")) {
                return allowedValidator;
            } else if(edition.equals("2014")) {
                String practiceTypeName = listing.getPracticeType().get("name").toString();
                String productClassificationName = listing.getClassificationType().get("name").toString();

                if (StringUtils.isEmpty(practiceTypeName) || StringUtils.isEmpty(productClassificationName)) {
                    String errMsg = msgUtil.getMessage("listing.validator.2014PracticeTypeOrClassificationNotFound");
                    listing.getErrorMessages().add(errMsg);
                    LOGGER.error(errMsg);
                    return null;
                }
                
                if (practiceTypeName.equalsIgnoreCase(PRACTICE_TYPE_AMBULATORY)) {
                    if (productClassificationName.equalsIgnoreCase(PRODUCT_CLASSIFICATION_MODULAR)) {
                        return ambulatoryModularLegacyValidator;
                    } else if (productClassificationName.equalsIgnoreCase(PRODUCT_CLASSIFICATION_COMPLETE)) {
                        return ambulatoryCompleteLegacyValidator;
                    } else {
                        String errMsg = msgUtil.getMessage("listing.validator.2014AmbulatoryClassificationNotFound");
                        listing.getErrorMessages().add(errMsg);
                        LOGGER.error(errMsg);
                    }
                } else if (practiceTypeName.equalsIgnoreCase(PRACTICE_TYPE_INPATIENT)) {
                    if (productClassificationName.equalsIgnoreCase(PRODUCT_CLASSIFICATION_MODULAR)) {
                        return inpatientModularLegacyValidator;
                    } else if (productClassificationName.equalsIgnoreCase(PRODUCT_CLASSIFICATION_COMPLETE)) {
                        return inpatientCompleteLegacyValidator;
                    } else {
                        String errMsg = msgUtil.getMessage("listing.validator.2014InpatientClassificationNotFound");
                        listing.getErrorMessages().add(errMsg);
                        LOGGER.error(errMsg);
                    }
                } else {
                    String errMsg = msgUtil.getMessage("listing.validator.2014PracticeTypeNotFound");
                    listing.getErrorMessages().add(errMsg);
                    LOGGER.error(errMsg);
                }
            } else {
                String errMsg = msgUtil.getMessage("listing.validator.certificationEditionNotFound", edition);
                listing.getErrorMessages().add(errMsg);
                LOGGER.error(errMsg);
            }
        } else {
            //new-style ID could be 2014 or 2015 listing
            if (edition.equals("2015")) {
                return edition2015Validator;
            } else if(edition.equals("2014")) {
                String practiceTypeName = listing.getPracticeType().get("name").toString();
                String productClassificationName = listing.getClassificationType().get("name").toString();

                if (StringUtils.isEmpty(practiceTypeName) || StringUtils.isEmpty(productClassificationName)) {
                    String errMsg = msgUtil.getMessage("listing.validator.2014PracticeTypeOrClassificationNotFound");
                    listing.getErrorMessages().add(errMsg);
                    LOGGER.error(errMsg);
                    return null;
                }
                
                if (practiceTypeName.equalsIgnoreCase(PRACTICE_TYPE_AMBULATORY)) {
                    if (productClassificationName.equalsIgnoreCase(PRODUCT_CLASSIFICATION_MODULAR)) {
                        return ambulatoryModularValidator;
                    } else if (productClassificationName.equalsIgnoreCase(PRODUCT_CLASSIFICATION_COMPLETE)) {
                        return ambulatoryCompleteValidator;
                    } else {
                        String errMsg = msgUtil.getMessage("listing.validator.2014AmbulatoryClassificationNotFound");
                        listing.getErrorMessages().add(errMsg);
                        LOGGER.error(errMsg);
                    }
                } else if (practiceTypeName.equalsIgnoreCase(PRACTICE_TYPE_INPATIENT)) {
                    if (productClassificationName.equalsIgnoreCase(PRODUCT_CLASSIFICATION_MODULAR)) {
                        return inpatientModularValidator;
                    } else if (productClassificationName.equalsIgnoreCase(PRODUCT_CLASSIFICATION_COMPLETE)) {
                        return inpatientCompleteValidator;
                    } else {
                        String errMsg = msgUtil.getMessage("listing.validator.2014InpatientClassificationNotFound");
                        listing.getErrorMessages().add(errMsg);
                        LOGGER.error(errMsg);
                    }
                } else {
                    String errMsg = msgUtil.getMessage("listing.validator.2014PracticeTypeNotFound");
                    listing.getErrorMessages().add(errMsg);
                    LOGGER.error(errMsg);
                }
            } else {
                String errMsg = msgUtil.getMessage("listing.validator.certificationEditionNotFound", edition);
                listing.getErrorMessages().add(errMsg);
                LOGGER.error(errMsg);
            }
        }
        return null;
    }
}
