package model;

/** This is the Customer class. */
public class Customer {
    private int customerId;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private Division customerDivision;

    public Customer(int customerId, String customerName, String address, String postalCode, String phone, Division customerDivision) {
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.customerDivision = customerDivision;
        this.customerId = customerId;
    }

    /** @return The customer name */
    public String getCustomerName() {
        return customerName;
    }

    /** @param customerName The customer name to set */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /** @return The customer address */
    public String getAddress() {
        return address;
    }

    /** @param address The customer address to set */
    public void setAddress(String address) {
        this.address = address;
    }

    /** @return The customer postal code */
    public String getPostalCode() {
        return postalCode;
    }

    /** @param postalCode The customer postal code to set */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /** @return The customer phone */
    public String getPhone() {
        return phone;
    }

    /** @param phone The customer phone to set */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** @return The customer division */
    public Division getCustomerDivision() {
        return customerDivision;
    }

    /** @param customerDivision The customer division to set */
    public void setCustomerDivision(Division customerDivision) {
        this.customerDivision = customerDivision;
    }

    /** @return The customer ID */
    public int getCustomerId() {
        return customerId;
    }

    /** @param customerId The customer ID to set */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /** @return The customer name string */
    @Override
    public String toString() {
        return customerName;
    }
}
