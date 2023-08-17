import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import entity.Region;
import logger.MyLog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import util.MessyUtils;
import util.SslUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 采集国家统计局：<a href="http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/index.html">2022年度全国统计用区划代码和城乡划分代码</a>
 *
 * @author 大定府羡民（QQ：1032694760）
 */
@SuppressWarnings("NonAsciiCharacters")
public class StatsGovCrawler {

    public static void main(String[] args) {
        MyLog.enable(true);
        SslUtils.ignoreSsl();
        List<Region> allProvinces = getProvinces();
        ArrayList<Region> allCities = new ArrayList<>();
        for (Region province : allProvinces) {
            try {
                allCities.addAll(getCities(province.code));
            } catch (Exception ignore) {
            }
        }
        MessyUtils.saveToFile("json/cities.json", JSON.toJSONString(allCities, JSONWriter.Feature.PrettyFormat));
        ArrayList<Region> allCounties = new ArrayList<>();
        for (Region city : allCities) {
            try {
                allCounties.addAll(getCounties(city.parentCode, city.code));
            } catch (Exception ignore) {
            }
        }
        MessyUtils.saveToFile("json/counties.json", JSON.toJSONString(allCounties, JSONWriter.Feature.PrettyFormat));
        ArrayList<Region> allTownships = new ArrayList<>();
        for (Region county : allCounties) {
            String provinceCode = "";
            for (Region city : allCities) {
                if (city.code.equals(county.parentCode)) {
                    provinceCode = city.parentCode;
                    break;
                }
            }
            try {
                allTownships.addAll(getTownships(provinceCode, county.parentCode, county.code));
            } catch (Exception ignore) {
            }
        }
        MessyUtils.saveToFile("json/townships.json", JSON.toJSONString(allTownships, JSONWriter.Feature.PrettyFormat));
//        ArrayList<Region> allVillages = new ArrayList<>();
//        for (Region township : allTownships) {
//            String cityCode = "";
//            for (Region county : allCounties) {
//                if (county.code.equals(township.parentCode)) {
//                    cityCode = county.parentCode;
//                    break;
//                }
//            }
//            String provinceCode = "";
//            for (Region city : allCities) {
//                if (city.code.equals(cityCode)) {
//                    provinceCode = city.parentCode;
//                    break;
//                }
//            }
//            try {
//                allVillages.addAll(getVillages(provinceCode, cityCode, township.parentCode, township.code));
//            } catch (Exception ignore) {
//            }
//        }
//        MessyUtils.saveToFile("json/villages.json", JSON.toJSONString(allVillages, JSONWriter.Feature.PrettyFormat));
    }

    private static Elements getElements(String url, Evaluator evaluator) {
        try {
            Document document = Jsoup.connect(url)
                    //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("60.199.29.41", 8111)))
                    .header("Accept", "*/*")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.188")
                    .maxBodySize(0)
                    .followRedirects(true)
                    .timeout(10000)
                    .get();
            Elements elements = document.select(evaluator);
            // HTTP请求太频繁会导致报错“HTTP error fetching URL. Status=502, URL=……”或“Too many redirects occurred trying to load URL……”，这里暂停几秒再继续下一次HTTP请求
            Thread.sleep(30 * 1000);
            MyLog.debug(url + "，符合条件的元素数：" + elements.size());
            return elements;
        } catch (Exception e) {
            MyLog.debug(url + "，执行出错：" + e);
            try {
                // 请求出错（如：Read timed out、502）后等待一段时间后再尝试，规避因同一IP频繁请求被限制问题
                Thread.sleep(30 * 60 * 1000);
                throw new RuntimeException(e);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static List<Region> getProvinces() {
        String filePath = "json/provinces.json";
        if (MessyUtils.existsFile(filePath)) {
            return JSON.parseArray(MessyUtils.readFromFile(filePath), Region.class);
        }
        Elements elements = getElements("http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/index.html",
                new Evaluator.AttributeWithValueEnding("href", ".html"));
        ArrayList<Region> provinces = new ArrayList<>();
        for (Element element : elements) {
            //MyLog.debug(element.toString());
            String href = element.attributes().get("href");
            if (href.matches("\\d+\\.html")) {
                Region region = new Region();
                region.code = href.replaceAll("\\.html", "");
                region.fullCode = MessyUtils.paddingZero(region.code);
                region.name = element.text();
                region.level = Region.Level.省级;
                region.levelName = Region.Level.省级.name();
                region.parentCode = "0";
                provinces.add(region);
            }
        }
        MessyUtils.saveToFile(filePath, JSON.toJSONString(provinces, JSONWriter.Feature.PrettyFormat));
        return provinces;
    }

    private static List<Region> getCities(String provinceCode) {
        String filePath = "json/cities/cities_" + provinceCode + ".json";
        if (MessyUtils.existsFile(filePath)) {
            return JSON.parseArray(MessyUtils.readFromFile(filePath), Region.class);
        }
        ArrayList<Region> cities = new ArrayList<>();
        Pattern pattern = Pattern.compile(provinceCode + "/\\d+\\.html");
        Elements elements = getElements("http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/" + provinceCode + ".html",
                new Evaluator.AttributeWithValueMatching("href", pattern));
        for (Element element : elements) {
            //MyLog.debug(element.toString());
            String href = element.attributes().get("href");
            String text = element.text();
            if (pattern.matcher(href).matches()) {
                String code = href.replaceAll(provinceCode + "/", "").replaceAll("\\.html", "");
                if (!text.matches("\\d+")) {
                    Region region = new Region();
                    region.code = code;
                    region.fullCode = MessyUtils.paddingZero(code);
                    region.name = text;
                    region.level = Region.Level.地级;
                    region.levelName = Region.Level.地级.name();
                    region.parentCode = provinceCode;
                    cities.add(region);
                }
            }
        }
        MessyUtils.saveToFile(filePath, JSON.toJSONString(cities, JSONWriter.Feature.PrettyFormat));
        return cities;
    }

    private static List<Region> getCounties(String provinceCode, String cityCode) {
        String filePath = "json/counties/counties_" + provinceCode + "_" + cityCode + ".json";
        if (MessyUtils.existsFile(filePath)) {
            return JSON.parseArray(MessyUtils.readFromFile(filePath), Region.class);
        }
        ArrayList<Region> counties = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+/\\d+\\.html");
        Elements elements = getElements("http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/" + provinceCode + "/" + cityCode + ".html",
                new Evaluator.AttributeWithValueMatching("href", pattern));
        for (Element element : elements) {
            //MyLog.debug(element.toString());
            String href = element.attributes().get("href");
            String text = element.text();
            if (pattern.matcher(href).matches()) {
                String code = href.replaceAll("\\d+/", "").replaceAll("\\.html", "");
                if (!text.matches("\\d+")) {
                    Region region = new Region();
                    region.code = code;
                    region.fullCode = MessyUtils.paddingZero(code);
                    region.name = text;
                    region.level = Region.Level.县级;
                    region.levelName = Region.Level.县级.name();
                    region.parentCode = cityCode;
                    counties.add(region);
                }
            }
        }
        MessyUtils.saveToFile(filePath, JSON.toJSONString(counties, JSONWriter.Feature.PrettyFormat));
        return counties;
    }

    private static List<Region> getTownships(String provinceCode, String cityCode, String countyCode) {
        String filePath = "json/townships/townships_" + provinceCode + "_" + cityCode + "_" + countyCode + ".json";
        if (MessyUtils.existsFile(filePath)) {
            return JSON.parseArray(MessyUtils.readFromFile(filePath), Region.class);
        }
        ArrayList<Region> townships = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+/\\d+\\.html");
        Elements elements = getElements("http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/" + provinceCode + "/" + cityCode.replace(provinceCode, "") + "/" + countyCode + ".html",
                new Evaluator.AttributeWithValueMatching("href", pattern));
        for (Element element : elements) {
            //MyLog.debug(element.toString());
            String href = element.attributes().get("href");
            String text = element.text();
            if (pattern.matcher(href).matches()) {
                String code = href.replaceAll("\\d+/", "").replaceAll("\\.html", "");
                if (!text.matches("\\d+")) {
                    Region region = new Region();
                    region.code = code;
                    region.fullCode = MessyUtils.paddingZero(code);
                    region.name = text;
                    region.level = Region.Level.乡级;
                    region.levelName = Region.Level.乡级.name();
                    region.parentCode = countyCode;
                    townships.add(region);
                }
            }
        }
        MessyUtils.saveToFile(filePath, JSON.toJSONString(townships, JSONWriter.Feature.PrettyFormat));
        return townships;
    }

//    private static List<Region> getVillages(String provinceCode, String cityCode, String countyCode, String townshipCode) {
//        String filePath = "json/villages/villages_" + provinceCode + "_" + cityCode + "_" + countyCode + "_" + townshipCode + ".json";
//        if (MessyUtils.existsFile(filePath)) {
//            return JSON.parseArray(MessyUtils.readFromFile(filePath), Region.class);
//        }
//        ArrayList<Region> villages = new ArrayList<>();
//        Elements elements = getElements("http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/" + provinceCode + "/" + cityCode.replace(provinceCode, "") + "/" + countyCode.replace(cityCode, "") + "/" + townshipCode + ".html",
//                new Evaluator.Class("villagetr"));
//        for (Element element : elements) {
//            //MyLog.debug(element.toString());
//            Elements tags = element.getElementsByTag("td");
//            if (tags.size() == 3) {
//                Region region = new Region();
//                region.code = tags.get(0).text();
//                region.fullCode = MessyUtils.paddingZero(region.code);
//                region.name = tags.get(2).text().replace("居民委员会", "居委会").replace("村民委员会", "村委会");
//                region.level = Region.Level.村级;
//                region.levelName = Region.Level.村级.name();
//                region.parentCode = cityCode;
//                villages.add(region);
//            }
//        }
//        MessyUtils.saveToFile(filePath, JSON.toJSONString(villages, JSONWriter.Feature.PrettyFormat));
//        return villages;
//    }

}
