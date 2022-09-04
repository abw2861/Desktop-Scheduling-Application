package model;

/** This is the Division class. */
public class Division {
    private int divisionId;
    private String divisionName;
    private Country country;


    public Division(int divisionId, String divisionName, Country country){
        this.divisionId = divisionId;
        this.divisionName = divisionName;
        this.country = country;

    }

    /** @return The division ID */
    public int getDivisionId() {
        return divisionId;
    }

    /** @param divisionId The division ID to set */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    /** @return The division name */
    public String getDivisionName() {
        return divisionName;
    }

    /** @param divisionName The division name to set */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /** @return The country */
    public Country getCountry() {
        return country;
    }

    /** @param country The country to set */
    public void setCountry(Country country) {
        this.country = country;
    }

    /** @return The division name string */
    @Override
    public String toString(){
        return divisionName;
    }
}
