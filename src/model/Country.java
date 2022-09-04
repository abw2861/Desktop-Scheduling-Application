package model;

/** This is the Country class. */
public class Country {
    private int countryId;
    private String countryName;


    public Country(int countryId, String countryName){
        this.countryId = countryId;
        this.countryName = countryName;
    }

    /** @return The country ID */
    public int getCountryId() {
        return countryId;
    }

    /** @param countryId The country Id to set */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /** @return The country name */
    public String getCountryName() {
        return countryName;
    }

    /** @param countryName The country name */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /** @return The country name string */
    @Override
    public String toString() {
        return countryName;
    }
}
