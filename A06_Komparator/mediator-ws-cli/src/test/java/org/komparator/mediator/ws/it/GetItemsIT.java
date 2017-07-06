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
public class GetItemsIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@Before
	public void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		ProductView product = new ProductView();
		product.setId("X1");
		product.setDesc("Basketball");
		product.setPrice(10);
		product.setQuantity(10);
		
		SupplierClient1.createProduct(product);


		product.setId("X1");
		product.setDesc("Baseball");
		product.setPrice(20);
		product.setQuantity(10);

		SupplierClient1.createProduct(product);
		
		product.setId("X1");
		product.setDesc("Baseball");
		product.setPrice(10);
		product.setQuantity(10);

		SupplierClient2.createProduct(product);
		
		product.setId("X1");
		product.setDesc("Baseball");
		product.setPrice(5);
		product.setQuantity(10);

		SupplierClient3.createProduct(product);
		
		product.setId("X1");
		product.setDesc("Baseball");
		product.setPrice(30);
		product.setQuantity(10);

		SupplierClient3.createProduct(product);

		
		product.setId("W4");
		product.setDesc("Tennis ball");
		product.setPrice(5);
		product.setQuantity(5);

		SupplierClient2.createProduct(product);	
	}

	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsNullTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(null);
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsEmptyTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsBlankTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(" ");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsTabTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\t");
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsNewLineTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\n");
	}
	
	@Test
	public void success() throws InvalidItemId_Exception, InvalidText_Exception, BadProductId_Exception {
		List<ItemView> result = null;
		result = mediatorClient.getItems("W4");
		
		assertEquals("W4", result.get(0).getItemId().getProductId());
		assertEquals("A06_Supplier2", result.get(0).getItemId().getSupplierId());
		assertEquals(5, result.get(0).getPrice());
		assertEquals("Tennis ball", result.get(0).getDesc());
		
		assertEquals(1, result.size());
	}
	
	
	@After
	public void oneTimeTearDown() {
		mediatorClient.clear();
	}

	// TODO

}
