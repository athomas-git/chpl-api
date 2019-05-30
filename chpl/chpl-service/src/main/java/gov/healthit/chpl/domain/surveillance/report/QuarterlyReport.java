package gov.healthit.chpl.domain.surveillance.report;

import java.io.Serializable;

import gov.healthit.chpl.domain.CertificationBody;
import gov.healthit.chpl.dto.surveillance.report.QuarterlyReportDTO;

public class QuarterlyReport implements Serializable {
    private static final long serialVersionUID = 8743838678379539305L;

    private Long id;
    private CertificationBody acb;
    private Integer year;
    private String quarter;
    private String reactiveSummary;
    private String prioritizedElementSummary;
    private String transparencyDisclosureSummary;

    public QuarterlyReport() {
    }

    public QuarterlyReport(final QuarterlyReportDTO dto) {
        this.id = dto.getId();
        this.reactiveSummary = dto.getReactiveSummary();
        this.prioritizedElementSummary = dto.getPrioritizedElementSummary();
        this.transparencyDisclosureSummary = dto.getTransparencyDisclosureSummary();
        if (dto.getQuarter() != null) {
            this.quarter = dto.getQuarter().getName();
        }
        if (dto.getAnnualReport() != null) {
            this.year = dto.getAnnualReport().getYear();
            if (dto.getAnnualReport().getAcb() != null) {
                this.acb = new CertificationBody(dto.getAnnualReport().getAcb());
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public CertificationBody getAcb() {
        return acb;
    }

    public void setAcb(final CertificationBody acb) {
        this.acb = acb;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(final Integer year) {
        this.year = year;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(final String quarter) {
        this.quarter = quarter;
    }

    public String getReactiveSummary() {
        return reactiveSummary;
    }

    public void setReactiveSummary(final String reactiveSummary) {
        this.reactiveSummary = reactiveSummary;
    }

    public String getPrioritizedElementSummary() {
        return prioritizedElementSummary;
    }

    public void setPrioritizedElementSummary(final String prioritizedElementSummary) {
        this.prioritizedElementSummary = prioritizedElementSummary;
    }

    public String getTransparencyDisclosureSummary() {
        return transparencyDisclosureSummary;
    }

    public void setTransparencyDisclosureSummary(final String transparencyDisclosureSummary) {
        this.transparencyDisclosureSummary = transparencyDisclosureSummary;
    }
}
