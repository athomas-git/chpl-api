package gov.healthit.chpl.dto;

import java.util.Date;

public class CertificationResultDTO {

	private Boolean automatedMeasureCapable;
	private Boolean automatedNumerator;
	private Long certificationCriterionId;
	private CertifiedProductDTO certifiedProduct;
	private Date creationDate;
	private Boolean deleted;
	private Boolean gap;
	private Long id;
	private Boolean inherited;
	private Date lastModifiedDate;
	private Long lastModifiedUser;
	private Boolean sedInherited;
	private Boolean sedSuccessful;
	private Boolean successful;
	private Long testDataVersionId;
	private Long testProcedureVersionId;
	
	
	public Boolean getAutomatedMeasureCapable() {
		return automatedMeasureCapable;
	}
	public void setAutomatedMeasureCapable(Boolean automatedMeasureCapable) {
		this.automatedMeasureCapable = automatedMeasureCapable;
	}
	public Boolean getAutomatedNumerator() {
		return automatedNumerator;
	}
	public void setAutomatedNumerator(Boolean automatedNumerator) {
		this.automatedNumerator = automatedNumerator;
	}
	public Long getCertificationCriterionId() {
		return certificationCriterionId;
	}
	public void setCertificationCriterionId(Long certificationCriterionId) {
		this.certificationCriterionId = certificationCriterionId;
	}
	public CertifiedProductDTO getCertifiedProduct() {
		return certifiedProduct;
	}
	public void setCertifiedProduct(CertifiedProductDTO certifiedProduct) {
		this.certifiedProduct = certifiedProduct;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public Boolean getGap() {
		return gap;
	}
	public void setGap(Boolean gap) {
		this.gap = gap;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getInherited() {
		return inherited;
	}
	public void setInherited(Boolean inherited) {
		this.inherited = inherited;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Long getLastModifiedUser() {
		return lastModifiedUser;
	}
	public void setLastModifiedUser(Long lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}
	public Boolean getSedInherited() {
		return sedInherited;
	}
	public void setSedInherited(Boolean sedInherited) {
		this.sedInherited = sedInherited;
	}
	public Boolean getSedSuccessful() {
		return sedSuccessful;
	}
	public void setSedSuccessful(Boolean sedSuccessful) {
		this.sedSuccessful = sedSuccessful;
	}
	public Boolean getSuccessful() {
		return successful;
	}
	public void setSuccessful(Boolean successful) {
		this.successful = successful;
	}
	public Long getTestDataVersionId() {
		return testDataVersionId;
	}
	public void setTestDataVersionId(Long testDataVersionId) {
		this.testDataVersionId = testDataVersionId;
	}
	public Long getTestProcedureVersionId() {
		return testProcedureVersionId;
	}
	public void setTestProcedureVersionId(Long testProcedureVersionId) {
		this.testProcedureVersionId = testProcedureVersionId;
	}
	
}
