package gov.healthit.chpl.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import gov.healthit.chpl.domain.Product;
import gov.healthit.chpl.domain.ProductOwner;
import gov.healthit.chpl.domain.comparator.ProductOwnerComparator;
import gov.healthit.chpl.entity.developer.DeveloperEntity;
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
@DynamicUpdate
@Table(name = "product")
public class ProductEntity extends EntityAudit {
    private static final long serialVersionUID = -5332080900089062551L;

    @Transient
    private final ProductOwnerComparator productOwnerComparator = new ProductOwnerComparator();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "product_id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @Size(min = 1)
    @Column(name = "name")
    private String name;

    @Basic(optional = true)
    @Column(name = "contact_id")
    private Long contactId;

    @Basic(optional = true)
    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", unique = true, nullable = true, insertable = false, updatable = false)
    private ContactEntity contact;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productId")
    @Basic(optional = false)
    @Column(name = "product_id", nullable = false)
    @Where(clause = "deleted <> 'true'")
    private Set<ProductVersionEntity> productVersions = new HashSet<ProductVersionEntity>();

    @Basic(optional = true)
    @Column(name = "report_file_location", length = 255)
    private String reportFileLocation;

    @Basic(optional = false)
    @Column(name = "vendor_id", nullable = false)
    private Long developerId;

    @Basic(optional = true)
    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", unique = true, nullable = true, insertable = false, updatable = false)
    private DeveloperEntity developer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productId")
    @Basic(optional = true)
    @Column(name = "product_id", nullable = false)
    @Where(clause = "deleted <> 'true'")
    private Set<ProductActiveOwnerEntity> ownerHistory = new HashSet<ProductActiveOwnerEntity>();

    public Product toDomain() {
        return Product.builder()
                .id(this.getId())
                .contact(this.getContact() != null ? this.getContact().toDomain() : null)
                .lastModifiedDate(this.getLastModifiedDate().getTime() + "")
                .name(this.getName())
                .owner(this.getDeveloper() != null ? this.getDeveloper().toDomain() : null)
                .reportFileLocation(this.getReportFileLocation())
                .ownerHistory(toOwnerHistoryDomains())
                .build();
    }

    private List<ProductOwner> toOwnerHistoryDomains() {
        if (CollectionUtils.isEmpty(this.getOwnerHistory())) {
            return new ArrayList<ProductOwner>();
        }
        return this.getOwnerHistory().stream()
            .map(ownerHistoryItem -> ownerHistoryItem.toDomain())
            .sorted(productOwnerComparator)
            .collect(Collectors.toList());
    }
}
