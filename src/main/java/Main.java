import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {

        JSONObject stationsObj = new JSONObject();
        JSONArray lineStationsArray = new JSONArray();
        JSONObject metropoliten = new JSONObject();

        int maxBodySize = 20480000;
        String urlSite = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";
        try {
            Document doc = Jsoup.connect(urlSite).maxBodySize(maxBodySize).get();
            //Elements elements = doc.select("td[data-sort-value~=\\w+][style~=background.+]");
            Elements elements = doc.select(".standard span[title~=[а-яА-Я]+ линия$]");  //td[data-sort-value~=\w+]
            /*for(Element element: elements1){
                System.out.println(element.parent().nextElementSibling().text());

            }*/
            stationsObj = createStationsJSON(elements);
            lineStationsArray = createLineStationsJSON(elements);

            //System.out.println(elements.size());



            metropoliten.put("stations", stationsObj);
            metropoliten.put("lines", lineStationsArray);
            String tetx = metropoliten.toJSONString();
            FileWriter file = new FileWriter("Station.json");
            file.write(tetx);
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Процесс завершен");

    }

    public static JSONObject createStationsJSON(Elements elements) {

        JSONObject stationsObj = new JSONObject();
        JSONArray StationsArrayOneLine = new JSONArray();
        String lineArray = "";
        String stationName;
        String numberLineStation;

        for (Element element : elements) {
            numberLineStation = element.parent().selectFirst(".sortkey").text();                       //numberLineStation = element.selectFirst(".sortkey").text(); можно использовать для пересадок
            //numberLineStation = Double.parseDouble(element.attr("data-sort-value"));        // stationName = element.nextElementSibling().text();
            stationName = element.parent().nextElementSibling().text();                                  //System.out.println(numberLineStation + "  " + stationName + " " + nameLine);
            System.out.println(numberLineStation + " " + stationName);
            if (lineArray.equals("")) {
                lineArray = numberLineStation;
            } else if (!lineArray.equals(numberLineStation)) {
                stationsObj.put(lineArray, StationsArrayOneLine);
                lineArray = numberLineStation;
                StationsArrayOneLine = new JSONArray();
            } else if (lineArray.equals(numberLineStation)) {
                lineArray = numberLineStation;
            }

            StationsArrayOneLine.add(stationName);
        }

        return stationsObj;
    }

    public static JSONArray createLineStationsJSON(Elements elements) {

        String startNumberLine = "";
        String colorLine;
        String nameLine;
        String numberLineStations;
        JSONArray linesArray = new JSONArray();
        JSONObject lineNumberNameColor = new JSONObject();


        for (Element element : elements) {
            nameLine = element.parent().select("span[title~=.+]").attr("title");
            numberLineStations = element.parent().selectFirst(".sortkey").text();
            colorLine = element.parent().selectFirst("td").attr("style");
            colorLine = colorLine.substring(colorLine.indexOf(":") + 1);
            System.out.println(nameLine + " " + numberLineStations + " " + colorLine);

            if(!startNumberLine.equals(numberLineStations)){
                lineNumberNameColor.put("name", nameLine);
                lineNumberNameColor.put("number", numberLineStations);
                if(colorLine.length() > 7){
                    colorLine = "#FFFFFF";
                }
                lineNumberNameColor.put("color", colorLine);
                startNumberLine = numberLineStations;
                linesArray.add(lineNumberNameColor);
                lineNumberNameColor = new JSONObject();
                System.out.println(colorLine + " " + nameLine);
            }

        }
        return linesArray;
    }
}


