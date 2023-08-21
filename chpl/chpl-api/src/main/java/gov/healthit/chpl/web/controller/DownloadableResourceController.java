package gov.healthit.chpl.web.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.ff4j.FF4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.healthit.chpl.FeatureList;
import gov.healthit.chpl.compliance.surveillance.SurveillanceManager;
import gov.healthit.chpl.exception.EntityRetrievalException;
import gov.healthit.chpl.svap.manager.SvapManager;
import gov.healthit.chpl.util.ErrorMessageUtil;
import gov.healthit.chpl.util.FileUtils;
import gov.healthit.chpl.util.SwaggerSecurityRequirement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@Tag(name = "downloadable-resources", description = "Provides access to files generated by CHPL.")
@RestController
@Log4j2
public class DownloadableResourceController {
    private Environment env;
    private ErrorMessageUtil msgUtil;
    private SurveillanceManager survManager;
    private SvapManager svapManager;
    private FileUtils fileUtils;
    private FF4j ff4j;

    @Value("${directReviewsReportName}")
    private String directReviewsReportName;

    @Value("${schemaDirectReviewsName}")
    private String directReviewsSchemaName;

    @Autowired
    public DownloadableResourceController(Environment env,
            ErrorMessageUtil msgUtil,
            SurveillanceManager survManager,
            SvapManager svapManager,
            FileUtils fileUtils,
            FF4j ff4j) {
        this.env = env;
        this.msgUtil = msgUtil;
        this.survManager = survManager;
        this.svapManager = svapManager;
        this.fileUtils = fileUtils;
        this.ff4j = ff4j;
    }

    @Operation(summary = "Download the entire CHPL as XML.",
            description = "Once per day, the entire certified product listing is "
                    + "written out to XML files on the CHPL servers, one for each "
                    + "certification edition. This method allows any user to download "
                    + "that XML file. It is formatted in such a way that users may import "
                    + "it into Microsoft Excel or any other XML tool of their choosing. To download "
                    + "any one of the XML files, append ‘&edition=year’ to the end of the query string "
                    + "(e.g., &edition=2015). A separate query is required to download each of the XML files.",
            security = {
                    @SecurityRequirement(name = SwaggerSecurityRequirement.API_KEY)
            })
    @RequestMapping(value = "/download", method = RequestMethod.GET, produces = "application/xml")
    public void downloadListingDetails(@RequestParam(value = "edition", required = false) String editionInput,
            @RequestParam(value = "format", defaultValue = "xml", required = false) String formatInput,
            @RequestParam(value = "definition", defaultValue = "false", required = false) Boolean isDefinition,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        // parse inputs
        String edition = editionInput;
        String format = formatInput;
        String responseType = "text/csv";

        if (!StringUtils.isEmpty(edition)) {
            // make sure it's a 4 character year
            edition = edition.trim();
            if (!edition.startsWith("20")) {
                edition = "20" + edition;
            }
        } else {
            edition = "all";
        }

        if (!StringUtils.isEmpty(format) && format.equalsIgnoreCase("csv")) {
            format = "csv";
        } else if (!StringUtils.isEmpty(format) && format.equalsIgnoreCase("xml")) {
            format = "xml";
            responseType = "application/xml";
        } else {
            format = "json";
            responseType = "application/json";
        }

        File toDownload = null;
        // if the user wants a definition file, find it
        if (isDefinition != null && isDefinition.booleanValue()) {
            if (format.equals("xml")) {
                toDownload = fileUtils.getDownloadFile(env.getProperty("schemaXmlName"));
            } else if (edition.equals("2014")) {
                toDownload = fileUtils.getDownloadFile(env.getProperty("schemaCsv2014Name"));
            } else if (edition.equals("2015")) {
                if (ff4j.check(FeatureList.ERD_PHASE_3)) {
                    toDownload = fileUtils.getDownloadFile(env.getProperty("schemaCsv2015Name.postErdPhase3"));
                } else {
                    toDownload = fileUtils.getDownloadFile(env.getProperty("schemaCsv2015Name"));
                }
            }

            if (!toDownload.exists()) {
                response.getWriter()
                        .write(msgUtil.getMessage("resources.schemaFileNotFound", toDownload.getAbsolutePath()));
                return;
            }
        } else {
            File newestFileWithFormat = fileUtils.getNewestFileMatchingName("^chpl-" + edition + "-.+\\." + format + "$");
            if (newestFileWithFormat != null) {
                toDownload = newestFileWithFormat;
            } else {
                response.getWriter()
                        .write(msgUtil.getMessage("resources.fileWithEditionAndFormatNotFound", edition, format));
                return;
            }
        }

        LOGGER.info("Downloading " + toDownload.getName());
        fileUtils.streamFileAsResponse(toDownload, responseType, response);
    }

    @Operation(summary = "Download a summary of SVAP activity as a CSV.",
            description = "Once per day, a summary of SVAP activity is written out to a CSV "
                    + "file on the CHPL servers. This method allows any user to download that file.",
            security = {
                    @SecurityRequirement(name = SwaggerSecurityRequirement.API_KEY),
                    @SecurityRequirement(name = SwaggerSecurityRequirement.BEARER)
            })
    @RequestMapping(value = "/svap/download", method = RequestMethod.GET, produces = "text/csv")
    public void downloadSvapSummary(
            @RequestParam(value = "definition", defaultValue = "false", required = false) Boolean isDefinition,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        File downloadFile = null;
        if (isDefinition != null && isDefinition.booleanValue()) {
            try {
                downloadFile = svapManager.getSvapSummaryDefinitionFile();
            } catch (IOException ex) {
                response.getWriter().append(ex.getMessage());
                return;
            }
        } else {
            try {
                downloadFile = svapManager.getSvapSummaryFile();
            } catch (IOException ex) {
                response.getWriter().append(ex.getMessage());
                return;
            }
        }

        if (downloadFile == null) {
            response.getWriter().append(msgUtil.getMessage("resources.schemaFileGeneralError"));
            return;
        }
        if (!downloadFile.exists()) {
            response.getWriter().append(msgUtil.getMessage("resources.schemaFileNotFound", downloadFile.getAbsolutePath()));
            return;
        }

        LOGGER.info("Streaming " + downloadFile.getName());
        fileUtils.streamFileAsResponse(downloadFile, "text/csv", response);
    }

    @Operation(summary = "Download all SED details for Listings that are certified to 170.315(g)(3).",
            description = "Download a specific file that is generated overnight.",
            security = {
                    @SecurityRequirement(name = SwaggerSecurityRequirement.API_KEY)
            })
    @RequestMapping(value = "/certified_products/sed_details", method = RequestMethod.GET)
    public void streamSEDDetailsDocumentContents(HttpServletResponse response)
            throws EntityRetrievalException, IOException {
        File downloadFile = fileUtils.getNewestFileMatchingName("^" + env.getProperty("SEDDownloadName") + "-.+\\.csv$");
        fileUtils.streamFileAsResponse(downloadFile, "text/csv", response);
    }

    @Operation(summary = "Download all direct reviews as a CSV.",
            description = "Once per day, all direct reviews are written out to a CSV "
                    + "file on the CHPL servers. This method allows any user to download that file.",
            security = {
                    @SecurityRequirement(name = SwaggerSecurityRequirement.API_KEY)
            })
    @RequestMapping(value = "/developers/direct-reviews/download", method = RequestMethod.GET, produces = "text/csv")
    public void downloadDirectReviews(
            @RequestParam(value = "definition", defaultValue = "false", required = false) Boolean isDefinition,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        File downloadFile = null;
        if (isDefinition != null && isDefinition.booleanValue()) {
            try {
                downloadFile = fileUtils.getDownloadFile(directReviewsSchemaName);
            } catch (IOException ex) {
                response.getWriter().append(ex.getMessage());
                return;
            }
        } else {
            try {
                downloadFile = fileUtils.getNewestFileMatchingName("^" + directReviewsReportName + "-.+\\.csv$");
            } catch (IOException ex) {
                response.getWriter().append(ex.getMessage());
                return;
            }
        }

        if (downloadFile == null) {
            response.getWriter().append(msgUtil.getMessage("resources.schemaFileGeneralError"));
            return;
        }
        if (!downloadFile.exists()) {
            response.getWriter().append(msgUtil.getMessage("resources.schemaFileNotFound", downloadFile.getAbsolutePath()));
            return;
        }

        LOGGER.info("Streaming " + downloadFile.getName());
        fileUtils.streamFileAsResponse(downloadFile, "text/csv", response);
    }

    @Operation(summary = "Download surveillance as CSV.",
            description = "Once per day, all surveillance and nonconformities are written out to CSV "
                    + "files on the CHPL servers. This method allows any user to download those files.",
            security = {
                    @SecurityRequirement(name = SwaggerSecurityRequirement.API_KEY),
                    @SecurityRequirement(name = SwaggerSecurityRequirement.BEARER)
            })
    @RequestMapping(value = "/surveillance/download", method = RequestMethod.GET, produces = "text/csv")
    public void downloadSurveillance(@RequestParam(value = "type", required = false, defaultValue = "") final String type,
            @RequestParam(value = "definition", defaultValue = "false", required = false) final Boolean isDefinition,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, EntityRetrievalException {

        File downloadFile = null;
        if (isDefinition != null && isDefinition.booleanValue()) {
            if (type.equalsIgnoreCase("basic")) {
                downloadFile = survManager.getBasicReportDownloadDefinitionFile();
            } else {
                downloadFile = fileUtils.getDownloadFile(env.getProperty("schemaSurveillanceName"));
            }
        } else {
            try {
                if (type.equalsIgnoreCase("all")) {
                    downloadFile = survManager.getAllSurveillanceDownloadFile();
                } else if (type.equalsIgnoreCase("basic")) {
                    downloadFile = survManager.getBasicReportDownloadFile();
                } else {
                    downloadFile = survManager.getSurveillanceWithNonconformitiesDownloadFile();
                }
            } catch (final IOException ex) {
                response.getWriter().append(ex.getMessage());
                return;
            }
        }

        if (downloadFile == null) {
            response.getWriter()
                    .append(msgUtil.getMessage("resources.schemaFileGeneralError"));
            return;
        }
        if (!downloadFile.exists()) {
            response.getWriter()
                    .write(msgUtil.getMessage("resources.schemaFileNotFound", downloadFile.getAbsolutePath()));
            return;
        }

        LOGGER.info("Downloading " + downloadFile.getName());
        fileUtils.streamFileAsResponse(downloadFile, "text/csv", response);
    }
}
