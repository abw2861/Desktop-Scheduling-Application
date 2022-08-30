package model;

public class Contact {
    private int contactId;
    private String contactName;

    public Contact (int contactId, String contactName){
        this.contactId = contactId;
        this.contactName = contactName;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public String toString() {
        return contactName;
    }
}
