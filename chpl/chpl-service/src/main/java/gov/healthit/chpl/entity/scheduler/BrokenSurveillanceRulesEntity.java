package gov.healthit.chpl.entity.scheduler;

import java.time.LocalDate;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import gov.healthit.chpl.entity.CertificationBodyEntity;
import gov.healthit.chpl.entity.EntityAudit;
import gov.healthit.chpl.entity.lastmodifieduserstrategy.LastModifiedUserStrategy;
import gov.healthit.chpl.entity.lastmodifieduserstrategy.SystemUserStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "broken_surveillance_rules")
public class BrokenSurveillanceRulesEntity extends EntityAudit {
    private static final long serialVersionUID = 2870697375846146076L;

    @Override
    public LastModifiedUserStrategy getLastModifiedUserStrategy() {
        return new SystemUserStrategy();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "developer")
    private String developer;

    @Basic(optional = false)
    @Column(name = "product")
    private String product;

    @Basic(optional = false)
    @Column(name = "version")
    private String version;

    @Basic(optional = false)
    @Column(name = "chpl_product_number")
    private String chplProductNumber;

    @Basic(optional = false)
    @Column(name = "url")
    private String url;

    @Column(name = "certification_body_id")
    private Long certificationBodyId;

    @Basic(optional = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_body_id", nullable = false, insertable = false, updatable = false)
    private CertificationBodyEntity certificationBody;

    @Basic(optional = false)
    @Column(name = "certification_status")
    private String certificationStatus;

    @Basic(optional = false)
    @Column(name = "date_of_last_status_change")
    private String dateOfLastStatusChange;

    @Basic(optional = true)
    @Column(name = "surveillance_id")
    private String surveillanceId;

    @Basic(optional = true)
    @Column(name = "date_surveillance_began")
    private String dateSurveillanceBegan;

    @Basic(optional = true)
    @Column(name = "date_surveillance_ended")
    private String dateSurveillanceEnded;

    @Basic(optional = true)
    @Column(name = "surveillance_type")
    private String surveillanceType;

    @Basic(optional = true)
    @Column(name = "lengthy_suspension_rule")
    private String lengthySuspensionRule;

    @Basic(optional = true)
    @Column(name = "cap_not_approved_rule")
    private String capNotApprovedRule;

    @Basic(optional = true)
    @Column(name = "cap_not_started_rule")
    private String capNotStartedRule;

    @Basic(optional = true)
    @Column(name = "cap_not_completed_rule")
    private String capNotCompletedRule;

    @Basic(optional = true)
    @Column(name = "cap_not_closed_rule")
    private String capNotClosedRule;

    @Basic(optional = true)
    @Column(name = "closed_cap_with_open_nonconformity_rule")
    private String closedCapWithOpenNonconformityRule;

    @Basic(optional = false)
    @Column(name = "nonconformity")
    private Boolean nonconformity;

    @Basic(optional = true)
    @Column(name = "non_conformity_close_date")
    private LocalDate nonConformityCloseDate;

    @Basic(optional = true)
    @Column(name = "nonconformity_criteria")
    private String nonconformityCriteria;

    @Basic(optional = true)
    @Column(name = "date_of_determination_of_nonconformity")
    private String dateOfDeterminationOfNonconformity;

    @Basic(optional = true)
    @Column(name = "corrective_action_plan_approved_date")
    private String correctiveActionPlanApprovedDate;

    @Basic(optional = true)
    @Column(name = "date_corrective_action_began")
    private String dateCorrectiveActionBegan;

    @Basic(optional = true)
    @Column(name = "date_corrective_action_must_be_completed")
    private String dateCorrectiveActionMustBeCompleted;

    @Basic(optional = true)
    @Column(name = "date_corrective_action_was_completed")
    private String dateCorrectiveActionWasCompleted;

    @Basic(optional = true)
    @Column(name = "number_of_days_from_determination_to_cap_approval")
    private long numberOfDaysFromDeterminationToCapApproval;

    @Basic(optional = true)
    @Column(name = "number_of_days_from_determination_to_present")
    private long numberOfDaysFromDeterminationToPresent;

    @Basic(optional = true)
    @Column(name = "number_of_days_from_cap_approval_to_cap_began")
    private long numberOfDaysFromCapApprovalToCapBegan;

    @Basic(optional = true)
    @Column(name = "number_of_days_from_cap_approval_to_present")
    private long numberOfDaysFromCapApprovalToPresent;

    @Basic(optional = true)
    @Column(name = "number_of_days_from_cap_began_to_cap_completed")
    private long numberOfDaysFromCapBeganToCapCompleted;

    @Basic(optional = true)
    @Column(name = "number_of_days_from_cap_began_to_present")
    private long numberOfDaysFromCapBeganToPresent;

    @Basic(optional = true)
    @Column(name = "difference_from_cap_completed_and_cap_must_be_completed")
    private long differenceFromCapCompletedAndCapMustBeCompleted;

}
