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

import junit.framework.Assert;


public class AddToCartIT extends BaseIT {
	
	private ItemIdView idView;
	
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception{
		
		ProductView product = new ProductView();
		product.setId("X1");
		product.setDesc("Basketball");
		product.setPrice(10);
		product.setQuantity(10);
		
		SupplierClient1.createProduct(product);


		product.setId("Y2");
		product.setDesc("Baseball");
		product.setPrice(20);
		product.setQuantity(20);

		SupplierClient2.createProduct(product);

		product.setId("Z3");
		product.setDesc("Soccer ball");
		product.setPrice(30);
		product.setQuantity(30);

		SupplierClient1.createProduct(product);

		
		product.setId("W4");
		product.setDesc("Tennis ball");
		product.setPrice(5);
		product.setQuantity(5);

		SupplierClient2.createProduct(product);
		
		ItemView itemView = new ItemView();
		idView = new ItemIdView();
		
		idView.setProductId("X1");
		idView.setSupplierId("A06_Supplier1");
		itemView.setItemId(this.idView);
		
		
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void AddToCartEmptyCartId() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("", this.idView, 10);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void AddToCartNullCartId() throws InvalidText_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart(null, this.idView, 10);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void AddToCartBlankCartId() throws InvalidText_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("   ", this.idView, 10);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void AddToCartTabCartId() throws InvalidText_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("\t", this.idView, 10);
	}
	
	@Test(expected = InvalidCartId_Exception.class)
	public void AddToCartNewLineCartId() throws InvalidText_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("\n", this.idView, 10);
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void AddToCartNullIdView() throws InvalidText_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("A1", null, 10);
	}
	
	@Test(expected = InvalidQuantity_Exception.class)
	public void AddToCartZeroQuantity() throws InvalidText_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("A1", this.idView, 0);
	}
	
	@Test(expected = InvalidQuantity_Exception.class)
	public void AddToCartNegativeQuantity() throws InvalidText_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("A1", this.idView, -100);
	}
	
	@Test(expected = NotEnoughItems_Exception.class)
	public void AddToCartQuantityTooHigh() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("A1", this.idView, 11);
	}
	
	@Test(expected = NotEnoughItems_Exception.class)
	public void AddToCartQuantityTooHigh2Steps() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("A1", this.idView, 10);
		mediatorClient.addToCart("A1", this.idView, 1);
	}
	

	
	@Test
	public void AddToCartPositiveQuantity() throws InvalidText_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("A1", this.idView, 1);
	}
	
	@Test
	public void AddToCartCartIdDoesNotExist() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("A1", this.idView, 1);
		mediatorClient.addToCart("A2", this.idView, 2);
		
		assertEquals(2, mediatorClient.listCarts().size());
		assertEquals(1, mediatorClient.listCarts().get(0).getItems().get(0).getQuantity());
		assertEquals(2, mediatorClient.listCarts().get(1).getItems().get(0).getQuantity());
	}
	
	@Test
	public void AddToCartItemIdExists() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("A1", this.idView, 1);
		
		assertEquals(1, mediatorClient.listCarts().size());
		assertEquals(1, mediatorClient.listCarts().get(0).getItems().get(0).getQuantity());
	
		mediatorClient.addToCart("A1", this.idView, 1);
		
		assertEquals(1, mediatorClient.listCarts().size());
		assertEquals(2, mediatorClient.listCarts().get(0).getItems().get(0).getQuantity());
	}
	
	@Test
	public void AddToCart4Items() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
		mediatorClient.addToCart("A1", this.idView, 5);
		
		ItemView itemView = new ItemView();
		itemView.setItemId(this.idView);
		idView.setProductId("Y2");
		idView.setSupplierId("A06_Supplier2");
		mediatorClient.addToCart("A1", this.idView, 5);
		
		idView.setProductId("Z3");
		idView.setSupplierId("A06_Supplier1");
		mediatorClient.addToCart("A1", this.idView, 5);
		
		idView.setProductId("W4");
		idView.setSupplierId("A06_Supplier2");
		mediatorClient.addToCart("A1", this.idView, 5);
		
		assertEquals(4, mediatorClient.listCarts().get(0).getItems().size());
	}
	
	@After
	public void tearDown() {
		mediatorClient.clear();
	}
	
	
}
