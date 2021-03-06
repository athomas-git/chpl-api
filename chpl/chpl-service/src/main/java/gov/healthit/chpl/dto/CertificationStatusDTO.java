package gov.healthit.chpl.dto;

import java.io.Serializable;

import gov.healthit.chpl.entity.CertificationStatusEntity;

public class CertificationStatusDTO implements Serializable {
    private static final long serialVersionUID = 7214567031021821333L;
    private Long id;
    private String status;

    public CertificationStatusDTO() {
    }

    public CertificationStatusDTO(CertificationStatusEntity entity) {
        this.setId(entity.getId());
        this.setStatus(entity.getStatus());
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }
}
