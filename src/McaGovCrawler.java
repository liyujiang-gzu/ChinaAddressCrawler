import com.alibaba.fastjson2.JSON;
import entity.Region;
import logger.MyLog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.MessyUtils;
import util.SslUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 采集国家民政局：<a href="https://www.mca.gov.cn/mzsj/xzqh/2022/202201xzqh.html">2022年中华人民共和国县以上行政区划代码</a>
 * 参阅 <a href="https://blog.csdn.net/ggg6568/article/details/130974026">Java爬取行政区域信息到数据库</a>
 *
 * @author 大定府羡民（QQ：1032694760）
 */
@SuppressWarnings("NonAsciiCharacters")
public class McaGovCrawler {

    public static void main(String[] args) throws Exception {
        MyLog.enable(true);
        SslUtils.ignoreSsl();
        Document document = Jsoup.connect("https://www.mca.gov.cn/mzsj/xzqh/2022/202201xzqh.html")
                .header("Accept", "*/*")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.188")
                .maxBodySize(0)
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .timeout(10000)
                .get();
        Elements elements = document.select("tr");
        ArrayList<Region> matchList = new ArrayList<>();
        ArrayList<String> ignoreList = new ArrayList<>();
        for (Element element : elements) {
            Elements selected = element.select("td");
            if (selected.size() > 3) {
                String regionCode = selected.get(1).text();
                String regionName = selected.get(2).text();
                String parentCode;
                if (regionCode.matches("^[1-9]\\d{5}$")) {
                    Region.Level level = Region.Level.地级;
                    parentCode = regionCode.substring(0, 2) + "0000";
                    if (!regionCode.endsWith("00")) {
                        level = Region.Level.县级;
                        parentCode = regionCode.substring(0, 4) + "00";
                    }
                    if (regionCode.endsWith("0000")) {
                        level = Region.Level.省级;
                        parentCode = "000000";
                    }
                    Region region = new Region();
                    region.code = regionCode;
                    region.name = regionName;
                    region.level = level;
                    region.parentCode = parentCode;
                    matchList.add(region);
                } else {
                    ignoreList.add(regionCode);
                }
            }
        }
        MyLog.debug("正则匹配的总数量为：" + matchList.size());
        MyLog.debug("正则未匹配的总数量为：" + ignoreList.size());
        saveAsSQL(matchList);
        MyLog.debug("正则未匹配的数据：" + JSON.toJSONString(ignoreList));
    }

    private static void saveAsSQL(List<Region> regions) {
        StringBuilder sb = new StringBuilder();
        sb.append("-- 国家民政部，2022年中华人民共和国县以上行政区划代码\n");
        sb.append("REPLACE INTO region_mca (`code`, `name`, `level`, `parent_code`) VALUES\n");
        int i = 0, n = regions.size();
        for (Region region : regions) {
            sb.append("(");
            sb.append("'").append(region.code).append("', ");
            sb.append("'").append(region.name).append("', ");
            sb.append("'").append(region.level.name()).append("', ");
            sb.append("'").append(region.parentCode).append("'");
            sb.append(i == n - 1 ? ");\n" : "), \n");
            i++;
        }
        MessyUtils.saveToFile("sql/region_mca.sql", sb.toString());
    }

}
