package entity;

import java.io.Serializable;

/**
 * @author 大定府羡民（QQ：1032694760）
 */
public class District implements Serializable {
    public String Name;
    public String City;
    public String Province;

    public District() {
        super();
    }

    public District(String province, String city, String name) {
        Province = province;
        City = city;
        Name = name;
    }

}
