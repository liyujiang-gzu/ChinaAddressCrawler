package entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author 大定府羡民（QQ：1032694760）
 */
public class Address implements Serializable {
    public String id;
    public Integer pid;
    public Integer deep;
    public String name;
    public String pinyin;
    public String pinyin_prefix;
    public String ext_id;
    public String ext_name;
    public List<Address> childs;
}
