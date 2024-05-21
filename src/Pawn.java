import java.util.Comparator;

public class Pawn extends ConcretePiece{

    private int kills = 0;
    public Pawn(Player player){
        this.owner = player;
        this.type = "♟";
    }
    public void decreaseKills(int num){
        this.kills-=num;
    }
    public void kill(){
        this.kills++;
    }
    public int getKills(){
        return this.kills;
    }
    @Override
    public String getType() {
        return this.type;
    }
}
//Comparator
class killsComparator implements Comparator<Pawn> {
    public int compare(Pawn a, Pawn b){
        if(b.getKills() != a.getKills())
            return b.getKills() - a.getKills();

        return a.getID()-b.getID();
    }
}
