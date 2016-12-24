package safe.girl.just.person;

/**
 * Created by 许灡珊 on 2016/10/22.
 */
public class NeiborPerson {
    private String phone ;
    private double lan,lon ;
    private String location ;
    public NeiborPerson(String p,double lon,double lan,String loca){
        this.phone = p ;
        this.lan = lan ;
        this.lon = lon ;
        this.location = loca ;
    }

    public double getLan() {
        return lan;
    }

    public double getLon() {
        return lon;
    }

    public String getLocation() {
        return location;
    }

    public String getPhone() {
        return phone;
    }
}
