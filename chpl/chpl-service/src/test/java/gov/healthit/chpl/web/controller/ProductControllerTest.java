package gov.healthit.chpl.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.healthit.chpl.auth.permission.GrantedPermission;
import gov.healthit.chpl.auth.user.JWTAuthenticatedUser;
import gov.healthit.chpl.caching.UnitTestRules;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.domain.Product;
import gov.healthit.chpl.domain.UpdateProductsRequest;
import gov.healthit.chpl.web.controller.exception.ValidationException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { gov.healthit.chpl.CHPLTestConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:data/testData.xml") 
public class ProductControllerTest {
	@Autowired ProductController productController;
	@Rule
    @Autowired
    public UnitTestRules cacheInvalidationRule;
	
	private static JWTAuthenticatedUser adminUser;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		adminUser = new JWTAuthenticatedUser();
		adminUser.setFirstName("Administrator");
		adminUser.setId(-2L);
		adminUser.setLastName("Administrator");
		adminUser.setSubjectName("admin");
		adminUser.getPermissions().add(new GrantedPermission("ROLE_ADMIN"));
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void getProductById() {
		Product result = null;
		try {
			result = productController.getProductById(-1L);
		} catch(EntityRetrievalException e) {
			fail(e.getMessage());
		}
		assertNotNull(result);
		assertNotNull(result.getOwner());
		assertNotNull(result.getOwner().getDeveloperId());
		assertEquals(-1, result.getOwner().getDeveloperId().longValue());
		assertNotNull(result.getOwnerHistory());
		assertEquals(1, result.getOwnerHistory().size());
		assertEquals(-2, result.getOwnerHistory().get(0).getDeveloper().getDeveloperId().longValue());
	}
	
	@Transactional
	@Test(expected=EntityRetrievalException.class)
	public void testGetProductByBadId() 
		throws EntityRetrievalException, IOException, ValidationException {
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		productController.getProductById(-100L);
	}
	
	@Transactional
	@Test(expected=EntityRetrievalException.class)
	public void testGetListingForProductByBadId() 
		throws EntityRetrievalException, IOException, ValidationException {
		SecurityContextHolder.getContext().setAuthentication(adminUser);
		productController.getListingsForProduct(-100L);
	}
	
	@Transactional
    @Test(expected=ValidationException.class)
	@Rollback(true)
    public void testUpdateDuplicateChplProductNumberValidationException() throws JsonProcessingException, EntityCreationException, EntityRetrievalException, InvalidArgumentsException, ValidationException {
        SecurityContextHolder.getContext().setAuthentication(adminUser);
        List<Long> productIds = new ArrayList<Long>();
        productIds.add(-4L);
        
        UpdateProductsRequest request = new UpdateProductsRequest();
        request.setNewDeveloperId(-1L);
        request.setProductIds(productIds);
        
        productController.updateProduct(request);
    }
}