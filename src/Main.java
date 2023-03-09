import com.alibaba.fastjson2.JSON;
import entity.Address;
import entity.City;
import entity.District;
import entity.Province;
import logger.MyLog;
import util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 大定府羡民（QQ：1032694760）
 */
public class Main {

    public static void main(String[] args) {
        MyLog.enable(true);
        // 中国行政区划数据，来源于中国国家统计局，参阅 https://xiangyuecn.gitee.io/areacity-jsspider-statsgov
        String json = FileUtils.readText(new File(System.getProperty("user.dir"), "china_address.json").getAbsolutePath());
        List<Address> addresses = JSON.parseArray(json, Address.class);
        MyLog.debug("解析到地址数据数量：" + addresses.size());
        List<Province> provinces = new ArrayList<>();
        List<City> cities = new ArrayList<>();
        List<District> districts = new ArrayList<>();
        for (Address province : addresses) {
            MyLog.debug("处理省份：" + province.ext_name);
            if (Arrays.asList("台湾", "香港", "澳门", "国外").contains(province.name)) {
                continue;
            }
            provinces.add(new Province(province.ext_name));
            for (Address city : province.childs) {
                MyLog.debug("处理地级市：" + province.ext_name + city.ext_name);
                cities.add(new City(province.ext_name, city.ext_name));
                districts.add(new District(province.ext_name, city.ext_name, "市辖区"));
                for (Address district : city.childs) {
                    MyLog.debug("处理区县：" + province.ext_name + city.ext_name + district.ext_name);
                    if (district.name.equals(city.name)) {
                        continue;
                    }
                    districts.add(new District(province.ext_name, city.ext_name, district.ext_name));
                }
            }
        }
        saveAsFile("provinces.json", JSON.toJSONString(provinces));
        saveAsFile("cities.json", JSON.toJSONString(cities));
        saveAsFile("districts.json", JSON.toJSONString(districts));
        MyLog.debug("处理完成");
    }

    private static void saveAsFile(String fileName, String content) {
        File saveDir = new File(System.getProperty("user.dir"), "json");
        if (!saveDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            saveDir.mkdirs();
        }
        File file = new File(saveDir, fileName);
        FileUtils.writeText(file.getAbsolutePath(), content, "UTF-8");
    }

}