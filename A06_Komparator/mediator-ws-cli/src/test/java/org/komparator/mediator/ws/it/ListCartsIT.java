package org.komparator.mediator.ws.it;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.supplier.ws.*;


/**
 * Test suite
 */
public class ListCartsIT extends BaseIT {
	
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		
		ProductView product = new ProductView();
		
		product.setId("R1");
		product.setDesc("Racket");
		product.setPrice(200);
		product.setQuantity(20);

		SupplierClient1.createProduct(product);
	}

    @Test
    public void ListCartsEmptyTest() {
        assertTrue(mediatorClient.listCarts().isEmpty());
        
    }
    
    @Test
    public void ListCartsNotEmptyTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception{
    	
    	ItemIdView itemId = new ItemIdView();
    	itemId.setProductId("R1");
		itemId.setSupplierId("A06_Supplier1");
		
    	mediatorClient.addToCart("Cart1", itemId, 1);
    	assertNotNull(mediatorClient.listCarts());
    }
    
    @AfterClass
	public static void oneTimeTearDown() {
		mediatorClient.clear();
	}
}
