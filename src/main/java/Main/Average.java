package Main;

public class Average {

    private int summary;
    private int visits;


    public Average(int summary, int visits) {
        this.summary = summary;
        this.visits = visits;
    }

    public void addOneVisit(int seconds){
        summary +=seconds;
        visits++;
    }

    public void addSummary(int s){
        summary += s;
    }
    public void addVisits(int v){
        visits += v;
    }
    public int getValue(){
        int value = (visits>0)? summary/visits: -1;
        double average = summary/visits;
        value = (int) Math.round(average);

        return value;
    }
}
