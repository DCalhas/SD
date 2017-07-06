package org.komparator.mediator.ws.it;

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
import org.komparator.mediator.ws.*;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;

/**
 * Test suite
 */
public class SearchItemsIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		ProductView product = new ProductView();
		
		product.setId("R1");
		product.setDesc("Racket");
		product.setPrice(200);
		product.setQuantity(20);

		SupplierClient1.createProduct(product);

		product.setId("S1");
		product.setDesc("Soccer ball");
		product.setPrice(30);
		product.setQuantity(30);

		SupplierClient1.createProduct(product);

		
		product.setId("T1");
		product.setDesc("Tennis ball");
		product.setPrice(5);
		product.setQuantity(50);

		SupplierClient2.createProduct(product);	
		
		product.setId("T2");
		product.setDesc("Tennis ball");
		product.setPrice(10);
		product.setQuantity(25);

		SupplierClient2.createProduct(product);	
		
		product.setId("T3");
		product.setDesc("Tennis ball");
		product.setPrice(15);
		product.setQuantity(10);

		SupplierClient1.createProduct(product);	
	}

	@AfterClass
	public static void oneTimeTearDown() {
		mediatorClient.clear();
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

	@Test(expected = InvalidText_Exception.class)
	public void searchItemsNullInputTest() throws InvalidText_Exception {
		mediatorClient.searchItems(null);
	}

	@Test(expected = InvalidText_Exception.class)
	public void searchItemsEmptyIdTest() throws InvalidText_Exception {
		mediatorClient.searchItems("");
	}

	@Test(expected = InvalidText_Exception.class)
	public void searchItemsWhiteSpaceTest() throws InvalidText_Exception {
		mediatorClient.searchItems(" ");
	}

	@Test(expected = InvalidText_Exception.class)
	public void searchProductsTabTest() throws InvalidText_Exception {
		mediatorClient.searchItems("\t");
	}

	@Test(expected = InvalidText_Exception.class)
	public void searchProductsNewLineTest() throws InvalidText_Exception {
		mediatorClient.searchItems("\n");
	}
	
	@Test
	public void caseSensitiveTest() throws InvalidText_Exception {
		List<ItemView> result = mediatorClient.searchItems("ball");
		assertEquals(4, result.size());
		assertEquals("A06_Supplier1", result.get(0).getItemId().getSupplierId());
		assertEquals("A06_Supplier2", result.get(1).getItemId().getSupplierId());
		assertEquals("A06_Supplier2", result.get(2).getItemId().getSupplierId());
		assertEquals("A06_Supplier1", result.get(3).getItemId().getSupplierId());
		result = mediatorClient.searchItems("Ball");
		assertEquals(0, result.size());
	}
	
	@Test
	public void itemContainsDescriptionTest() throws InvalidText_Exception {
		List<ItemView> result = mediatorClient.searchItems("ball");
		assertEquals(4, result.size());
		assertEquals("A06_Supplier1", result.get(0).getItemId().getSupplierId());
		assertEquals("A06_Supplier2", result.get(1).getItemId().getSupplierId());
		assertEquals("A06_Supplier2", result.get(2).getItemId().getSupplierId());
		assertEquals("A06_Supplier1", result.get(3).getItemId().getSupplierId());

	}
	
	@Test
	public void itemMatchesDescriptionTest() throws InvalidText_Exception {
		List<ItemView> result = mediatorClient.searchItems("Soccer ball");
		System.out.println(result.get(0).getItemId().getSupplierId());
		assertEquals(1, result.size());
		assertEquals("A06_Supplier1", result.get(0).getItemId().getSupplierId());
		assertEquals("S1", result.get(0).getItemId().getProductId());
	}
	
	@Test
	public void itemSortedByIdPriceTest() throws InvalidText_Exception {
		List<ItemView> result = mediatorClient.searchItems("Tennis ball");
		assertEquals(3, result.size());
		assertEquals("T1", result.get(0).getItemId().getProductId());
		assertEquals("T2", result.get(1).getItemId().getProductId());
		assertEquals("T3", result.get(2).getItemId().getProductId());
		assertEquals("A06_Supplier2", result.get(0).getItemId().getSupplierId());
		assertEquals("A06_Supplier2", result.get(1).getItemId().getSupplierId());
		assertEquals("A06_Supplier1", result.get(2).getItemId().getSupplierId());
	}
	
	@Test
	public void success() throws InvalidText_Exception {
		List<ItemView> result = mediatorClient.searchItems("a");
		assertEquals(5, result.size());
		assertEquals("R1", result.get(0).getItemId().getProductId());
		assertEquals("S1", result.get(1).getItemId().getProductId());
		assertEquals("T1", result.get(2).getItemId().getProductId());
		assertEquals("T2", result.get(3).getItemId().getProductId());
		assertEquals("T3", result.get(4).getItemId().getProductId());
		result = mediatorClient.searchItems("Tennis ball");
		System.out.println(result);
		assertEquals(3, result.size());
		assertEquals("A06_Supplier2", result.get(0).getItemId().getSupplierId());
		assertEquals("A06_Supplier2", result.get(1).getItemId().getSupplierId());
		assertEquals("A06_Supplier1", result.get(2).getItemId().getSupplierId());
		result = mediatorClient.searchItems("Golf Ball");
		assertEquals(0, result.size());
	}

	
	// main tests

	// TODO

}
