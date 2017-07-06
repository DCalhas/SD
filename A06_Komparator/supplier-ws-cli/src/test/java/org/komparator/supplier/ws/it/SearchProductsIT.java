package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.*;

/**
 * Test suite
 */
public class SearchProductsIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
		ProductView product = new ProductView();
		product.setId("X1");
		product.setDesc("Basketball");
		product.setPrice(10);
		product.setQuantity(10);
		try {
			client.createProduct(product);
		} catch(BadProductId_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		} catch(BadProduct_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		}


		product.setId("Y2");
		product.setDesc("Baseball");
		product.setPrice(20);
		product.setQuantity(20);
		try {
			client.createProduct(product);
		} catch(BadProductId_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		} catch(BadProduct_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		}
		

		product.setId("Z3");
		product.setDesc("Soccer ball");
		product.setPrice(30);
		product.setQuantity(30);
		try {
			client.createProduct(product);
		} catch(BadProductId_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		} catch(BadProduct_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		}
		
		product.setId("W4");
		product.setDesc("Tennis ball");
		product.setPrice(5);
		product.setQuantity(5);
		try {
			client.createProduct(product);
		} catch(BadProductId_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		} catch(BadProduct_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		}
		
		product.setId("W5");
		product.setDesc("Tennis ball");
		product.setPrice(6);
		product.setQuantity(2);
		try {
			client.createProduct(product);
		} catch(BadProductId_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		} catch(BadProduct_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {
		client.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	// tests
	// assertEquals(expected, actual);

	// public List<ProductView> searchProducts(String descText) throws
	// BadText_Exception

	// bad input tests

	@Test(expected = BadText_Exception.class)
	public void searchProductsNullInputTest() throws BadText_Exception {
		List<ProductView> result = client.searchProducts(null);
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsEmptyIdTest() throws BadText_Exception {
		List<ProductView> result = client.searchProducts("");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsWhiteSpaceTest() throws BadText_Exception {
		List<ProductView> result = client.searchProducts(" ");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsTabTest() throws BadText_Exception {
		List<ProductView> result = client.searchProducts("\t");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsNewLineTest() throws BadText_Exception {
		List<ProductView> result = client.searchProducts("\n");
	}
	
	@Test
	public void success() throws BadText_Exception {
		List<ProductView> result = client.searchProducts("Tennis ball");
		assertEquals(result.size(), 2);
		result = client.searchProducts("Golf Ball");
		assertEquals(result.size(), 0);
	}

	
	// main tests

	// TODO

}
