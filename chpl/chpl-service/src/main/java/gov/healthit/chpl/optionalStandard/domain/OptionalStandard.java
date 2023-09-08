package gov.healthit.chpl.optionalStandard.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OptionalStandard implements Serializable {
    private static final long serialVersionUID = 620315627813875501L;
    private Long id;
    private String citation;
    private String description;
}
