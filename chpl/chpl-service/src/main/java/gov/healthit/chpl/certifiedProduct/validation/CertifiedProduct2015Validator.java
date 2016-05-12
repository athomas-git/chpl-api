package gov.healthit.chpl.certifiedProduct.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.domain.CertificationResult;
import gov.healthit.chpl.domain.CertificationResultTestTask;
import gov.healthit.chpl.domain.CertifiedProductSearchDetails;
import gov.healthit.chpl.dto.PendingCertificationResultDTO;
import gov.healthit.chpl.dto.PendingCertificationResultTestTaskDTO;
import gov.healthit.chpl.dto.PendingCertificationResultTestTaskParticipantDTO;
import gov.healthit.chpl.dto.PendingCertifiedProductDTO;
import gov.healthit.chpl.dto.PendingCertifiedProductQmsStandardDTO;
import gov.healthit.chpl.util.CertificationResultRules;

@Component("certifiedProduct2015Validator")
public class CertifiedProduct2015Validator extends CertifiedProductValidatorImpl {

	private static final String[] aComplimentaryCerts = {"170.315 (d)(1)", "170.315 (d)(2)", "170.315 (d)(3)",
			"170.315 (d)(4)", "170.315 (d)(5)", "170.315 (d)(6)", "170.315 (d)(7)"};
	
	private static final String[] bComplimentaryCerts = {"170.315 (d)(1)", "170.315 (d)(2)", "170.315 (d)(3)",
			"170.315 (d)(5)", "170.315 (d)(6)", "170.315 (d)(7)", "170.315 (d)(8)"};
	
	private static final String[] cComplimentaryCerts = {"170.315 (d)(1)", "170.315 (d)(2)", "170.315 (d)(3)",
			"170.315 (d)(5)"};
	
	private static final String[] e1ComplimentaryCerts = {"170.315 (d)(1)", "170.315 (d)(2)", "170.315 (d)(3)",
		"170.315 (d)(5)", "170.315 (d)(7)", "170.315 (d)(9)"};
	
	private static final String[] e2Ore3ComplimentaryCerts = {"170.315 (d)(1)", "170.315 (d)(2)", "170.315 (d)(3)",
		"170.315 (d)(5)", "170.315 (d)(9)"};
	
	private static final String[] fComplimentaryCerts = {"170.315 (d)(1)", "170.315 (d)(2)", "170.315 (d)(3)",
		"170.315 (d)(7)"};
	
	private static final String[] g7Org8Org9ComplimentaryCerts = {"170.315 (d)(1)", "170.315 (d)(9)"};

	private static final String[] hComplimentaryCerts = {"170.315 (d)(1)", "170.315 (d)(2)", "170.315 (d)(3)"};
	
	private static final String[] ucdRequiredCerts = {"170.315 (a)(1)", "170.315 (a)(2)", "170.315 (a)(3)", 
			"170.315 (a)(4)", "170.315 (a)(5)", "170.315 (a)(6)", "170.315 (a)(7)", "170.315 (a)(8)", 
			"170.315 (a)(9)", "170.315 (a)(14)", "170.315 (b)(2)", "170.315 (b)(3)"};

	private static final String[] g6CertsToCheck = {"170.315 (b)(1)", "170.315 (b)(2)", "170.315 (b)(4)",
			"170.315 (b)(6)", "170.315 (b)(9)", "170.315 (e)(1)", "170.315 (g)(9)"};
	
	@Override
	public void validate(PendingCertifiedProductDTO product) {
		super.validate(product);
		
		List<String> allMetCerts = new ArrayList<String>();
		for(PendingCertificationResultDTO certCriteria : product.getCertificationCriterion()) {
			if(certCriteria.getMeetsCriteria()) {
				allMetCerts.add(certCriteria.getNumber());
			}
		}
		
		List<String> errors = checkClassOfCriteriaForErrors("170.315 (a)", allMetCerts, Arrays.asList(aComplimentaryCerts));
		product.getErrorMessages().addAll(errors);
		
		errors = checkClassOfCriteriaForErrors("170.315 (b)", allMetCerts, Arrays.asList(bComplimentaryCerts));
		product.getErrorMessages().addAll(errors);
		
		errors = checkClassOfCriteriaForErrors("170.315 (c)", allMetCerts, Arrays.asList(cComplimentaryCerts));
		product.getErrorMessages().addAll(errors);

		errors = checkClassOfCriteriaForErrors("170.315 (f)", allMetCerts, Arrays.asList(fComplimentaryCerts));
		product.getErrorMessages().addAll(errors);
		
		errors = checkClassOfCriteriaForErrors("170.315 (h)", allMetCerts, Arrays.asList(hComplimentaryCerts));
		product.getErrorMessages().addAll(errors);
		
		errors = checkSpecificCriteriaForErrors("170.315 (e)(1)", allMetCerts, Arrays.asList(e1ComplimentaryCerts));
		product.getErrorMessages().addAll(errors);
		
		//check for (e)(2) or (e)(3) certs
		boolean meetsE2Criterion = hasCert("170.315 (e)(2)", allMetCerts);;
		boolean meetsE3Criterion = hasCert("170.315 (e)(3)", allMetCerts);;
		if(meetsE2Criterion || meetsE3Criterion) {
			for(int i = 0; i < e2Ore3ComplimentaryCerts.length; i++) {
				boolean hasComplimentaryCert = false;
				for(PendingCertificationResultDTO certCriteria : product.getCertificationCriterion()) {
					if(certCriteria.getNumber().equals(e2Ore3ComplimentaryCerts[i]) && certCriteria.getMeetsCriteria()) {
						hasComplimentaryCert = true;
					}
				}
				
				if(!hasComplimentaryCert) {
					product.getErrorMessages().add("Certification criterion 170.315 (e)(2) or 170.315 (e)(3) was found so " + e2Ore3ComplimentaryCerts[i] + " is required but was not found.");

				}
			}
		}
		
		//check for (g)(7) or (g)(8) or (g)(9) certs
		boolean meetsG7Criterion = hasCert("170.315 (g)(7)", allMetCerts);
		boolean meetsG8Criterion = hasCert("170.315 (g)(8)", allMetCerts);
		boolean meetsG9Criterion = hasCert("170.315 (g)(9)", allMetCerts);	
		if(meetsG7Criterion || meetsG8Criterion || meetsG9Criterion) {
			for(int i = 0; i < g7Org8Org9ComplimentaryCerts.length; i++) {
				boolean hasComplimentaryCert = false;
				for(PendingCertificationResultDTO certCriteria : product.getCertificationCriterion()) {
					if(certCriteria.getNumber().equals(g7Org8Org9ComplimentaryCerts[i]) && certCriteria.getMeetsCriteria()) {
						hasComplimentaryCert = true;
					}
				}
				
				if(!hasComplimentaryCert) {
					product.getErrorMessages().add("Certification criterion 170.315 (g)(7) or 170.315 (g)(8) or 170.315 (g)(9) was found so " + g7Org8Org9ComplimentaryCerts[i] + " is required but was not found.");

				}
			}
			
			boolean meetsD2Criterion = hasCert("170.315 (d)(2)", allMetCerts);
			boolean meetsD10Criterion = hasCert("170.315 (d)(10)", allMetCerts);
			if(!meetsD2Criterion && !meetsD10Criterion) {
				product.getErrorMessages().add("Certification criterion 170.315 (g)(7) or 170.315 (g)(8) or 170.315 (g)(9) was found so 170.315 (d)(2) or 170.315 (d)(10) is also required.");
			}
		}
		
		//g3 checks
		boolean needsG3 = false;
		for(int i = 0; i < ucdRequiredCerts.length; i++) {
			if(hasCert(ucdRequiredCerts[i], allMetCerts)) {
				needsG3 = true;
				
				//check for full set of UCD data
				for(PendingCertificationResultDTO certCriteria : product.getCertificationCriterion()) {
					if(certCriteria.getNumber().equals(ucdRequiredCerts[i])) {
						if(certCriteria.getUcdProcesses() == null || certCriteria.getUcdProcesses().size() == 0) {
							product.getErrorMessages().add("Certification " + certCriteria.getNumber() + " requires at least one UCD process.");
						} 
						if(certCriteria.getTestTasks() == null || certCriteria.getTestTasks().size() == 0) {
							product.getErrorMessages().add("Certification " + certCriteria.getNumber() + " requires at least one test task.");
						} else {
							for(PendingCertificationResultTestTaskDTO task : certCriteria.getTestTasks()) {
								if(task.getTaskParticipants() == null || task.getTaskParticipants().size() < 10) {
									product.getErrorMessages().add("A test task for certification " + certCriteria.getNumber() + " requires at least 10 participants.");
								}
								for(PendingCertificationResultTestTaskParticipantDTO part : task.getTaskParticipants()) {
									if(part.getTestParticipant().getEducationTypeId() == null) {
										product.getErrorMessages().add("Found no matching eduation level for test participant (age: " + 
												part.getTestParticipant().getAge() + " and gender: " + part.getTestParticipant().getGender() 
												+ ") related to " + certCriteria.getNumber() + ".");
									}
								}
							}
						}
					}
				}
			}
		}
		if(needsG3) {
			boolean hasG3 = hasCert("170.315 (g)(3)", allMetCerts);
			if(!hasG3) {
				product.getErrorMessages().add("170.315 (g)(3) is required but was not found.");
			}
		}		
		
		//g3 inverse check
		boolean hasG3ComplimentaryCerts = false;
		for(int i = 0; i < ucdRequiredCerts.length; i++) {
			if(hasCert(ucdRequiredCerts[i], allMetCerts)) {
				hasG3ComplimentaryCerts = true;
			}
		}
		if(!hasG3ComplimentaryCerts) {
			//make sure it doesn't have g3
			boolean hasG3 = hasCert("170.315 (g)(3)", allMetCerts);
			if(hasG3) {
				product.getErrorMessages().add("170.315 (g)(3) is not allowed but was found.");
			}
		}
		
		//g4 check
		boolean hasG4 = hasCert("170.315 (g)(4)", allMetCerts);
		if(!hasG4) {
			product.getErrorMessages().add("170.315 (g)(4) is required but was not found.");
		}
		
		//g5 check
		boolean hasG5 = hasCert("170.315 (g)(5)", allMetCerts);
		if(!hasG5) {
			product.getErrorMessages().add("170.315 (g)(5) is required but was not found.");
		}
		
		//g6 checks
		boolean needsG6 = false;
		for(int i = 0; i < g6CertsToCheck.length && !needsG6; i++) {
			if(hasCert(g6CertsToCheck[i], allMetCerts)) {
				needsG6 = true;
			}
		}
		if(needsG6) {
			boolean hasG6 = hasCert("170.315 (g)(6)", allMetCerts);
			if(!hasG6) {
				product.getErrorMessages().add("170.315 (g)(6) is required but was not found.");
			}
		}
		
		//reverse g6 check
		boolean hasG6 = hasCert("170.315 (g)(6)", allMetCerts);
		if(hasG6) {
			boolean hasG6ComplimentaryCerts = false;
			for(int i = 0; i < g6CertsToCheck.length; i++) {
				if(hasCert(g6CertsToCheck[i], allMetCerts)) {
					hasG6ComplimentaryCerts = true;
				}
			}
			
			if(!hasG6ComplimentaryCerts) {
				product.getErrorMessages().add("170.315 (g)(6) was found but a related required cert was not found.");
			}
		}
				
		//TODO: detailed G6 check
		
		//h1 plus b1
		boolean hasH1 = hasCert("170.315 (h)(1)", allMetCerts);
		if(hasH1) {
			boolean hasB1 = hasCert("170.315 (b)(1)", allMetCerts);
			if(!hasB1) {
				product.getErrorMessages().add("170.315 (h)(1) was found so 170.315 (b)(1) is required but was not found.");
			}
		}
	}

	protected void validateDemographics(PendingCertifiedProductDTO product) {
		super.validateDemographics(product);
		
		if(product.getQmsStandards() == null || product.getQmsStandards().size() == 0) {
			product.getErrorMessages().add("QMS Standards are required.");
		} else {
			for(PendingCertifiedProductQmsStandardDTO qms : product.getQmsStandards()) {
				if(StringUtils.isBlank(qms.getApplicableCriteria())) {
					product.getErrorMessages().add("Applicable criteria is required for each QMS Standard listed.");
				}
			}
		}
		
		if(product.getAccessibilityStandards() == null || product.getAccessibilityStandards().size() == 0) {
			product.getErrorMessages().add("Accessibility standards are required.");
		}
		
		for(PendingCertificationResultDTO cert : product.getCertificationCriterion()) {
			if(cert.getMeetsCriteria() != null && cert.getMeetsCriteria() == Boolean.TRUE) {
				if(certRules.hasCertOption(cert.getNumber(), CertificationResultRules.PRIVACY_SECURITY) &&
						cert.getPrivacySecurityFramework() == null) {
					product.getErrorMessages().add("Privacy and Security Framework is required for certification " + cert.getNumber() + ".");
				}
				if(certRules.hasCertOption(cert.getNumber(), CertificationResultRules.API_DOCUMENTATION) &&
						cert.getApiDocumentation() == null) {
					product.getErrorMessages().add("API Documentation is required for certification " + cert.getNumber() + ".");
				}
				if(certRules.hasCertOption(cert.getNumber(), CertificationResultRules.FUNCTIONALITY_TESTED) &&
						(cert.getTestFunctionality() == null || cert.getTestFunctionality().size() == 0)) {
					product.getErrorMessages().add("Functionality Tested is required for certification " + cert.getNumber() + ".");
				}
				if(certRules.hasCertOption(cert.getNumber(), CertificationResultRules.TEST_TOOLS_USED) &&
						(cert.getTestTools() == null || cert.getTestTools().size() == 0)) {
						product.getErrorMessages().add("Test Tools are required for certification " + cert.getNumber() + ".");
				}
				if((cert.getNumber().equals("170.315 (g)(1)") || cert.getNumber().equals("170.315 (g)(2)")) &&
					(cert.getTestData() == null || cert.getTestData().size() == 0)) {
					product.getErrorMessages().add("Test Data is required for certification " + cert.getNumber() + ".");
				}
//				if(certRules.hasCertOption(cert.getNumber(), CertificationResultRules.SED)) {
//					if(cert.getUcdProcesses() == null || cert.getUcdProcesses().size() == 0) {
//						product.getErrorMessages().add("UCD Fields are required for certification " + cert.getNumber() + ".");
//					}
//					if(cert.getTestTasks() == null || cert.getTestTasks().size() == 0) {
//						product.getErrorMessages().add("Test tasks are required for certification " + cert.getNumber() + ".");
//					}
//				}
			}
		}
	}
	
	@Override
	public void validate(CertifiedProductSearchDetails product) {
		super.validate(product);
		
		List<String> allMetCerts = new ArrayList<String>();
		for(CertificationResult certCriteria : product.getCertificationResults()) {
			if(certCriteria.isSuccess()) {
				allMetCerts.add(certCriteria.getNumber());
			}
		}
		
		List<String> errors = checkClassOfCriteriaForErrors("170.315 (a)", allMetCerts, Arrays.asList(aComplimentaryCerts));
		product.getErrorMessages().addAll(errors);
		
		errors = checkClassOfCriteriaForErrors("170.315 (b)", allMetCerts, Arrays.asList(bComplimentaryCerts));
		product.getErrorMessages().addAll(errors);
		
		errors = checkClassOfCriteriaForErrors("170.315 (c)", allMetCerts, Arrays.asList(cComplimentaryCerts));
		product.getErrorMessages().addAll(errors);

		errors = checkClassOfCriteriaForErrors("170.315 (f)", allMetCerts, Arrays.asList(fComplimentaryCerts));
		product.getErrorMessages().addAll(errors);
		
		errors = checkClassOfCriteriaForErrors("170.315 (h)", allMetCerts, Arrays.asList(hComplimentaryCerts));
		product.getErrorMessages().addAll(errors);
		
		errors = checkSpecificCriteriaForErrors("170.315 (e)(1)", allMetCerts, Arrays.asList(e1ComplimentaryCerts));
		product.getErrorMessages().addAll(errors);
		
		//check for (e)(2) or (e)(3) certs
		boolean meetsE2Criterion = hasCert("170.315 (e)(2)", allMetCerts);;
		boolean meetsE3Criterion = hasCert("170.315 (e)(3)", allMetCerts);	
		if(meetsE2Criterion || meetsE3Criterion) {
			for(int i = 0; i < e2Ore3ComplimentaryCerts.length; i++) {
				boolean hasComplimentaryCert = false;
				for(CertificationResult certCriteria : product.getCertificationResults()) {
					if(certCriteria.getNumber().equals(e2Ore3ComplimentaryCerts[i]) && certCriteria.isSuccess()) {
						hasComplimentaryCert = true;
					}
				}
				
				if(!hasComplimentaryCert) {
					product.getErrorMessages().add("Certification criterion 170.315 (e)(2) or 170.315 (e)(3) was found so " + e2Ore3ComplimentaryCerts[i] + " is required but was not found.");

				}
			}
		}
		
		//check for (g)(7) or (g)(8) or (g)(9) certs
		boolean meetsG7Criterion = hasCert("170.315 (g)(7)", allMetCerts);;
		boolean meetsG8Criterion = hasCert("170.315 (g)(8)", allMetCerts);;
		boolean meetsG9Criterion = hasCert("170.315 (g)(9)", allMetCerts);;
	
		if(meetsG7Criterion || meetsG8Criterion || meetsG9Criterion) {
			for(int i = 0; i < g7Org8Org9ComplimentaryCerts.length; i++) {
				boolean hasComplimentaryCert = false;
				for(CertificationResult certCriteria : product.getCertificationResults()) {
					if(certCriteria.getNumber().equals(g7Org8Org9ComplimentaryCerts[i]) && certCriteria.isSuccess()) {
						hasComplimentaryCert = true;
					}
				}
				
				if(!hasComplimentaryCert) {
					product.getErrorMessages().add("Certification criterion 170.315 (g)(7) or 170.315 (g)(8) or 170.315 (g)(9) was found so " + g7Org8Org9ComplimentaryCerts[i] + " is required but was not found.");

				}
			}
			
			boolean meetsD2Criterion = hasCert("170.315 (d)(2)", allMetCerts);
			boolean meetsD10Criterion = hasCert("170.315 (d)(10)", allMetCerts);;
			if(!meetsD2Criterion && !meetsD10Criterion) {
				product.getErrorMessages().add("Certification criterion 170.315 (g)(7) or 170.315 (g)(8) or 170.315 (g)(9) was found so 170.315 (d)(2) or 170.315 (d)(10) is required.");
			}
		}
		
		//g3 checks
		boolean needsG3 = false;
		for(int i = 0; i < ucdRequiredCerts.length && !needsG3; i++) {
			if(hasCert(ucdRequiredCerts[i], allMetCerts)) {
				needsG3 = true;
				
				//check for full set of UCD data
				for(CertificationResult certCriteria : product.getCertificationResults()) {
					if(certCriteria.getNumber().equals(ucdRequiredCerts[i])) {
						if(certCriteria.getUcdProcesses() == null || certCriteria.getUcdProcesses().size() == 0) {
							product.getErrorMessages().add("Certification " + certCriteria.getNumber() + " requires at least one UCD process.");
						}
						if(certCriteria.getTestTasks() == null || certCriteria.getTestTasks().size() == 0) {
							product.getErrorMessages().add("Certification " + certCriteria.getNumber() + " requires at least one test task.");
						} else {
							for(CertificationResultTestTask task : certCriteria.getTestTasks()) {
								if(task.getTestParticipants() == null || task.getTestParticipants().size() < 10) {
									product.getWarningMessages().add("A test task for certification " + certCriteria.getNumber() + " requires at least 10 participants.");
								}
							}
						}
					}
				}
			}
		}
		if(needsG3) {
			boolean hasG3 = hasCert("170.315 (g)(3)", allMetCerts);
			if(!hasG3) {
				product.getErrorMessages().add("170.315 (g)(3) is required but was not found.");
			}
		}
				
		//g3 inverse check
		boolean hasG3ComplimentaryCerts = false;
		for(int i = 0; i < ucdRequiredCerts.length; i++) {
			if(hasCert(ucdRequiredCerts[i], allMetCerts)) {
				hasG3ComplimentaryCerts = true;
			}
		}
		if(!hasG3ComplimentaryCerts) {
			//make sure it doesn't have g3
			boolean hasG3 = hasCert("170.315 (g)(3)", allMetCerts);
			if(hasG3) {
				product.getErrorMessages().add("170.315 (g)(3) is not allowed but was found.");
			}
		}
		
		//g4 check
		boolean hasG4 = hasCert("170.315 (g)(4)", allMetCerts);
		if(!hasG4) {
			product.getErrorMessages().add("170.315 (g)(4) is required but was not found.");
		}
		
		//g5 check
		boolean hasG5 = hasCert("170.315 (g)(5)", allMetCerts);
		if(!hasG5) {
			product.getErrorMessages().add("170.315 (g)(5) is required but was not found.");
		}
		
		//g6 checks
		boolean needsG6 = false;
		for(int i = 0; i < g6CertsToCheck.length && !needsG6; i++) {
			if(hasCert(g6CertsToCheck[i], allMetCerts)) {
				needsG6 = true;
			}
		}
		if(needsG6) {
			boolean hasG6 = hasCert("170.315 (g)(6)", allMetCerts);
			if(!hasG6) {
				product.getErrorMessages().add("170.315 (g)(6) is required but was not found.");
			}
		}
		
		//TODO: detailed G6 check
		
		//h1 plus b1
		boolean hasH1 = hasCert("170.315 (h)(1)", allMetCerts);
		if(hasH1) {
			boolean hasB1 = hasCert("170.315 (b)(1)", allMetCerts);
			if(!hasB1) {
				product.getErrorMessages().add("170.315 (h)(1) was found so 170.315 (b)(1) is required but was not found.");
			}
		}
	}

	private boolean hasCert(String toCheck, List<String> allCerts) {
		boolean hasCert = false;
		for(int i = 0; i < allCerts.size() && !hasCert; i++) {
			if(allCerts.get(i).equals(toCheck)) {
				hasCert = true;
			}
		}
		return hasCert;
	}
	
	/**
	 * look for required complimentary certs when one of the criteria met is a certain
	 * class of cert... such as 170.315 (a)(*)
	 * @param criterionNumberStart
	 * @param allCriteriaMet
	 * @param complimentaryCertNumbers
	 * @return
	 */
	private List<String> checkClassOfCriteriaForErrors(String criterionNumberStart, List<String> allCriteriaMet, List<String> complimentaryCertNumbers) {
		List<String> errors = new ArrayList<String>();
		boolean hasCriterion = false;
		for(String currCriteria : allCriteriaMet) {
			if(currCriteria.startsWith(criterionNumberStart)) {
				hasCriterion = true;
			}
		}	
		if(hasCriterion) {
			for(String currRequiredCriteria : complimentaryCertNumbers) {
				boolean hasComplimentaryCert = false;
				for(String certCriteria : complimentaryCertNumbers) {
					if(certCriteria.equals(currRequiredCriteria)) {
						hasComplimentaryCert = true;
					}
				}
				
				if(!hasComplimentaryCert) {
					errors.add("Certification criterion " + criterionNumberStart + "(*) was found "
							+ "so " + currRequiredCriteria + " is required but was not found.");
				}
			}
		}
		return errors;
	}
	
	/**
	 * Look for a required complimentary criteria when a specific criteria has been met
	 * @param criterionNumber
	 * @param allCriteriaMet
	 * @param complimentaryCertNumbers
	 * @return
	 */
	private List<String> checkSpecificCriteriaForErrors(String criterionNumber, List<String> allCriteriaMet, List<String> complimentaryCertNumbers) {
		List<String> errors = new ArrayList<String>();
		boolean hasCriterion = false;
		for(String currCriteria : allCriteriaMet) {
			if(currCriteria.equals(criterionNumber)) {
				hasCriterion = true;
			}
		}	
		if(hasCriterion) {
			for(String currRequiredCriteria : complimentaryCertNumbers) {
				boolean hasComplimentaryCert = false;
				for(String certCriteria : complimentaryCertNumbers) {
					if(certCriteria.equals(currRequiredCriteria)) {
						hasComplimentaryCert = true;
					}
				}
				
				if(!hasComplimentaryCert) {
					errors.add("Certification criterion " + criterionNumber + "(*) was found "
							+ "so " + currRequiredCriteria + " is required but was not found.");
				}
			}
		}
		return errors;
	}
}
