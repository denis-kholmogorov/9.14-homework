import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {
        JSONArray array = new JSONArray();
        String lineArray = "";
        String stationName;
        String numberLineStation;
        String nameLine;
        JSONObject stationsObj = new JSONObject();

        int maxBodySize = 20480000;
        String urlSite = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";
        try {
            Document doc = Jsoup.connect(urlSite).maxBodySize(maxBodySize).get();
            Elements elements = doc.select(".standard td[data-sort-value~=\\d+][style~=background.+]");
            System.out.println(elements.size());

            for (Element element : elements) {
                nameLine = element.select("span[title~=.+]").attr("title");
                numberLineStation = element.attr("data-sort-value");
                //numberLineStation = Double.parseDouble(element.attr("data-sort-value"));
                stationName = element.nextElementSibling().text();
              System.out.println(numberLineStation + "  " + stationName + " " + nameLine);

                if (lineArray.equals(""))
                {
                    lineArray = numberLineStation;
                    array.add(stationName);
                }
                else if (!lineArray.equals(numberLineStation))
                {
                    stationsObj.put(lineArray, array);
                    lineArray = numberLineStation;
                    array = new JSONArray();
                    array.add(stationName);
                }
                else if (lineArray.equals(numberLineStation))
                {
                    array.add(stationName);
                    System.out.println(stationName);
                    lineArray = numberLineStation;
                    //stations.put(stationName, numberStation);
                }

                FileWriter file = new FileWriter("Station.json");
                file.write(stationsObj.toString());
                file.flush();
            }
        }
        catch(IOException e)
            {
                e.printStackTrace();
            }
            System.out.println("Процесс завершен");

    }
}
