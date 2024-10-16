package gov.healthit.chpl.report.criteriamigrationreport;

import java.util.List;

import org.hibernate.Hibernate;

import gov.healthit.chpl.certificationCriteria.CertificationCriterionEntity;
import gov.healthit.chpl.entity.EntityAudit;
import gov.healthit.chpl.entity.lastmodifieduserstrategy.LastModifiedUserStrategy;
import gov.healthit.chpl.entity.lastmodifieduserstrategy.SystemUserStrategy;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "criteria_migration_definition")
public class CriteriaMigrationDefinitionEntity extends EntityAudit {
    private static final long serialVersionUID = 5484388441051382895L;

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
    @Column(name = "criteria_migration_report_id", nullable = false)
    private Long criteriaMigrationReportId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "original_certification_criterion_id", insertable = false, updatable = false)
    private CertificationCriterionEntity originalCriterion;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_certification_criterion_id", insertable = false, updatable = false)
    private CertificationCriterionEntity updatedCriterion;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id")
    @Column(name = "criteria_migration_definition_id", nullable = false)
    private List<CriteriaMigrationCountEntity> criteriaMigrationCounts;

    public CriteriaMigrationDefinition toDomain() {
        return CriteriaMigrationDefinition.builder()
                .id(this.id)
                .originalCriterion(this.originalCriterion.toDomain())
                .updatedCriterion(this.updatedCriterion.toDomain())
                .criteriaMigrationCounts(!Hibernate.isInitialized(this.criteriaMigrationCounts) ? null : this.criteriaMigrationCounts.stream()
                        .map(e -> e.toDomain())
                        .toList())
                .build();
    }
}
