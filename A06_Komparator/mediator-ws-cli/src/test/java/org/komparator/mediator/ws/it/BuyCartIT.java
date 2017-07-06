package org.komparator.mediator.ws.it;



import static org.junit.Assert.assertEquals;

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


/**
 * Test suite
 */
public class BuyCartIT extends BaseIT {
	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
			
	}

	@AfterClass
	public static void oneTimeTearDown() {
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
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
		
		product.setId("T1");
		product.setDesc("Golf ball");
		product.setPrice(5);
		product.setQuantity(5);

		SupplierClient3.createProduct(product);
		
		ItemIdView idView1 = new ItemIdView();
		ItemIdView idView2 = new ItemIdView();
		ItemIdView idView3 = new ItemIdView();
		
		idView1.setProductId("X1");
		idView1.setSupplierId("A06_Supplier1");
		
		idView2.setProductId("Y2");
		idView2.setSupplierId("A06_Supplier2");
		
		idView3.setProductId("T1");
		idView3.setSupplierId("A06_Supplier3");
		
		mediatorClient.addToCart("A1", idView1, 10);
		mediatorClient.addToCart("A2", idView1, 10);
		mediatorClient.addToCart("A3", idView1, 5);
		mediatorClient.addToCart("A4", idView1, 10);
		mediatorClient.addToCart("A4", idView2, 5);
		mediatorClient.addToCart("A5", idView1, 10);
		mediatorClient.addToCart("A5", idView2, 20);
		mediatorClient.addToCart("A5", idView3, 5);
		mediatorClient.addToCart("A6", idView2, 5);
		
		
		mediatorClient.addToCart("A7", idView1, 1);
		mediatorClient.addToCart("A8", idView1, 1);
		mediatorClient.addToCart("A9", idView1, 1);
		mediatorClient.addToCart("A10", idView1, 1);
		mediatorClient.addToCart("A11", idView1, 1);
		mediatorClient.addToCart("A12", idView1, 1);
		mediatorClient.addToCart("A13", idView1, 1);
		mediatorClient.addToCart("A14", idView1, 1);
		mediatorClient.addToCart("A15", idView1, 1);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartNullCartIdInputTest() throws InvalidText_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart(null, "4024007102923926");
	}
	
	@Test(expected = InvalidCreditCard_Exception.class)
	public void buyCartNullCreditCardNrInputTest() throws InvalidText_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("A1", null);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartEmptyCartIdTest() throws InvalidText_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("", "4024007102923926");
	}
	
	@Test(expected = InvalidCreditCard_Exception.class)
	public void buyCartEmptyCreditCardNrTest() throws InvalidText_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("A1", "");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartWhiteSpaceCardIdTest() throws InvalidText_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart(" ", "4024007102923926");
	}
	
	@Test(expected = InvalidCreditCard_Exception.class)
	public void buyCartWhiteSpaceCreditCardNrTest() throws InvalidText_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("A1", " ");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartTabCartIdTest() throws InvalidText_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("\t", "4024007102923926");
	}
	
	@Test(expected = InvalidCreditCard_Exception.class)
	public void buyCartTabCreditCardNrTest() throws InvalidText_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("A1", "\t");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartNewLineCartIdTest() throws InvalidText_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("\n", "4024007102923926");
	}
	
	@Test(expected = InvalidCreditCard_Exception.class)
	public void buyCartNewLineCreditCardNrTest() throws InvalidText_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("A1", "\n");
	}
	
	
	@Test
	public void success() throws InvalidText_Exception, EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		ShoppingResultView shoppingResult = mediatorClient.buyCart("A1", "4024007102923926");
		
		
		assertEquals(Result.COMPLETE, shoppingResult.getResult());
		assertEquals(100, shoppingResult.getTotalPrice());
		assertEquals(0, shoppingResult.getDroppedItems().size());
		assertEquals(1, shoppingResult.getPurchasedItems().size());
	}
	
	
	@Test(expected=InvalidCartId_Exception.class)
	public void inexistingCart() throws EmptyCart_Exception, InvalidCreditCard_Exception, InvalidCartId_Exception {
		mediatorClient.buyCart("A20", "4024007102923926");
	}
	
	@Test(expected=InvalidCartId_Exception.class)
	public void sameCartTwice() throws EmptyCart_Exception, InvalidCreditCard_Exception, InvalidCartId_Exception {
		mediatorClient.buyCart("A1", "4024007102923926");
		mediatorClient.buyCart("A1", "4024007102923926");
	}
	
	@Test
	public void emptyPurchase() throws EmptyCart_Exception, InvalidCreditCard_Exception, InvalidCartId_Exception {
		//TODO: need to throw EmptyCart but can not create an empty Cart 
		ShoppingResultView shoppingResult1 = mediatorClient.buyCart("A1", "4024007102923926");
		ShoppingResultView shoppingResult2 = mediatorClient.buyCart("A2", "4024007102923926");
		
		assertEquals(Result.COMPLETE, shoppingResult1.getResult());
		assertEquals(100, shoppingResult1.getTotalPrice());
		assertEquals(0, shoppingResult1.getDroppedItems().size());
		assertEquals(1, shoppingResult1.getPurchasedItems().size());
		
		assertEquals(Result.EMPTY, shoppingResult2.getResult());
		assertEquals(0, shoppingResult2.getTotalPrice());
		assertEquals(1, shoppingResult2.getDroppedItems().size());
		assertEquals(0, shoppingResult2.getPurchasedItems().size());
	}
	
	
	@Test
	public void partialPurchaseTwoCarts() throws EmptyCart_Exception, InvalidCreditCard_Exception, InvalidCartId_Exception {
		//TODO: need to throw EmptyCart but can not create an empty Cart 
		ShoppingResultView shoppingResult1 = mediatorClient.buyCart("A3", "4024007102923926");
		ShoppingResultView shoppingResult2 = mediatorClient.buyCart("A4", "4024007102923926");
		
		assertEquals(Result.COMPLETE, shoppingResult1.getResult());
		assertEquals(50, shoppingResult1.getTotalPrice());
		assertEquals(0, shoppingResult1.getDroppedItems().size());
		assertEquals(1, shoppingResult1.getPurchasedItems().size());
		
		assertEquals(Result.PARTIAL, shoppingResult2.getResult());
		assertEquals(100, shoppingResult2.getTotalPrice());
		assertEquals(1, shoppingResult2.getDroppedItems().size());
		assertEquals(1, shoppingResult2.getPurchasedItems().size());
	}
	
	@Test
	public void completePurchase() throws EmptyCart_Exception, InvalidCreditCard_Exception, InvalidCartId_Exception {
		//TODO: need to throw EmptyCart but can not create an empty Cart
		ShoppingResultView shoppingResult = mediatorClient.buyCart("A5", "4024007102923926");
		
		
		assertEquals(Result.COMPLETE, shoppingResult.getResult());
		assertEquals(525, shoppingResult.getTotalPrice());
		assertEquals(0, shoppingResult.getDroppedItems().size());
		assertEquals(3, shoppingResult.getPurchasedItems().size());
	}
	
	@After
	public void tearDown() {
		mediatorClient.clear();
	}

	

	
	// main tests

	// TODO

}
