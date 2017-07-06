package org.komparator.mediator.ws;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import org.komparator.mediator.ws.MediatorPortType;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;
import org.komparator.security.Manager;
import org.komparator.security.handler.MediatorOperationIdHandler;

// TODO annotate to bind with WSDL
// TODO implement port type interface

@WebService(
		endpointInterface = "org.komparator.mediator.ws.MediatorPortType", 
		wsdlLocation = "mediator.1_0.wsdl", 
		name = "MediatorWebService", 
		portName = "MediatorPort", 
		targetNamespace = "http://ws.mediator.komparator.org/", 
		serviceName = "MediatorService"
)
@HandlerChain(file = "/mediator-ws_handler-chain.xml")
public class MediatorPortImpl implements MediatorPortType {
	// end point manager
	private MediatorEndpointManager endpointManager;
	
	private Map<String, ShoppingResultView> buyCartResponses = new ConcurrentHashMap<String, ShoppingResultView>();
	
	private List<String> addCartResponses = new ArrayList<String>();
	
	private List<CartView> _cartList = new ArrayList<CartView>();
	
	private List<ShoppingResultView> _shoppingHistory = new ArrayList<ShoppingResultView>();
	
	private Integer purchase = new Integer(1);
	private boolean sleepFlag = true;

	@Resource
	private WebServiceContext webServiceContext;

	
	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}
	
	private class ItemViewComparator implements Comparator<ItemView> {
	    @Override
	    public int compare(ItemView o1, ItemView o2) {
	    	int result = String.CASE_INSENSITIVE_ORDER.compare(o1.getItemId().getProductId(), o2.getItemId().getProductId());
	    	if(result == 0) return o1.getPrice() - o2.getPrice();
	        return result;
	    }
	}
	
	private class ItemViewPriceComparator implements Comparator<ItemView> {
	    @Override
	    public int compare(ItemView o1, ItemView o2) {
	    	return o1.getPrice() - o2.getPrice();
	    }
	}
	
	private class ShoppingResultViewComparator implements Comparator<ShoppingResultView> {
	    @Override
	    public int compare(ShoppingResultView o1, ShoppingResultView o2) {
	    	String sub1 = o1.getId().substring(3, o1.getId().length()-1);
	    	String sub2 = o2.getId().substring(3, o2.getId().length()-1);
	    	int id1 = Integer.parseInt(sub1);
	    	int id2 = Integer.parseInt(sub2);
	    	return id1 - id2;
	    }
	}

	// Main operations -------------------------------------------------------
	
	
	
	
	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		// TODO Auto-generated method stub
		
		if(productId == null || (productId.trim()).equals("")) throwInvalidItemId("Item description can't be empty or blank");
		
		List<ItemView> listItems = new ArrayList<ItemView>();
    	List<SupplierClient> listSupplierClients = null;
		try {
			listSupplierClients = getSupplierClients("A06_Supplier%");
			System.out.println("Starting to ask suppliers");
			for(SupplierClient s : listSupplierClients) {
				System.out.println("Asking ");
				
				ProductView product = s.getProduct(productId);
				if(product != null) {
					System.out.println("Found product " + product.getId());
					System.out.println("Product description" + product.getDesc());
					ItemView item = new ItemView();
					ItemIdView itemId = new ItemIdView();
					item.setDesc(product.getDesc());
					itemId.setProductId(product.getId());
					itemId.setSupplierId(getWsName(s.getWsURL(), "A06_Supplier%"));
					item.setItemId(itemId);
					item.setPrice(product.getPrice());
					listItems.add(item);
				}
	    	}
		} catch (BadProductId_Exception e) {
			throwInvalidItemId("Invalid itemId was given");
		}

		Collections.sort(listItems, new ItemViewPriceComparator());
		return listItems;
	}
	
	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		
		if(descText == null || (descText.trim()).equals("")) throwInvalidText("Item description can't be empty or blank");
		
		List<ItemView> resultItems = new ArrayList<ItemView>();
    	List<SupplierClient> listSupplierClients;
    	ItemView itemView;
		ItemIdView itemId;
		
		try {
			
			listSupplierClients = getSupplierClients("A06_Supplier%");
			for(SupplierClient s : listSupplierClients) {
				
				List<ProductView> productViews = s.searchProducts(descText);
				
				for(ProductView pView : productViews){
					itemView = new ItemView();
					itemId = new ItemIdView();
					itemId.setProductId(pView.getId());
					itemId.setSupplierId(getWsName(s.getWsURL(), "A06_Supplier%"));
					itemView.setItemId(itemId);
					itemView.setDesc(pView.getDesc());
					itemView.setPrice(pView.getPrice());
					resultItems.add(itemView);
				}
				
				System.out.println("Searching for items on " + s.getWsURL());
			}
		} catch (BadText_Exception e) {
			throwInvalidText("Invalid Item description");
		}
			
		Collections.sort(resultItems, new ItemViewComparator());
		return resultItems;
	}

	@Override
	public ShoppingResultView buyCart(String cartId, String creditCardNr)
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		
		MessageContext messageContext = webServiceContext.getMessageContext();
		if(buyCartResponses.containsKey((String) messageContext.get(MediatorOperationIdHandler.REQUEST_PROPERTY))) {
			System.out.println("Operação idempotente já executada");
			return buyCartResponses.get((String) messageContext.get(MediatorOperationIdHandler.REQUEST_PROPERTY));
		}
		
		
		String verifyCart = null;
		for(CartView cart : _cartList) 
			if(cart.getCartId().equals(cartId)) verifyCart = cartId;
		
		System.out.println(_cartList.size());
		if(verifyCart == null) throwInvalidCartId("Cart Id is invalid");
		
		CreditCardClient creditClient = null;

		int totalPrice = 0;
		
    	SupplierClient s = null;
    	UDDINaming uddi = this.endpointManager.getUddiNaming();
    	
    	
    	ShoppingResultView shoppingResult = new ShoppingResultView();
		List<CartItemView> droppedItems = new ArrayList<CartItemView>();
		List<CartItemView> purchasedItems = new ArrayList<CartItemView>();
		
		
    	//verify if creditCardNr is valid
    	try {
			creditClient = new CreditCardClient(uddi.lookup("CreditCard"));
			if(!creditClient.validateNumber(creditCardNr))
				throwInvalidCreditCard("CreditCard Number is invalid");
		} catch (CreditCardClientException e) {
			System.err.println("Unable to reach Credit Card server");
		} catch (UDDINamingException e1) {
			System.err.println("Error listing UDDIRecords");
			e1.printStackTrace();
		}
		
		try {
			for(CartView cart : _cartList) {
				synchronized(this){
					if(cart.getCartId().equals(cartId)) {
						
						if(cart.getItems().isEmpty())
							throwEmptyCart("Cart is empty");
	
						List<CartItemView> listItems = cart.getItems();
						System.out.println("Size of cartItems : " + listItems.size());
						//buy each product in the cart
						for(CartItemView cartItemView : listItems) {
							System.out.println("Entering for listItems");
							ItemView item = cartItemView.getItem();
							ItemIdView itemId = item.getItemId();
							
							try { //Making the purchase
								s = new SupplierClient(uddi.lookup(itemId.getSupplierId()));
								System.out.println("Asking Supplier : " + itemId.getSupplierId());
								//only reaches this if it does not throw an Exception
								s.buyProduct(itemId.getProductId(), cartItemView.getQuantity());
								purchasedItems.add(cartItemView);
								System.out.println(cartItemView.getQuantity());
								System.out.println(cartItemView.getItem().getPrice());
								totalPrice += cartItemView.getQuantity() * cartItemView.getItem().getPrice();
								System.out.println("Total price is being incremented : " + totalPrice);
							} catch (InsufficientQuantity_Exception | BadProductId_Exception | BadQuantity_Exception e) {
								System.out.println("Could not buy product " + cartItemView.getItem().getItemId().getProductId());
								System.out.println(e);
								droppedItems.add(cartItemView);
							} catch (SupplierClientException e1) {
								System.out.println("Unnable to connect to supplier " + itemId.getSupplierId());
							}
						}
						
						StringBuilder buildResult = new StringBuilder();
						buildResult.append("A06");
						buildResult.append(purchase.toString());
						String result = buildResult.toString();
						purchase = new Integer(purchase.intValue()+1);
						
						shoppingResult.setId(result);
						shoppingResult.purchasedItems = purchasedItems;
						shoppingResult.droppedItems = droppedItems;
						System.out.println(totalPrice);
						shoppingResult.setTotalPrice(totalPrice);
						
						if(purchasedItems.isEmpty()) {
							shoppingResult.setResult(Result.EMPTY);
						} else if(droppedItems.isEmpty()) {
							shoppingResult.setResult(Result.COMPLETE);
						} else { 
							shoppingResult.setResult(Result.PARTIAL);
						}
						
						//remove cart from the cartList
						_cartList.remove(cart);
						_shoppingHistory.add(shoppingResult);
						if(endpointManager.getWsURL().contains("8071")) {
							MediatorClient client = null;
							try {
								client = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
							} catch (MediatorClientException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							client.updateShopHistory(shoppingResult, cart, (String) messageContext.get(MediatorOperationIdHandler.REQUEST_PROPERTY));
						}
						buyCartResponses.put((String) messageContext.get(MediatorOperationIdHandler.REQUEST_PROPERTY), shoppingResult);
						try {
							if(sleepFlag){
								System.out.println("Mediator thread SLEEPING");
								Thread.sleep(5000);
								sleepFlag=false;
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return shoppingResult;
					}
				}
			}
			//at this point we know the cart does not exist and we throw a exception
			throwInvalidCartId("CartId is invalid: does not exist");
		} catch (UDDINamingException e) {
			System.err.println("Error listing UDDIRecords");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		
		MessageContext messageContext = webServiceContext.getMessageContext();
		if(addCartResponses.contains((String) messageContext.get(MediatorOperationIdHandler.REQUEST_PROPERTY))) {
			System.out.println("Operação idempotente já executada");
			return;
		}
		
		if(cartId == null || cartId.trim().equals("")) throwInvalidCartId("Item description can't be empty or blank");
		if(itemId == null) throwInvalidItemId("Item Id can't be null");
		if(itemQty<=0) throwInvalidQuantity("Quantity can't be negative or zero");
		
		SupplierClient s = null;
		ProductView product = null;
		UDDINaming uddi = this.endpointManager.getUddiNaming();
    	
		if(itemId.getSupplierId() == null || itemId.getSupplierId().equals("")) throwInvalidItemId("Supplier Id is invalid");
		try {
			s = new SupplierClient(uddi.lookup(itemId.getSupplierId()));
			System.out.println("Searching for productId : " + itemId.getProductId());
			product = s.getProduct(itemId.getProductId());
		} catch (SupplierClientException e) {
			System.out.println("Unnable to connect to supplier " + itemId.getSupplierId());

		} catch (BadProductId_Exception e){
			throwInvalidItemId("Item Id is invalid: does not exist");
		} catch (UDDINamingException e) {
			System.err.println("Error listing UDDIRecords");
			e.printStackTrace();
		}
		
		if(product == null) throwInvalidItemId("Item ID is invalid : Product was not found");
		synchronized(this) {
			System.out.println("Product : " + product);
			if(itemQty <= product.getQuantity()) {
				System.out.println("Entering cartList for");
				for(CartView cartView: _cartList) {
					if(cartView.getCartId().equals(cartId)) { //If cart exists
						List<CartItemView> items = cartView.getItems();
						System.out.println("Searching items of cartView");
						for(CartItemView cartItemView: items) {
							System.out.println(itemId.getProductId());
							System.out.println(cartItemView.getItem().getItemId().getProductId());
							if((cartItemView.getItem().getItemId().getProductId()).equals(itemId.getProductId()) && 
									cartItemView.getItem().getItemId().getSupplierId().equals(itemId.getSupplierId())) {
								
								int quantity = cartItemView.getQuantity();
								int newQuantity = quantity + itemQty;
								if(newQuantity < product.getQuantity()) {
									System.out.println("New quantity" + newQuantity);
									cartItemView.setQuantity(quantity + itemQty);
									sendUpdate(cartView);
									addCartResponses.add((String) messageContext.get(MediatorOperationIdHandler.REQUEST_PROPERTY));
									return;
								}
								else{ throwNotEnoughItems("Supplier doesn't have enough items"); }
							}
						}
						CartItemView cartItem = createItemForCart(product, itemId, itemQty);
						cartView.getItems().add(cartItem);
						sendUpdate(cartView);
						addCartResponses.add((String) messageContext.get(MediatorOperationIdHandler.REQUEST_PROPERTY));
						return;
					}
				}
				//If cart doesn't exist
				CartItemView cartItem = createItemForCart(product, itemId, itemQty);
				CartView newCart = new CartView();
				newCart.getItems().add(cartItem);
				newCart.setCartId(cartId);
				_cartList.add(newCart);
				sendUpdate(newCart);
				addCartResponses.add((String) messageContext.get(MediatorOperationIdHandler.REQUEST_PROPERTY));
				return;
			}
		}
		throwNotEnoughItems("Supplier don't have enough items");
		
	}
	
	public void sendUpdate(CartView cartView) {
		if(endpointManager.getWsURL().contains("8071")) {
			MediatorClient client = null;
			try {
				client = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
			} catch (MediatorClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MessageContext messageContext = webServiceContext.getMessageContext();
			client.updateCart(cartView, (String) messageContext.get(MediatorOperationIdHandler.REQUEST_PROPERTY));
		}
	}
	
	
    
	// Auxiliary operations --------------------------------------------------		
	
	@Override
    public String ping(String orgName) {
    	SupplierClient s = null;
    	String responses = "";
    	Collection<UDDIRecord> list_uddi = null;
		
		list_uddi = getSupplierRecords("A06_Supplier%");
		for(UDDIRecord record : list_uddi) {
    		System.out.println(record);
			try {
				s = new SupplierClient(record.getUrl());
			} catch (SupplierClientException e) {
				System.out.println("Unnable to connect to supplier " + record.getOrgName());

			}
    		System.out.println("Creating client" + record);
    		responses += s.ping(record.getOrgName());
    	}
		
    	System.out.println(responses);
    	return responses;
    }

	@Override
	public void clear() {
		if(endpointManager.getWsURL().contains("8071")) {
			SupplierClient s = null;
	    	Collection<UDDIRecord> list_uddi = null;
	
			list_uddi = getSupplierRecords("A06_Supplier%");
			for(UDDIRecord record : list_uddi) {
	    		System.out.println(record);
				try {
					s = new SupplierClient(record.getUrl());
				} catch (SupplierClientException e) {
					System.out.println("Unnable to connect to supplier " + record.getOrgName());
				}
				System.out.println("Creating client" + record);
				s.clear();
				System.out.println("Cleaning " + s.getWsURL());
			} 
		}
		
		_cartList.clear();
		_shoppingHistory.clear();
		
		buyCartResponses.clear();
		addCartResponses.clear();

		if(endpointManager.getWsURL().contains("8071")) {
			MediatorClient client = null;
			try {
				client = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
			} catch (MediatorClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client.clear();
		}
	}
	
	public Collection<UDDIRecord> getSupplierRecords(String orgName) {
    	UDDINaming uddi = this.endpointManager.getUddiNaming();
    	Collection<UDDIRecord> list_uddi = null;
		try {
			list_uddi = uddi.listRecords(orgName);
		} catch (UDDINamingException e) {
			System.err.println("Error listing UDDIRecords");
			e.printStackTrace();
		} 
    	return list_uddi;
	}
	
	public List<SupplierClient> getSupplierClients(String orgName) {
		List<SupplierClient>  suppliers = new ArrayList<SupplierClient>();
		
		Collection<UDDIRecord> supplierRecords = getSupplierRecords(orgName);
		
		for(UDDIRecord record : supplierRecords) {
			
			SupplierClient s;
			try {
				s = new SupplierClient(record.getUrl());
				suppliers.add(s);
			} catch (SupplierClientException e) {
				System.out.println("Could not connect to supplier " + record.getOrgName());
			}
			
		}
		
		return suppliers;
	}
	
	public CartItemView createItemForCart(ProductView product, ItemIdView itemId, int itemQty){
		ItemView newItem = new ItemView();
		CartItemView newCartItem = new CartItemView();
		newItem.setItemId(itemId);
		newItem.setPrice(product.getPrice());
		newItem.setDesc(product.getDesc());
		newCartItem.setItem(newItem);
		newCartItem.setQuantity(itemQty);
		
		return newCartItem;
	}
	
	public String getWsName(String wsURL, String orgName) {
		UDDINaming uddi = this.endpointManager.getUddiNaming();
		Collection<UDDIRecord> records = null;
		try {
			records = uddi.listRecords(orgName);
		} catch (UDDINamingException e) {
			System.out.println("Error listing UDDIRecords");
		}
		
		for(UDDIRecord record : records) {
			if(record.getUrl().equals(wsURL))
				return record.getOrgName();
		}
		
		return null;
	}

	@Override
	public List<CartView> listCarts() {
		return _cartList;
	}	
			

	@Override
	public List<ShoppingResultView> shopHistory() {
		Collections.sort(_shoppingHistory, new ShoppingResultViewComparator());
		return _shoppingHistory;
	}


	
	// View helpers -----------------------------------------------------
	
    //TODO

	
	
	
    
	// Exception helpers -----------------------------------------------------

	
	
	
	
	
	/** Helper method to throw new InvalidText exception */
	private void throwInvalidText(final String message) throws InvalidText_Exception {
		InvalidText faultInfo = new InvalidText();
		faultInfo.message = message;
		throw new InvalidText_Exception(message, faultInfo);
	}

	private void throwInvalidItemId(final String message) throws InvalidItemId_Exception {
		InvalidItemId faultInfo = new InvalidItemId();
		faultInfo.setMessage(message);
		throw new InvalidItemId_Exception(message, faultInfo);
	}
	
	private void throwInvalidCreditCard(final String message) throws InvalidCreditCard_Exception {
		InvalidCreditCard faultInfo = new InvalidCreditCard();
		faultInfo.setMessage(message);
		throw new InvalidCreditCard_Exception(message, faultInfo);
	}
	
	private void throwEmptyCart(final String message) throws EmptyCart_Exception {
		EmptyCart faultInfo = new EmptyCart();
		faultInfo.setMessage(message);
		throw new EmptyCart_Exception(message, faultInfo);
	}
	
	private void throwInvalidCartId(final String message) throws InvalidCartId_Exception {
		InvalidCartId faultInfo = new InvalidCartId();
		faultInfo.setMessage(message);
		throw new InvalidCartId_Exception(message, faultInfo);
	}
	
	private void throwInvalidQuantity(final String message) throws InvalidQuantity_Exception {
		InvalidQuantity faultInfo = new InvalidQuantity();
		faultInfo.setMessage(message);
		throw new InvalidQuantity_Exception(message, faultInfo);
	}
	private void throwNotEnoughItems(final String message) throws NotEnoughItems_Exception{
		NotEnoughItems faultInfo = new NotEnoughItems();
		faultInfo.setMessage(message);
		throw new NotEnoughItems_Exception(message, faultInfo);
	}

	@Override
	public void imAlive() {
		if(endpointManager.getWsURL().contains("8071"))
			return;
		Date date = new Date();
		
		LifeProof.stampAlive = date;
	}

	@Override
	public void updateCart(CartView cartView, String operationId) {
		for(int i = 0; i < _cartList.size(); i ++) {
			if(_cartList.get(i).getCartId().equals(cartView.getCartId())) {
				System.out.println("Removing cart " + cartView.getCartId());
				_cartList.remove(i);
			}
		}
		_cartList.add(cartView);
		addCartResponses.add(operationId);
		System.out.println("UPDATING CART LIST WITH CHANGE FROM PRIMARY MEDIATOR");
	}

	@Override
	public void updateShopHistory(ShoppingResultView shoppingResultView, CartView cartView, String operationId) {
		_cartList.remove(cartView);
		_shoppingHistory.add(shoppingResultView);
		buyCartResponses.put(operationId, shoppingResultView);
		System.out.println("UPDATING SHOP HISTORY WITH PURCHASE FROM PRIMARY MEDIATOR");
	}
}
