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
        省, 地, 县, 乡, 村
    }
}
