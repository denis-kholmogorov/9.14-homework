package StationsAndLines;

public class Line implements Comparable<Line> {

    private String number;
    private String name;
    private String color;

    public Line(String number, String name, String color) {

        this.number = number;
        this.name = name;
        this.color = color;

    }

    public String getNumber()
    {
        return number;
    }

    public String getColor(){
        if(color.equals(""))
            return "ffffff";
        return (color) ;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int compareTo(Line line)
    {
        return number.compareToIgnoreCase(line.getNumber());
    }

    @Override
    public boolean equals(Object obj)
    {
        return compareTo((Line) obj) == 0;
    }
}

