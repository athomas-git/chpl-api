package gov.healthit.chpl.entity.listing.pending;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import gov.healthit.chpl.entity.AddressEntity;
import gov.healthit.chpl.entity.AttestationType;
import gov.healthit.chpl.util.Util;

/**
 * Object mapping for hibernate-handled table: certified_product. A product that
 * has been Certified
 *
 * @author autogenerated / cwatson
 */

@Entity
@Table(name = "pending_certified_product")
public class PendingCertifiedProductEntity {

    @Transient
    private List<String> errorMessages = new ArrayList<String>();

    /**
     * fields we generate mostly from spreadsheet values
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "pending_certified_product_id", nullable = false)
    private Long id;

    @Column(name = "practice_type_id")
    private Long practiceTypeId;

    @Column(name = "vendor_id")
    private Long developerId;

    @Column(name = "vendor_contact_id")
    private Long developerContactId;

    @Basic(optional = true)
    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_address_id", unique = true, nullable = true)
    private AddressEntity developerAddress;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_version_id")
    private Long productVersionId;

    @Column(name = "certification_edition_id")
    private Long certificationEditionId;

    @Column(name = "certification_body_id")
    private Long certificationBodyId;

    @Column(name = "product_classification_id")
    private Long productClassificationId;

    @Basic(optional = false)
    @Column(name = "certification_status_id", nullable = false)
    private Long status;

    /**
     * fields directly from the spreadsheet
     **/
    @Column(name = "unique_id")
    private String uniqueId;

    @Column(name = "record_status")
    private String recordStatus;

    @Column(name = "practice_type")
    private String practiceType;

    @Column(name = "vendor_name")
    private String developerName;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_version")
    private String productVersion;

    @Column(name = "certification_edition")
    private String certificationEdition;

    @Column(name = "acb_certification_id")
    private String acbCertificationId;

    @Column(name = "certification_body_name")
    private String certificationBodyName;

    @Column(name = "product_classification_name")
    private String productClassificationName;

    @Column(name = "certification_date")
    private Date certificationDate;

    @Column(name = "vendor_street_address")
    private String developerStreetAddress;

    @Column(name = "vendor_transparency_attestation")
    @Type(type = "gov.healthit.chpl.entity.PostgresAttestationType", parameters = {
            @org.hibernate.annotations.Parameter(name = "enumClassName",
                    value = "gov.healthit.chpl.entity.AttestationType")
    })
    private AttestationType transparencyAttestation;

    @Column(name = "vendor_transparency_attestation_url")
    private String transparencyAttestationUrl;

    @Column(name = "vendor_city")
    private String developerCity;

    @Column(name = "vendor_state")
    private String developerState;

    @Column(name = "vendor_zip_code")
    private String developerZipCode;

    @Column(name = "vendor_website")
    private String developerWebsite;

    @Column(name = "vendor_email")
    private String developerEmail;

    @Column(name = "vendor_contact_name")
    private String developerContactName;

    @Column(name = "vendor_phone")
    private String developerPhoneNumber;

    @Column(name = "test_report_url")
    private String reportFileLocation;

    @Column(name = "sed_report_file_location")
    private String sedReportFileLocation;

    @Column(name = "sed_intended_user_description")
    private String sedIntendedUserDescription;

    @Column(name = "sed_testing_end")
    private Date sedTestingEnd;

    @Column(name = "ics")
    private Boolean ics;

    @Column(name = "accessibility_certified")
    private Boolean accessibilityCertified;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pendingCertifiedProductId")
    @Basic(optional = false)
    @Column(name = "pending_certified_product_id", nullable = false)
    private Set<PendingCertificationResultEntity> certificationCriterion;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pendingCertifiedProductId")
    @Basic(optional = false)
    @Column(name = "pending_certified_product_id", nullable = false)
    private Set<PendingCqmCriterionEntity> cqmCriterion;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pendingCertifiedProductId")
    @Basic(optional = false)
    @Column(name = "pending_certified_product_id", nullable = false)
    private Set<PendingCertifiedProductQmsStandardEntity> qmsStandards;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pendingCertifiedProductId")
    @Basic(optional = false)
    @Column(name = "pending_certified_product_id", nullable = false)
    private Set<PendingCertifiedProductTestingLabMapEntity> testingLabs;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pendingCertifiedProductId")
    @Basic(optional = false)
    @Column(name = "pending_certified_product_id", nullable = false)
    private Set<PendingCertifiedProductTargetedUserEntity> targetedUsers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pendingCertifiedProductId")
    @Basic(optional = false)
    @Column(name = "pending_certified_product_id", nullable = false)
    private Set<PendingCertifiedProductAccessibilityStandardEntity> accessibilityStandards;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pendingCertifiedProductId")
    @Basic(optional = false)
    @Column(name = "pending_certified_product_id", nullable = false)
    private Set<PendingCertifiedProductParentListingEntity> parentListings;

    @Basic(optional = false)
    @Column(name = "has_qms", nullable = false)
    private Boolean hasQms;

    @Basic(optional = false)
    @Column(name = "last_modified_date", nullable = false)
    private Date lastModifiedDate;

    @Basic(optional = false)
    @Column(name = "last_modified_user", nullable = false)
    private Long lastModifiedUser;

    @Basic(optional = false)
    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @Basic(optional = false)
    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    /**
     * Default constructor.
     */
    public PendingCertifiedProductEntity() {
        testingLabs = new HashSet<PendingCertifiedProductTestingLabMapEntity>();
        certificationCriterion = new HashSet<PendingCertificationResultEntity>();
        cqmCriterion = new HashSet<PendingCqmCriterionEntity>();
        qmsStandards = new HashSet<PendingCertifiedProductQmsStandardEntity>();
        targetedUsers = new HashSet<PendingCertifiedProductTargetedUserEntity>();
        accessibilityStandards = new HashSet<PendingCertifiedProductAccessibilityStandardEntity>();
        parentListings = new HashSet<PendingCertifiedProductParentListingEntity>();
    }

    /**
     * Constructor with id.
     * @param id the id
     */
    public PendingCertifiedProductEntity(final Long id) {
        this();
        this.id = id;
    }

    @Transient
    public Class<?> getClassType() {
        return PendingCertifiedProductEntity.class;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getPracticeTypeId() {
        return practiceTypeId;
    }

    public void setPracticeTypeId(final Long practiceTypeId) {
        this.practiceTypeId = practiceTypeId;
    }

    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(final Long developerId) {
        this.developerId = developerId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(final Long productId) {
        this.productId = productId;
    }

    public Long getProductVersionId() {
        return productVersionId;
    }

    public void setProductVersionId(final Long productVersionId) {
        this.productVersionId = productVersionId;
    }

    public Long getCertificationEditionId() {
        return certificationEditionId;
    }

    public void setCertificationEditionId(final Long certificationEditionId) {
        this.certificationEditionId = certificationEditionId;
    }

    public Long getCertificationBodyId() {
        return certificationBodyId;
    }

    public void setCertificationBodyId(final Long certificationBodyId) {
        this.certificationBodyId = certificationBodyId;
    }

    public Long getProductClassificationId() {
        return productClassificationId;
    }

    public void setProductClassificationId(final Long productClassificationId) {
        this.productClassificationId = productClassificationId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(final String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getPracticeType() {
        return practiceType;
    }

    public void setPracticeType(final String practiceType) {
        this.practiceType = practiceType;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(final String developerName) {
        this.developerName = developerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(final String productVersion) {
        this.productVersion = productVersion;
    }

    public String getCertificationEdition() {
        return certificationEdition;
    }

    public void setCertificationEdition(final String certificationEdition) {
        this.certificationEdition = certificationEdition;
    }

    public String getAcbCertificationId() {
        return acbCertificationId;
    }

    public void setAcbCertificationId(final String acbCertificationId) {
        this.acbCertificationId = acbCertificationId;
    }

    public String getCertificationBodyName() {
        return certificationBodyName;
    }

    public void setCertificationBodyName(final String certificationBodyName) {
        this.certificationBodyName = certificationBodyName;
    }

    public String getProductClassificationName() {
        return productClassificationName;
    }

    public void setProductClassificationName(final String productClassificationName) {
        this.productClassificationName = productClassificationName;
    }

    public Date getCertificationDate() {
        return Util.getNewDate(certificationDate);
    }

    public void setCertificationDate(final Date certificationDate) {
        this.certificationDate = Util.getNewDate(certificationDate);
    }

    public String getDeveloperStreetAddress() {
        return developerStreetAddress;
    }

    public void setDeveloperStreetAddress(final String developerStreetAddress) {
        this.developerStreetAddress = developerStreetAddress;
    }

    public String getDeveloperCity() {
        return developerCity;
    }

    public void setDeveloperCity(final String developerCity) {
        this.developerCity = developerCity;
    }

    public String getDeveloperState() {
        return developerState;
    }

    public void setDeveloperState(final String developerState) {
        this.developerState = developerState;
    }

    public String getDeveloperZipCode() {
        return developerZipCode;
    }

    public void setDeveloperZipCode(final String developerZipCode) {
        this.developerZipCode = developerZipCode;
    }

    public String getDeveloperWebsite() {
        return developerWebsite;
    }

    public void setDeveloperWebsite(final String developerWebsite) {
        this.developerWebsite = developerWebsite;
    }

    public String getDeveloperEmail() {
        return developerEmail;
    }

    public void setDeveloperEmail(final String developerEmail) {
        this.developerEmail = developerEmail;
    }

    public String getReportFileLocation() {
        return reportFileLocation;
    }

    public void setReportFileLocation(final String reportFileLocation) {
        this.reportFileLocation = reportFileLocation;
    }

    public Set<PendingCertificationResultEntity> getCertificationCriterion() {
        return certificationCriterion;
    }

    public void setCertificationCriterion(final Set<PendingCertificationResultEntity> certificationCriterion) {
        this.certificationCriterion = certificationCriterion;
    }

    public Set<PendingCqmCriterionEntity> getCqmCriterion() {
        return cqmCriterion;
    }

    public void setCqmCriterion(final Set<PendingCqmCriterionEntity> cqmCriterion) {
        this.cqmCriterion = cqmCriterion;
    }

    public AddressEntity getDeveloperAddress() {
        return developerAddress;
    }

    public void setDeveloperAddress(final AddressEntity developerAddress) {
        this.developerAddress = developerAddress;
    }

    public Date getLastModifiedDate() {
        return Util.getNewDate(lastModifiedDate);
    }

    public void setLastModifiedDate(final Date lastModifiedDate) {
        this.lastModifiedDate = Util.getNewDate(lastModifiedDate);
    }

    public Long getLastModifiedUser() {
        return lastModifiedUser;
    }

    public void setLastModifiedUser(final Long lastModifiedUser) {
        this.lastModifiedUser = lastModifiedUser;
    }

    public Date getCreationDate() {
        return Util.getNewDate(creationDate);
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = Util.getNewDate(creationDate);
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(final Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public Boolean getIcs() {
        return ics;
    }

    public void setIcs(final Boolean ics) {
        this.ics = ics;
    }

    public String getDeveloperContactName() {
        return developerContactName;
    }

    public void setDeveloperContactName(final String developerContactName) {
        this.developerContactName = developerContactName;
    }

    public String getDeveloperPhoneNumber() {
        return developerPhoneNumber;
    }

    public void setDeveloperPhoneNumber(final String developerPhoneNumber) {
        this.developerPhoneNumber = developerPhoneNumber;
    }

    public String getSedReportFileLocation() {
        return sedReportFileLocation;
    }

    public void setSedReportFileLocation(final String sedReportFileLocation) {
        this.sedReportFileLocation = sedReportFileLocation;
    }

    public Set<PendingCertifiedProductQmsStandardEntity> getQmsStandards() {
        return qmsStandards;
    }

    public void setQmsStandards(final Set<PendingCertifiedProductQmsStandardEntity> qmsStandards) {
        this.qmsStandards = qmsStandards;
    }

    public Boolean isHasQms() {
        return hasQms;
    }

    public void setHasQms(final Boolean hasQms) {
        this.hasQms = hasQms;
    }

    public AttestationType getTransparencyAttestation() {
        return transparencyAttestation;
    }

    public void setTransparencyAttestation(final AttestationType transparencyAttestation) {
        this.transparencyAttestation = transparencyAttestation;
    }

    public Long getDeveloperContactId() {
        return developerContactId;
    }

    public void setDeveloperContactId(final Long developerContactId) {
        this.developerContactId = developerContactId;
    }

    public String getTransparencyAttestationUrl() {
        return transparencyAttestationUrl;
    }

    public void setTransparencyAttestationUrl(final String transparencyAttestationUrl) {
        this.transparencyAttestationUrl = transparencyAttestationUrl;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(final List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public Boolean getAccessibilityCertified() {
        return accessibilityCertified;
    }

    public void setAccessibilityCertified(final Boolean accessibilityCertified) {
        this.accessibilityCertified = accessibilityCertified;
    }

    public Set<PendingCertifiedProductTestingLabMapEntity> getTestingLabs() {
        return testingLabs;
    }

    public void setTestingLabs(final Set<PendingCertifiedProductTestingLabMapEntity> testingLabs) {
        this.testingLabs = testingLabs;
    }

    public Set<PendingCertifiedProductTargetedUserEntity> getTargetedUsers() {
        return targetedUsers;
    }

    public void setTargetedUsers(final Set<PendingCertifiedProductTargetedUserEntity> targetedUsers) {
        this.targetedUsers = targetedUsers;
    }

    public Set<PendingCertifiedProductAccessibilityStandardEntity> getAccessibilityStandards() {
        return accessibilityStandards;
    }

    public void setAccessibilityStandards(final
            Set<PendingCertifiedProductAccessibilityStandardEntity> accessibilityStandards) {
        this.accessibilityStandards = accessibilityStandards;
    }

    public String getSedIntendedUserDescription() {
        return sedIntendedUserDescription;
    }

    public void setSedIntendedUserDescription(final String sedIntendedUserDescription) {
        this.sedIntendedUserDescription = sedIntendedUserDescription;
    }

    public Date getSedTestingEnd() {
        return Util.getNewDate(sedTestingEnd);
    }

    public void setSedTestingEnd(final Date sedTestingEnd) {
        this.sedTestingEnd = Util.getNewDate(sedTestingEnd);
    }

    public Set<PendingCertifiedProductParentListingEntity> getParentListings() {
        return parentListings;
    }

    public void setParentListings(final Set<PendingCertifiedProductParentListingEntity> parentListings) {
        this.parentListings = parentListings;
    }
}
