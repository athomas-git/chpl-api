package gov.healthit.chpl.domain.compliance;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;
import lombok.NoArgsConstructor;

@XmlType(namespace = "http://chpl.healthit.gov/listings")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectReviewNonConformity implements Serializable {
    private static final long serialVersionUID = 7018071377961783691L;
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLOSED = "CLOSED";
    private static final String NOT_APPLICABLE = "Not applicable";
    private static final String NOT_DETERMINED = "Not determined";
    private static final String NOT_COMPLETED = "Not completed";

    @JsonProperty(value = "requirement", access = Access.WRITE_ONLY)
    @JsonAlias("customfield_11018")
    @JsonDeserialize(using = SimpleValueDeserializer.class)
    @XmlTransient
    private String requirement;

    @JsonProperty(value = "developerAssociatedListings")
    @JsonAlias("customfield_12202")
    @JsonDeserialize(using = ListingDeserializer.class)
    @XmlElementWrapper(name = "developerAssociatedListings", nillable = true, required = false)
    @XmlElement(name = "listing")
    private List<DeveloperAssociatedListing> developerAssociatedListings = new ArrayList<DeveloperAssociatedListing>();

    @JsonProperty(value = "nonConformityType")
    @JsonAlias("customfield_11036")
    @XmlElement(required = false, nillable = true)
    private String nonConformityType;

    @JsonProperty(value = "dateOfDetermination", access = Access.WRITE_ONLY)
    @JsonAlias("customfield_11021")
    @JsonDeserialize(using = DateDeserializer.class)
    @XmlTransient
    private LocalDate dateOfDetermination;

    @JsonProperty(value = "nonConformitySummary", access = Access.WRITE_ONLY)
    @JsonAlias("customfield_11026")
    @XmlTransient
    private String nonConformitySummary;

    @JsonProperty(value = "nonConformityFindings", access = Access.WRITE_ONLY)
    @JsonAlias("customfield_11027")
    @XmlTransient
    private String nonConformityFindings;

    @JsonProperty(value = "nonConformityStatus")
    @JsonAlias("customfield_11035")
    private String nonConformityStatus;

    @JsonProperty(value = "nonConformityResolution", access = Access.WRITE_ONLY)
    @JsonAlias("customfield_11029")
    @XmlTransient
    private String nonConformityResolution;

    @JsonProperty(value = "capStatus", access = Access.WRITE_ONLY)
    @JsonAlias("customfield_TBD")
    @XmlTransient
    private String capStatus;

    @JsonProperty(value = "capApprovalDateInternal", access = Access.WRITE_ONLY)
    @JsonAlias("customfield_11022")
    @JsonDeserialize(using = DateDeserializer.class)
    @XmlTransient
    private LocalDate capApprovalDateInternal;

    @JsonProperty(value = "capStartDate", access = Access.WRITE_ONLY)
    @JsonAlias("customfield_11023")
    @JsonDeserialize(using = DateDeserializer.class)
    @XmlTransient
    private LocalDate capStartDate;

    @JsonProperty(value = "capMustCompleteDateInternal", access = Access.WRITE_ONLY)
    @JsonAlias("customfield_11024")
    @JsonDeserialize(using = DateDeserializer.class)
    @XmlTransient
    private LocalDate capMustCompleteDateInternal;

    @JsonProperty(value = "capEndDateInternal", access = Access.WRITE_ONLY)
    @JsonAlias("customfield_11025")
    @JsonDeserialize(using = DateDeserializer.class)
    @XmlTransient
    private LocalDate capEndDateInternal;

    @JsonProperty(value = "lastUpdated")
    @JsonAlias("updated")
    @JsonDeserialize(using = TimestampDeserializer.class)
    @XmlElement(required = true, nillable = false)
    private Date lastUpdated;

    @JsonProperty(value = "created")
    @JsonDeserialize(using = TimestampDeserializer.class)
    @XmlElement(required = true, nillable = false)
    private Date created;

    @JsonProperty(value = "capApprovalDate")
    @XmlElement(required = false, nillable = true)
    public String getCapApprovalDate() {
        if (getCapApprovalDateInternal() != null) {
            return getCapApprovalDateInternal().toString();
        }
        return getCapStatus();
    }

    @JsonProperty(value = "capMustCompleteDate")
    @XmlElement(required = false, nillable = true)
    public String getCapMustCompleteDate() {
        if (getCapMustCompleteDateInternal() != null) {
            return getCapMustCompleteDateInternal().toString();
        } else if (!StringUtils.isEmpty(getCapStatus())) {
            DirectReviewNonConformityCapStatus capStatusValue = DirectReviewNonConformityCapStatus.getByName(getCapStatus());
            switch (capStatusValue) {
            case RESOLVED_WITHOUT_CAP:
                return NOT_APPLICABLE;
            case TBD:
            case CAP_NOT_PROVIDED:
            case CAP_NOT_APPROVED:
                return NOT_DETERMINED;
            case CAP_APPROVED:
            case FAILED_TO_COMPLETE:
            default:
                return "";
            }
        }
        return "";
    }

    @JsonProperty(value = "capEndDate")
    @XmlElement(required = false, nillable = true)
    public String getCapEndDate() {
        if (getCapEndDateInternal() != null) {
            return getCapEndDateInternal().toString();
        }
        return NOT_COMPLETED;
    }
}
