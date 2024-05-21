import java.util.*;

public class Position {
    private int x;
    private int y;
    private int countSteps;

    public Position(int x, int y){
        this.x=x;
        this.y=y;
        this.countSteps=0;
    }
    public Position(int x, int y, int count){
        this.x=x;
        this.y=y;
        this.countSteps=count;
    }
    public int getX(){
        return this.x;
    }
    public  int getY(){
        return this.y;
    }
    public int getCountSteps(){
        return this.countSteps;
    }
}
//Comparators
class stepsComparator implements Comparator<Position> {
    public int compare(Position a, Position b){
        if(b.getCountSteps() != a.getCountSteps())
            return b.getCountSteps() - a.getCountSteps();
        if(a.getX()!=b.getX())
            return a.getX()-b.getX();
        return a.getY()-b.getY();
    }
}
