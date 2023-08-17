package entity;

/**
 * @author 大定府羡民（QQ：1032694760）
 */
public class Region {
    public String code;
    public String fullCode;
    public String name;
    public Level level;
    public String levelName;
    public String parentCode;

    @SuppressWarnings("NonAsciiCharacters")
    public enum Level {
        省级, 地级, 县级, 乡级, 村级
    }
}
