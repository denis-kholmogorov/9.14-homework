package StationsAndLines;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class LineWithStations
{

    @JsonProperty("stations")
    private HashMap<String, ArrayList<String>> stations;

    @JsonProperty("lines")
    private ArrayList<Line> lines;

    public LineWithStations(HashMap<String, ArrayList<String>> stations, ArrayList<Line> lines )
    {
        this.stations = stations;
        this.lines = lines;
    }


    public void addStation(HashMap<String, ArrayList<String>> stations)
    {
        this.stations = stations;
    }

    public HashMap<String, ArrayList<String>> getStations()
    {
        return stations;
    }

    public void addLines(ArrayList<Line> lines)
    {
        this.lines = lines;
    }

    public ArrayList<Line> getLines()
    {
        return lines;
    }

}