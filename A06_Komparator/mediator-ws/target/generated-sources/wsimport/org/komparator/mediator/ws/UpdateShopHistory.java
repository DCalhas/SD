
package org.komparator.mediator.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateShopHistory complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateShopHistory">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shoppingResultView" type="{http://ws.mediator.komparator.org/}shoppingResultView" minOccurs="0"/>
 *         &lt;element name="cartView" type="{http://ws.mediator.komparator.org/}cartView" minOccurs="0"/>
 *         &lt;element name="operationId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateShopHistory", propOrder = {
    "shoppingResultView",
    "cartView",
    "operationId"
})
public class UpdateShopHistory {

    protected ShoppingResultView shoppingResultView;
    protected CartView cartView;
    protected String operationId;

    /**
     * Gets the value of the shoppingResultView property.
     * 
     * @return
     *     possible object is
     *     {@link ShoppingResultView }
     *     
     */
    public ShoppingResultView getShoppingResultView() {
        return shoppingResultView;
    }

    /**
     * Sets the value of the shoppingResultView property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShoppingResultView }
     *     
     */
    public void setShoppingResultView(ShoppingResultView value) {
        this.shoppingResultView = value;
    }

    /**
     * Gets the value of the cartView property.
     * 
     * @return
     *     possible object is
     *     {@link CartView }
     *     
     */
    public CartView getCartView() {
        return cartView;
    }

    /**
     * Sets the value of the cartView property.
     * 
     * @param value
     *     allowed object is
     *     {@link CartView }
     *     
     */
    public void setCartView(CartView value) {
        this.cartView = value;
    }

    /**
     * Gets the value of the operationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationId() {
        return operationId;
    }

    /**
     * Sets the value of the operationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationId(String value) {
        this.operationId = value;
    }

}
