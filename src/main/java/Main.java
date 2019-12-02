import StationsAndLines.Line;
import StationsAndLines.LineWithStations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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

        int maxBodySize = 20480000;
        String path = "stations.json";
        HashMap<String, Integer> stationsAndLines;
        String urlSite = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";

        try {
            Document doc = Jsoup.connect(urlSite).maxBodySize(maxBodySize).get();
            Elements elements = doc.select(".standard span[title~=[а-яА-Я]+ линия$]");

            LineWithStations lineWithStation = createLineWithStations(elements);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter write = new FileWriter(path);
            gson.toJson(lineWithStation, write);
            write.close();

            /*ObjectMapper mapper = new ObjectMapper();                                             Попробывал с маппером, но он по другому отображает текст чем в оригинале.
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), lineWithStation);*/

            String fileStationsJson = getJsonFile(path);
            stationsAndLines = getLinesWithStationsFromJSON(fileStationsJson);

            printCountStation(stationsAndLines);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LineWithStations createLineWithStations(Elements elements) {

        String lineName;
        String lineNumber;
        String lineColor;
        String stationName;
        HashMap<String, ArrayList<String>> stations = new HashMap<>();
        ArrayList<Line> linesList = new ArrayList<>();

        for (Element element : elements) {
            lineName = element.select("span[title~=.+]").attr("title");
            lineNumber = element.previousElementSibling().selectFirst(".sortkey").text();
            lineColor = element.parent().selectFirst("td").attr("style");
            lineColor = lineColor.substring(lineColor.indexOf(":") + 1);
            stationName = element.parent().nextElementSibling().text();

            if (lineNumber.indexOf("0") == 0)
                lineNumber = lineNumber.replace("0", "");

            if (!stations.containsKey(lineNumber)){
                stations.put(lineNumber, new ArrayList<>());
                stations.get(lineNumber).add(stationName);
                linesList.add(new Line(lineNumber, lineName, lineColor));
            }
            else{
                stations.get(lineNumber).add(stationName);
            }
        }
        return new LineWithStations(stations, linesList);
    }

    private static HashMap getLinesWithStationsFromJSON(String data){

        HashMap<String,Integer> stationAndLines = new HashMap<>();
        try
        {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(data);
            JSONObject stationsObject = (JSONObject) jsonData.get("stations");
            JSONArray lines = (JSONArray) jsonData.get("lines");

            lines.forEach(line ->{
                JSONObject lineJsonObject = (JSONObject) line;
                String numberLineJson = (String)lineJsonObject.get("number");
                String nameLine = (String)lineJsonObject.get("name");

                stationsObject.keySet().forEach(lineNumber -> {
                    JSONArray stationsArray = (JSONArray)stationsObject.get(numberLineJson);
                    stationAndLines.put(nameLine, stationsArray.size());
                });

            });

        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return stationAndLines;
    }

    private static String getJsonFile(String path)
    {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            lines.forEach(line -> builder.append(line));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

    private static void printCountStation(HashMap<String, Integer> map){

        map.keySet().forEach(name ->{
                    System.out.println(name + " --- " + map.get(name) + " станций");
        });
    }

}


