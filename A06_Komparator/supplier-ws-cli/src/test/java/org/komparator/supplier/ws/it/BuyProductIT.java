package org.komparator.supplier.ws.it;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.*;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.cli.*;

/**
 * Test suite
 */
public class BuyProductIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
		client.clear();
	}

	@AfterClass
	public static void oneTimeTearDown() {
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() {
		ProductView product = new ProductView();
		product.setId("X1");
		product.setDesc("Basketball");
		product.setPrice(10);
		product.setQuantity(1);
		try {
			client.createProduct(product);
		} catch(BadProductId_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		} catch(BadProduct_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		}

		product.setId("W2");
		product.setDesc("Tennis");
		product.setPrice(5);
		product.setQuantity(6);
		try {
			client.createProduct(product);
		} catch(BadProductId_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		} catch(BadProduct_Exception e) { System.out.println("BadProductId_Exception thrown: " + e.getMessage()); 
		}
	}

	@After
	public void tearDown() {
		client.clear();
	}

	// tests
	// assertEquals(expected, actual);

	// public String buyProduct(String productId, int quantity)
	// throws BadProductId_Exception, BadQuantity_Exception,
	// InsufficientQuantity_Exception {

	// bad input tests

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNullInputTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(null, 1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductEmptyIdTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("", 1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductWhiteSpaceTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(" ", 1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductTabTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\t", 1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNewLineTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\n", 1);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductQuantityZeroTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1", 0);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductNegativeQuantityTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1", -1);
	}

	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductInsufficientTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("X1", 2);
	}
	
	@Test
	public void buyExistingProduct() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String result = client.buyProduct("W2", 1);
		assertNotNull(result);
		int count = 0;
		try {
			while(true){
				result = client.buyProduct("W2", 1);
				count++;
			}
		} catch (InsufficientQuantity_Exception e) {
			assertEquals(count, 5);
		}
	}
	
	@Test(expected = BadProductId_Exception.class)
	public void buyUnexistingProduct() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("W5", 2);
	}
	
	@Test
	public void success() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String result = "";
		result = client.buyProduct("X1", 1);
		assertNotNull(result);
		assertNotEquals("", result);
		assertTrue(result.matches("[A-Za-z0-9]+"));
	}

	// main tests

	// TODO
	
}
