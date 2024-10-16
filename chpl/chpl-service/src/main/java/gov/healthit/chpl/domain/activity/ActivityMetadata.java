package gov.healthit.chpl.domain.activity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import gov.healthit.chpl.api.deprecatedUsage.DeprecatedResponseField;
import gov.healthit.chpl.domain.auth.User;
import gov.healthit.chpl.util.Util;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

@Builder
@AllArgsConstructor
@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class ActivityMetadata implements Serializable {
    private static final long serialVersionUID = -3855142961571082535L;

    private Long id;
    private ActivityConcept concept;
    @Singular
    private Set<ActivityCategory> categories = new HashSet<ActivityCategory>();
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Date date;

    @DeprecatedResponseField(message = "This field is deprecated and will be removed. Use object.id",
            removalDate = "2024-10-31")
    @Deprecated
    private Long objectId;

    private ActivityObject object;
    private User responsibleUser;
    private String description;

    @Override
    public boolean equals(Object another) {
        if (another == null) {
            return false;
        }
        if (!(another instanceof ActivityMetadata)) {
            return false;
        }
        ActivityMetadata anotherMeta = (ActivityMetadata) another;
        return Objects.equals(this.id, anotherMeta.id);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    public Date getDate() {
        return Util.getNewDate(date);
    }

    public void setDate(Date date) {
        this.date = Util.getNewDate(date);
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class ActivityObject {
        private Long id;
        private String name;
    }
}
