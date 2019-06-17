package gov.healthit.chpl.manager.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.healthit.chpl.auth.permission.GrantedPermission;
import gov.healthit.chpl.auth.user.JWTAuthenticatedUser;
import gov.healthit.chpl.caching.UnitTestRules;
import gov.healthit.chpl.domain.activity.ActivityCategory;
import gov.healthit.chpl.domain.activity.ActivityMetadata;
import gov.healthit.chpl.dto.CertificationBodyDTO;
import gov.healthit.chpl.exception.EntityCreationException;
import gov.healthit.chpl.exception.EntityRetrievalException;
import gov.healthit.chpl.exception.InvalidArgumentsException;
import gov.healthit.chpl.exception.ValidationException;
import gov.healthit.chpl.manager.ActivityMetadataManager;
import gov.healthit.chpl.manager.CertificationBodyManager;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        gov.healthit.chpl.CHPLTestConfig.class
})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class
})
@DatabaseSetup("classpath:data/testData.xml")
public class CertificationBodyActivityMetadataTest extends TestCase {
    @Autowired
    private ActivityMetadataManager metadataManager;

    @Autowired
    private CertificationBodyManager acbManager;

    private static JWTAuthenticatedUser adminUser, acbUser, atlUser;

    @Rule
    @Autowired
    public UnitTestRules cacheInvalidationRule;

    @BeforeClass
    public static void setUpClass() throws Exception {
        adminUser = new JWTAuthenticatedUser();
        adminUser.setFullName("Administrator");
        adminUser.setId(-2L);
        adminUser.setFriendlyName("Administrator");
        adminUser.setSubjectName("admin");
        adminUser.getPermissions().add(new GrantedPermission("ROLE_ADMIN"));

        acbUser = new JWTAuthenticatedUser();
        acbUser.setFullName("Test");
        acbUser.setId(3L);
        acbUser.setFriendlyName("User3");
        acbUser.setSubjectName("testUser3");
        acbUser.getPermissions().add(new GrantedPermission("ROLE_ACB"));

        atlUser = new JWTAuthenticatedUser();
        atlUser.setFullName("ATL");
        atlUser.setId(4L);
        atlUser.setFriendlyName("User");
        atlUser.setSubjectName("atlUser");
        atlUser.getPermissions().add(new GrantedPermission("ROLE_ATL"));
    }

    @Test
    @Rollback(true)
    @Transactional
    public void testModifyAcbName_GetActivityAsAdmin_Allowed()
        throws EntityRetrievalException, ValidationException, IOException,
        InvalidArgumentsException, EntityCreationException {
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        Long acbId = -1L;
        CertificationBodyDTO acb = acbManager.getById(acbId);
        acb.setWebsite("http://www.new-website.com");

        try {
            acbManager.update(acb);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Could not perform ACB name update: " + ex.getMessage());
        }

        Calendar start = getBeginningOfToday();
        Calendar end = getEndOfToday();
        List<ActivityMetadata> metadatas = metadataManager.getCertificationBodyActivityMetadata(
                start.getTime(), end.getTime());
        assertEquals(1, metadatas.size());
        ActivityMetadata metadata = metadatas.get(0);
        assertEquals(acbId.longValue(), metadata.getObjectId().longValue());
        assertTrue(metadata.getCategories().contains(ActivityCategory.CERTIFICATION_BODY));
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    @Rollback(true)
    @Transactional
    public void testModifyAcbName_GetActivityAsAcbWithAccess_Allowed()
        throws EntityRetrievalException, ValidationException, IOException,
        InvalidArgumentsException, EntityCreationException {
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        Long acbId = -1L;
        CertificationBodyDTO acb = acbManager.getById(acbId);
        acb.setWebsite("http://www.new-website.com");

        try {
            acbManager.update(acb);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Could not perform ACB name update: " + ex.getMessage());
        }

        SecurityContextHolder.getContext().setAuthentication(acbUser);
        Calendar start = getBeginningOfToday();
        Calendar end = getEndOfToday();
        List<ActivityMetadata> metadatas =
                metadataManager.getCertificationBodyActivityMetadata(start.getTime(), end.getTime());
        assertEquals(1, metadatas.size());
        ActivityMetadata metadata = metadatas.get(0);
        assertEquals(acbId.longValue(), metadata.getObjectId().longValue());
        assertTrue(metadata.getCategories().contains(ActivityCategory.CERTIFICATION_BODY));
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    @Rollback(true)
    @Transactional
    public void testModifyAcbName_GetActivityAsAcbWithoutAccess_NoResults()
        throws EntityRetrievalException, ValidationException, IOException,
        InvalidArgumentsException, EntityCreationException {
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        Long acbId = -2L;
        CertificationBodyDTO acb = acbManager.getById(acbId);
        acb.setWebsite("http://www.new-website.com");

        try {
            acbManager.update(acb);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Could not perform ACB name update: " + ex.getMessage());
        }

        SecurityContextHolder.getContext().setAuthentication(acbUser);
        Calendar start = getBeginningOfToday();
        Calendar end = getEndOfToday();
        List<ActivityMetadata> results =
                metadataManager.getCertificationBodyActivityMetadata(start.getTime(), end.getTime());
        assertEquals(0, results.size());
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test(expected = AccessDeniedException.class)
    @Rollback(true)
    @Transactional
    public void testModifyAcbName_GetActivityAsAtl_NotAllowed()
        throws EntityRetrievalException, ValidationException, IOException,
        InvalidArgumentsException, EntityCreationException {
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        Long acbId = -1L;
        CertificationBodyDTO acb = acbManager.getById(acbId);
        acb.setWebsite("http://www.new-website.com");

        try {
            acbManager.update(acb);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Could not perform ACB name update: " + ex.getMessage());
        }

        SecurityContextHolder.getContext().setAuthentication(atlUser);
        Calendar start = getBeginningOfToday();
        Calendar end = getEndOfToday();
        metadataManager.getCertificationBodyActivityMetadata(start.getTime(), end.getTime());
    }

    @Test(expected = EntityRetrievalException.class)
    @Rollback(true)
    @Transactional
    public void testGetActivityForBadAcb_NotAllowed()
        throws EntityRetrievalException, ValidationException, IOException,
        InvalidArgumentsException, EntityCreationException {
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        Calendar start = getBeginningOfToday();
        Calendar end = getEndOfToday();
        metadataManager.getCertificationBodyActivityMetadata(
                100L, start.getTime(), end.getTime());
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private Calendar getBeginningOfToday() {
        Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        start.set(Calendar.HOUR, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        return start;
    }

    private Calendar getEndOfToday() {
        Calendar end = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        end.set(Calendar.HOUR, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        return end;
    }
}