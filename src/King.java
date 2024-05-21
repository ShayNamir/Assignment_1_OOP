public class King extends ConcretePiece{
    public King(Player player){
        this.owner=player;
        this.type = "♔";
    }
    @Override
    public String getType() {
        return this.type;
    }
}
