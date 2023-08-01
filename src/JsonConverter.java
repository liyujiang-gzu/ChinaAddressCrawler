import com.alibaba.fastjson2.JSON;
import entity.*;
import logger.MyLog;
import util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 大定府羡民（QQ：1032694760）
 */
public class JsonConverter {

    public static void main(String[] args) {
        MyLog.enable(true);
        String provincesJson = FileUtils.readText(new File(System.getProperty("user.dir"), "provinces.json").getAbsolutePath());
        String citiesJson = FileUtils.readText(new File(System.getProperty("user.dir"), "cities.json").getAbsolutePath());
        String countiesJson = FileUtils.readText(new File(System.getProperty("user.dir"), "counties.json").getAbsolutePath());
        List<Region> provinceRegions = JSON.parseArray(provincesJson, Region.class);
        MyLog.debug("解析到省级数据数量：" + provinceRegions.size());
        List<Region> citiesRegions = JSON.parseArray(citiesJson, Region.class);
        MyLog.debug("解析到地级数据数量：" + citiesRegions.size());
        List<Region> countiesRegions = JSON.parseArray(countiesJson, Region.class);
        MyLog.debug("解析到县级数据数量：" + countiesRegions.size());
        List<Province> provinces = new ArrayList<>();
        List<City> cities = new ArrayList<>();
        List<County> counties = new ArrayList<>();
//        for (Region region : provinceRegions) {
//            MyLog.debug("处理省份：" + region.ext_name);
//            if (Arrays.asList("台湾", "香港", "澳门", "国外").contains(region.name)) {
//                continue;
//            }
//            provinces.add(new Province(region.ext_name));
//            for (Address city : region.childs) {
//                MyLog.debug("处理地级市：" + region.ext_name + city.ext_name);
//                cities.add(new City(region.ext_name, city.ext_name));
//                counties.add(new County(region.ext_name, city.ext_name, "市辖区"));
//                for (Address district : city.childs) {
//                    MyLog.debug("处理区县：" + region.ext_name + city.ext_name + district.ext_name);
//                    if (district.name.equals(city.name)) {
//                        continue;
//                    }
//                    counties.add(new County(region.ext_name, city.ext_name, district.ext_name));
//                }
//            }
//        }
//        MessyUtils.saveAsFile("json/china_provinces.json", JSON.toJSONString(provinces));
    }

}