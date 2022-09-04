package model;

/** This is the Contact class. */
public class Contact {
    private int contactId;
    private String contactName;

    public Contact (int contactId, String contactName){
        this.contactId = contactId;
        this.contactName = contactName;
    }

    /** @return The contact ID*/
    public int getContactId() {
        return contactId;
    }

    /** @param contactId The contact ID to set */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /** @return The contact name */
    public String getContactName() {
        return contactName;
    }

    /** @param contactName The contact name to set*/
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /** @return The contact name string */
    @Override
    public String toString() {
        return contactName;
    }
}
