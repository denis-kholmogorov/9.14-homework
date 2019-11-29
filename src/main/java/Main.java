import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        JSONObject stationsObj;
        JSONArray lineStationsArray;
        JSONObject metropoliten = new JSONObject();
        String path = "stations.json";
        HashMap<String, Integer> stationAndLines;

        int maxBodySize = 20480000;
        String urlSite = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";
        try {
            Document doc = Jsoup.connect(urlSite).maxBodySize(maxBodySize).get();
            Elements elements = doc.select(".standard span[title~=[а-яА-Я]+ линия$]");                          //td[data-sort-value~=\w+] Elements elements = doc.select("td[data-sort-value~=\\w+][style~=background.+]");

            stationsObj = createStationsJSON(elements);
            lineStationsArray = createLineStationsJSON(elements);

            metropoliten.put("stations", stationsObj);
            metropoliten.put("lines", lineStationsArray);

            String text = metropoliten.toJSONString();
            FileWriter file = new FileWriter(path);
            file.write(text);
            file.flush();
            file.close();

            String fileStationsJson = getJsonFile(path);
            stationAndLines = readJsonFile(fileStationsJson);
            printCountStation(stationAndLines, lineStationsArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static JSONObject createStationsJSON(Elements elements) {

        JSONObject stationsObj = new JSONObject();
        String stationName;
        String numberLineStation;
        JSONArray stationsArrayOneLine;

        for (Element element : elements) {
            numberLineStation = element.previousElementSibling().selectFirst(".sortkey").text();                        //numberLineStation = element.selectFirst(".sortkey").text(); можно использовать для пересадок
            stationName = element.parent().nextElementSibling().text();                                                 //System.out.println(numberLineStation + "  " + stationName + " " + nameLine);

            if(stationsObj.containsKey(numberLineStation)){
                stationsArrayOneLine = (JSONArray) stationsObj.get(numberLineStation);                                  //numberLineStation = Double.parseDouble(element.attr("data-sort-value"));
                stationsArrayOneLine.add(stationName);                                                                  // stationName = element.nextElementSibling().text();
            }
            else
            {
                stationsArrayOneLine = new JSONArray();
                stationsArrayOneLine.add(stationName);
                stationsObj.put(numberLineStation,stationsArrayOneLine);
            }
        }
        return stationsObj;
    }

    public static JSONArray createLineStationsJSON(Elements elements) {

        String colorLine;
        String nameLine;
        String numberLineStations;
        JSONArray linesArray = new JSONArray();
        JSONObject lineNumberNameColor = new JSONObject();

        HashSet<String> set = new HashSet<>();
        for (Element element : elements) {
            nameLine = element.select("span[title~=.+]").attr("title");
            numberLineStations = element.previousElementSibling().selectFirst(".sortkey").text();
            colorLine = element.parent().selectFirst("td").attr("style");
            colorLine = colorLine.substring(colorLine.indexOf(":") + 1);

            if(!set.contains(nameLine)){
                lineNumberNameColor.put("name", nameLine);
                lineNumberNameColor.put("number", numberLineStations);
                lineNumberNameColor.put("color", colorLine);
                linesArray.add(lineNumberNameColor);
                lineNumberNameColor = new JSONObject();
            }
            set.add(nameLine);

        }
        return linesArray;
    }

    private static HashMap readJsonFile(String path){

        HashMap<String,Integer> stationAndLines = new HashMap<>();
        try
        {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(path);
            JSONObject stationsObject = (JSONObject) jsonData.get("stations");

            stationsObject.keySet().forEach(lineNumber -> {
                String numberStation = (String) lineNumber;
                JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumber);

                stationAndLines.put(numberStation, stationsArray.size());

            });
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return stationAndLines;
    }

    private static String getJsonFile(String dataFile)
    {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(dataFile));
            lines.forEach(line -> builder.append(line));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    private static void printCountStation(HashMap<String, Integer> map, JSONArray array){

        map.keySet().forEach(number ->{
            array.forEach(lineObject -> {
                JSONObject lineJsonObject = (JSONObject) lineObject;
                String numberJson = (String)lineJsonObject.get("number");
                if(numberJson.equals(number)){
                    String nameLine = (String)lineJsonObject.get("name");
                    System.out.println(nameLine + " --- " + map.get(number) + " станций");
                }
            });

        });
    }
}


