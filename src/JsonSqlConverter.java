import com.alibaba.fastjson2.JSON;
import entity.*;
import logger.MyLog;
import util.MessyUtils;

import java.util.List;

/**
 * @author 大定府羡民（QQ：1032694760）
 */
public class JsonSqlConverter {

    public static void main(String[] args) {
        MyLog.enable(true);
        String provincesJson = MessyUtils.readFromFile("json/provinces.json");
        String citiesJson = MessyUtils.readFromFile("json/cities.json");
        String countiesJson = MessyUtils.readFromFile("json/counties.json");
        String townshipsJson = MessyUtils.readFromFile("json/townships.json");
        List<Region> provinceRegions = JSON.parseArray(provincesJson, Region.class);
        MyLog.debug("解析到省级数据数量：" + provinceRegions.size());
        List<Region> citiesRegions = JSON.parseArray(citiesJson, Region.class);
        MyLog.debug("解析到地级数据数量：" + citiesRegions.size());
        List<Region> countiesRegions = JSON.parseArray(countiesJson, Region.class);
        MyLog.debug("解析到县级数据数量：" + countiesRegions.size());
        List<Region> townshipsRegions = JSON.parseArray(townshipsJson, Region.class);
        MyLog.debug("解析到乡级数据数量：" + townshipsRegions.size());
        StringBuilder sb = new StringBuilder();
        sb.append("-- 国家统计局，2022年度全国统计用区划代码和城乡划分代码\n");
        sb.append("SET FOREIGN_KEY_CHECKS=0;\n");
        sb.append("TRUNCATE TABLE region_stats;\n");
        sb.append("INSERT INTO region_stats (`code`, `full_code`, `name`, `level`, `parent_code`) VALUES\n");
        buildSQL(provinceRegions, sb, false);
        buildSQL(citiesRegions, sb, false);
        buildSQL(countiesRegions, sb, false);
        buildSQL(townshipsRegions, sb, true);
        sb.append("SET FOREIGN_KEY_CHECKS=1;\n");
        MessyUtils.saveToFile("sql/region_stats.sql", sb.toString());
    }

    private static void buildSQL(List<Region> provinceRegions, StringBuilder sb, boolean isOver) {
        int i = 0, n = provinceRegions.size();
        for (Region region : provinceRegions) {
            sb.append("(");
            sb.append("'").append(region.code).append("', ");
            sb.append("'").append(region.fullCode).append("', ");
            sb.append("'").append(region.name).append("', ");
            sb.append("'").append(region.level.name()).append("', ");
            sb.append("'").append(region.parentCode).append("'");
            sb.append(i == n - 1 && isOver ? ");\n" : "), \n");
            i++;
        }
    }

}
