import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {

        String station;
        String numberStation;
        TreeMap<String,String> stations = new TreeMap<>();

        int maxBodySize = 20480000;
        String urlSite = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";
        try {
            Document doc = Jsoup.connect(urlSite).maxBodySize(maxBodySize).get();
            Elements elements = doc.select(".standard td[data-sort-value~=\\d+][style~=background.+]");
           // Elements elements = elements1.select("[]");
            System.out.println(elements.size());
            for(Element element: elements){
                numberStation = element.selectFirst("span").text();
                station = element.nextElementSibling().text();
                System.out.println(numberStation +"     " + station);

                stations.put(station, numberStation);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Процесс завершен");
    }
}
