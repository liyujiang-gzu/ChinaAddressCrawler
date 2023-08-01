package entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author 大定府羡民（QQ：1032694760）
 */
public class Address implements Serializable {
    public String name;
    public String code;
    public int level;
    public List<Address> children;
}
