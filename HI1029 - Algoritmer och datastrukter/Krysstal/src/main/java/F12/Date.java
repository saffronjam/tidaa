package F12;

public class Date implements Comparable<Date> {
    public int date;
    public int hour;

    public Date(int date, int hour) {
        this.date = date;
        this.hour = hour;
    }

    @Override
    public int compareTo(Date o) {
        return date == o.date ? hour - o.hour : date - o.date;
    }
}
