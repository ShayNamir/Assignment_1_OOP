public class ConcretePlayer implements Player{
    private boolean inOne;
    private int playerWins=0;
    public ConcretePlayer(boolean inPlayerOne){
        this.inOne=inPlayerOne;
    }
    @Override
    public boolean isPlayerOne() {
        return this.inOne;
    }

    @Override
    public int getWins() {
        return this.playerWins;
    }
    public void playerWon(){
        this.playerWins++;
    }
}
