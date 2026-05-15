package model.roomUtil;


public abstract class Amenities{
    public String featureName;
    public double additionalCosts;
    public String returnName(){
        return featureName;
    }
    public abstract String returnFeatureDesc();
    public abstract double getAdditionalCost();
    public abstract Amenities parseAmenity(String amenityString);


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Amenities that = (Amenities) o;

        return this.returnFeatureDesc().equals(that.returnFeatureDesc());
    }

    public int hashCode() {
        return (getClass().getName() + returnFeatureDesc()).hashCode();
    }
}


