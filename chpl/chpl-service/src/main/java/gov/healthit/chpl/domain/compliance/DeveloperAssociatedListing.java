package gov.healthit.chpl.domain.compliance;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DeveloperAssociatedListing implements Serializable {
    private static final long serialVersionUID = 5321764022740308740L;

    private Long id;
    private String chplProductNumber;
}
