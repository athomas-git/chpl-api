package gov.healthit.chpl.manager;

import java.util.List;

import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.domain.AdditionalSoftware;
import gov.healthit.chpl.dto.CQMResultAdditionalSoftwareMapDTO;
import gov.healthit.chpl.dto.CertificationResultAdditionalSoftwareMapDTO;

public interface AdditionalSoftwareManager {
	
	public CertificationResultAdditionalSoftwareMapDTO addAdditionalSoftwareCertificationResultMapping(Long additionalSoftwareId, Long certificationResultId) throws EntityCreationException;
	public CQMResultAdditionalSoftwareMapDTO addAdditionalSoftwareCQMResultMapping(Long additionalSoftwareId, Long cqmResultId) throws EntityCreationException;
	public void deleteAdditionalSoftwareCertificationResultMapping(Long additionalSoftwareId, Long certificationResultId);
	public void deleteAdditionalSoftwareCQMResultMapping(Long additionalSoftwareId, Long cqmResultId);
	public void associateAdditionalSoftwareCerifiedProductSelf(Long additionalSoftwareId, Long certifiedProductId) throws EntityRetrievalException;
	public List<AdditionalSoftware> getAdditionalSoftwareByCQMResultId(Long id);
	public List<AdditionalSoftware> getAdditionalSoftwareByCertificationResultId(Long id);
	
}
