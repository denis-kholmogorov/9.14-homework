import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {

        String station;
        Double numberStation;
        String nameLine;
        TreeMap<String,Double> stations = new TreeMap<>();

        int maxBodySize = 20480000;
        String urlSite = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";
        try
        {
            Document doc = Jsoup.connect(urlSite).maxBodySize(maxBodySize).get();
            Elements elements = doc.select(".standard td[data-sort-value~=\\d+][style~=background.+]");
            System.out.println(elements.size());

            for(Element element: elements)
            {
                nameLine = element.select("span[title~=.+]").attr("title");
                numberStation = Double.parseDouble(element.attr("data-sort-value"));
                station = element.nextElementSibling().text();

                System.out.println(numberStation + "  " + station + " " + nameLine);
                stations.put(station, numberStation);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Процесс завершен");
    }

}
