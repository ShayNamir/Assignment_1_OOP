import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ConcretePiece implements Piece{
    protected Player owner;
    protected String type;
    private int distance=0;
    private int ID;
    private List<Position> posHistory = new ArrayList<>();

    public int getID(){return this.ID;}
    public void setID(int id){this.ID = id;}
    public List<Position> getPosHistory(){
        //Deep copy
        return new ArrayList<>(this.posHistory);
    }
    public Position getLastPos(){
        return this.posHistory.get(this.posHistory.size()-1);
    }
    public void addMove(Position pos){
        this.posHistory.add(new Position(pos.getX(), pos.getY()));
    }
    public int getDistance(){return this.distance;}
    public void addDistance(int dis){this.distance+=dis;}
    @Override
    public Player getOwner() {
        return this.owner;
    }
    public Position undo(){
        Position ans =this.posHistory.get(this.posHistory.size()-1);
        this.posHistory.remove(this.posHistory.size()-1);
        return ans;
    }
    public List<Position> CleanMinusOne(){
        //Clear the -1 and the first position? (the for start in 1)
        List<Position> ans= new ArrayList<>();
        for (Position position : this.posHistory) {
            if (position.getX() != -1)
                ans.add(position);
        }
        return ans;
    }
    public String movesString(){

        List<Position> clean = this.CleanMinusOne();
        if(clean.size()<2)
            return null;

        String ans;
        if(this instanceof King)
            ans="K";
        else if(this.owner.isPlayerOne())
            ans="D";
        else
            ans="A";
        ans+=this.ID+": [";
        for (Position pos : clean) {
            ans += "(" + pos.getX() + ", " + pos.getY() + "), ";
        }
        ans=ans.substring(0,ans.length()-2);//Delete: , and the space from the String
        ans+="]";
        return ans;
    }
}
//Comparators
class movesComparator implements Comparator<ConcretePiece> {
    public int compare(ConcretePiece a, ConcretePiece b){
        if(a.CleanMinusOne().size()!=b.CleanMinusOne().size())
            return a.CleanMinusOne().size() - b.CleanMinusOne().size();
        return  a.getID()-b.getID();
    }
}
class disComparator implements Comparator<ConcretePiece> {
    public int compare(ConcretePiece a, ConcretePiece b){
        if(b.getDistance() != a.getDistance())
            return b.getDistance() - a.getDistance();
        return  a.getID()-b.getID();
    }
}
