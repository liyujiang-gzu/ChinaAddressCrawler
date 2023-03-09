package entity;

import java.io.Serializable;

/**
 * @author 大定府羡民（QQ：1032694760）
 */
public class City implements Serializable {
    public String Name;
    public String Province;

    public City() {
        super();
    }

    public City(String province, String name) {
        Province = province;
        Name = name;
    }

}
