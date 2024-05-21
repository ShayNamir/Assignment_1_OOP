import java.util.*;

public class GameLogic implements PlayableLogic{
    /*
    Player one - red - attack
    Player two - blue - defense
     */
    private final int bordSize=11;
    private List<ConcretePiece> [][] stepCount = new ArrayList[this.bordSize][this.bordSize];

    public ConcretePiece [][] bord = new ConcretePiece[bordSize][bordSize];
    private Set<Pawn> killedPieces = new HashSet<>();
    private Stack<ConcretePiece> latMovedPiece= new Stack<>();
    private boolean secondTurn = true;
    private final ConcretePlayer firstPlayer = new ConcretePlayer(true);
    private final ConcretePlayer secondPlayer = new ConcretePlayer(false);

    public GameLogic(){
        this.reset();
    }

    @Override
    public boolean move(Position a, Position b) {
        //System.out.println("Piece: "+this.bord[a.getX()][a.getY()].getID());
        //printStat(this.secondPlayer);
        int ax=a.getX(), ay=a.getY(), bx=b.getX(), by=b.getY();
        //Check if this player turn
        if(this.bord[ax][ay].getOwner().isPlayerOne()==(this.secondTurn))
            return false;//Not his turn

        //Check if b point is in border
        if(bx<0||bx>this.bordSize||by<0||by>this.bordSize)
            return false;//Not in border

        //Check if b point is in straight line
        if(bx!=ax&&by!=ay)
            return false;// Diagonally move

        //Check if b point is available
        if(this.bord[bx][by]!=null)
            return false;

        //If is Pawn and b is in the corner
        if(this.bord[ax][ay] instanceof Pawn){
            if((bx==0&&by==0)||(bx==0&&by==this.bordSize-1)||(bx==this.bordSize-1&&by==0)||(bx==this.bordSize-1&&by==this.bordSize-1)){
                return false;
            }
        }

        //If there is a piece between
        if(ax==bx)
            for(int i=Math.min(ay,by)+1;i<Math.max(ay,by);i++){//Skip the edges
                if (this.bord[ax][i]!=null)
                    return false;
            }
        else{
            for(int i=Math.min(ax,bx)+1;i<Math.max(ax,bx);i++){//Skip the edges
                if (this.bord[i][ay]!=null)
                    return false;
            }
        }

        //Move
        this.bord[bx][by]=this.bord[ax][ay];
        this.bord[ax][ay]=null;
        stepCount[bx][by].add(this.bord[bx][by]);

        //Add to piece history
        this.bord[bx][by].addMove(b);

        //Update the piece distance
        if(ax==bx)
            this.bord[bx][by].addDistance(Math.abs(ay-by));
        else
            this.bord[bx][by].addDistance(Math.abs(ax-bx));
        //System.out.println("The pice distance: "+this.bord[bx][by].getDistance());

        //If king - can't eat
        if(this.bord[bx][by] instanceof King)//Can't eat
        {
            //The next player turn
            secondTurn = !secondTurn;
            //Add to the moved history
            this.latMovedPiece.add(this.bord[bx][by]);
            isGameFinished();
            return true;
        }
        eatNeighbor(b);
        //Add to the moved history
        this.latMovedPiece.add(this.bord[bx][by]);
        //The next player turn
        secondTurn = !secondTurn;

        //System.out.println("The pice distance: "+this.bord[bx][by].getDistance());
        //if(this.bord[bx][by] instanceof Pawn)
            //System.out.println("The pice Kills: "+((Pawn)(this.bord[bx][by])).getKills());

        //The move has done
        isGameFinished();
        return true;
    }
    public String test(){
        Position king=findTheKing();
        String s="King x:"+ king.getX()+" King Y:"+king.getY();
        return s;

        /*
        if(this.bord[king.getX()][king.getY()+1]==null&&this.bord[king.getX()][king.getY()+1].owner.isPlayerOne()==this.bord[king.getX()][king.getY()].owner.isPlayerOne())
            return false;
        if(this.bord[king.getX()][king.getY()-1]==null&&this.bord[king.getX()][king.getY()+1].owner.isPlayerOne()==this.bord[king.getX()][king.getY()].owner.isPlayerOne())
            return false;
        if(this.bord[king.getX()+1][king.getY()]==null&&this.bord[king.getX()][king.getY()+1].owner.isPlayerOne()==this.bord[king.getX()][king.getY()].owner.isPlayerOne())
            return false;
        if(this.bord[king.getX()-1][king.getY()]==null&&this.bord[king.getX()][king.getY()+1].owner.isPlayerOne()==this.bord[king.getX()][king.getY()].owner.isPlayerOne())
            return false;

        return true;

         */
    }
    @Override
    public Piece getPieceAtPosition(Position position) {
        return this.bord[position.getX()][position.getY()];
    }

    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    @Override
    public Player getSecondPlayer() {
        return secondPlayer;
    }

    @Override
    public boolean isGameFinished() {
        //System.out.println("ENTER" );
        //Check if player two won
        if(this.bord[0][0]!=null||this.bord[10][0]!=null||this.bord[0][10]!=null||this.bord[10][10]!=null){
            this.firstPlayer.playerWon();
            printStat(this.firstPlayer); //Print the stat
            return true;
        }

        //Check if player one won
        Position kingPos = findTheKing();
        int x= kingPos.getX(), y= kingPos.getY();
        boolean oneWon=true;
        oneWon = oneWon &&(x==0||this.bord[x-1][y] instanceof Pawn &&!this.bord[x-1][y].owner.isPlayerOne());//Left
        oneWon = oneWon && (x==10||this.bord[x+1][y] instanceof Pawn&&!this.bord[x+1][y].owner.isPlayerOne());//Right
        oneWon = oneWon &&(y==0||this.bord[x][y-1] instanceof Pawn&&!this.bord[x][y-1].owner.isPlayerOne());//Up
        oneWon = oneWon && (y==10||this.bord[x][y+1] instanceof Pawn&&!this.bord[x][y+1].owner.isPlayerOne());//Down
        if(oneWon){
            this.secondPlayer.playerWon();
            printStat(this.secondPlayer); //Print the stat
            //System.out.println("IS FINISH:YES" );
            return true;
        }
        //System.out.println("IS FINISH: NO" );
        return false;
    }

    @Override
    public boolean isSecondPlayerTurn() {
        return this.secondTurn;
    }

    @Override
    public void reset() {
        this.bord=new ConcretePiece[this.bordSize][this.bordSize];
        stepCountReset();
        //Second player - BLACK
        for(int i=3;i<8;i++){
            this.bord[i][0] = new Pawn(this.getSecondPlayer());
            stepCount[i][0].add(this.bord[i][0]);
            this.bord[i][10] = new Pawn(this.getSecondPlayer());
            stepCount[i][10].add(this.bord[i][10]);
            this.bord[0][i] = new Pawn(this.getSecondPlayer());
            stepCount[0][i].add(this.bord[0][i]);
            this.bord[10][i] = new Pawn(this.getSecondPlayer());
            stepCount[10][i].add(this.bord[10][i]);
        }
        this.bord[5][1] = new Pawn(this.getSecondPlayer());
        stepCount[5][1].add(this.bord[5][1]);
        this.bord[1][5] = new Pawn(this.getSecondPlayer());
        stepCount[1][5].add(this.bord[1][5]);
        this.bord[9][5] = new Pawn(this.getSecondPlayer());
        stepCount[9][5].add(this.bord[9][5]);
        this.bord[5][9] = new Pawn(this.getSecondPlayer());
        stepCount[5][9].add(this.bord[5][9]);

        //First player - WHITE
        this.bord[5][5] = new King(this.getFirstPlayer());
        stepCount[5][5].add(this.bord[5][9]);
        this.bord[5][3] = new Pawn(this.getFirstPlayer());
        stepCount[5][3].add(this.bord[5][9]);
        this.bord[5][7] = new Pawn(this.getFirstPlayer());
        stepCount[5][7].add(this.bord[5][9]);
        this.bord[4][4] = new Pawn(this.getFirstPlayer());
        stepCount[4][4].add(this.bord[5][9]);
        this.bord[5][4] = new Pawn(this.getFirstPlayer());
        stepCount[5][4].add(this.bord[5][9]);
        this.bord[6][4] = new Pawn(this.getFirstPlayer());
        stepCount[6][4].add(this.bord[5][9]);
        this.bord[3][5] = new Pawn(this.getFirstPlayer());
        stepCount[3][5].add(this.bord[5][9]);
        this.bord[4][5] = new Pawn(this.getFirstPlayer());
        stepCount[3][5].add(this.bord[5][9]);
        this.bord[6][5] = new Pawn(this.getFirstPlayer());
        stepCount[6][5].add(this.bord[5][9]);
        this.bord[7][5] = new Pawn(this.getFirstPlayer());
        stepCount[7][5].add(this.bord[5][9]);
        this.bord[4][6] = new Pawn(this.getFirstPlayer());
        stepCount[4][6].add(this.bord[5][9]);
        this.bord[5][6] = new Pawn(this.getFirstPlayer());
        stepCount[5][6].add(this.bord[5][9]);
        this.bord[6][6] = new Pawn(this.getFirstPlayer());
        stepCount[6][6].add(this.bord[5][9]);
        resetPieceHistory();
        setIDPiece();
        this.killedPieces.clear();
        this.secondTurn=true;
    }
    @Override
    public void undoLastMove() {
        printStat(this.firstPlayer);//
        if(this.latMovedPiece.empty())
            return;
        int killCount=0;
        ConcretePiece movedPiece=this.latMovedPiece.peek();
        //undo the last move
        Position to=movedPiece.undo();


        stepCount[to.getX()][to.getY()].remove(stepCount[to.getX()][to.getY()].size()-1);//Minus the last step

        Position from=this.latMovedPiece.pop().getLastPos();
        this.bord[to.getX()][to.getY()]=null;
        this.bord[from.getX()][from.getY()]=movedPiece;
        //Decrease the distance
        if(to.getX()==from.getX())
            movedPiece.addDistance(Math.abs(to.getY()-from.getY())*-1);
        else
            movedPiece.addDistance(Math.abs(to.getX()-from.getX())*-1);

        //Revive the dead
        while ((!this.latMovedPiece.empty())&&this.latMovedPiece.peek().getLastPos().getX()==-1){
            this.latMovedPiece.peek().undo();
            Position revive=this.latMovedPiece.peek().getLastPos();
            this.bord[revive.getX()][revive.getY()]=this.latMovedPiece.pop();
            killCount++;
        }
        //Fix kills
        ((Pawn)movedPiece).decreaseKills(killCount);

        //Fix distance
        if(to.getY()== from.getY())
            movedPiece.addDistance(Math.abs(to.getX()- from.getX())*-1);
        else
            movedPiece.addDistance(Math.abs(to.getY()- from.getY())*-1);

        //Fix the number of steps in the position
        this.bord[from.getX()][from.getY()]=movedPiece;
        //Change the player turn
        this.secondTurn=!this.secondTurn;
    }

    @Override
    public int getBoardSize() {
        return this.bordSize;
    }
    private void eatNeighbor(Position pos){
        int x = pos.getX(), y= pos.getY();

        //Up neighbor
        if(y>0&&this.bord[x][y-1] instanceof Pawn &&this.bord[x][y].owner.isPlayerOne()!=this.bord[x][y-1].owner.isPlayerOne()){
            if(y-1==0)//On the edge - eat
            {
                eatSpecific(new Position(x,y-1),this.bord[pos.getX()][pos.getY()]);
            }
            //Enemy between them - eat
            else if(this.bord[x][y-2] instanceof Pawn && this.bord[x][y-2].owner.isPlayerOne()==this.bord[x][y].owner.isPlayerOne())
            {
                eatSpecific(new Position(x,y-1),this.bord[pos.getX()][pos.getY()]);
            }
        }

        //Down neighbor
        if(y<10&&this.bord[x][y+1] instanceof Pawn &&this.bord[x][y].owner.isPlayerOne()!=this.bord[x][y+1].owner.isPlayerOne()){
            if(y+1==10)//On the edge - eat
            {
                eatSpecific(new Position(x,y+1),this.bord[pos.getX()][pos.getY()]);
            }
            //Enemy between them - eat
            else if(this.bord[x][y+2] instanceof Pawn &&this.bord[x][y+2].owner.isPlayerOne()==this.bord[x][y].owner.isPlayerOne())
            {
                eatSpecific(new Position(x,y+1),this.bord[pos.getX()][pos.getY()]);
            }
        }

        //Right neighbor
        if(x<10&&this.bord[x+1][y] instanceof Pawn &&this.bord[x][y].owner.isPlayerOne()!=this.bord[x+1][y].owner.isPlayerOne()){
            if(x+1==10)//On the edge - eat
            {
                eatSpecific(new Position(x+1,y),this.bord[pos.getX()][pos.getY()]);
            }
            //Enemy between them - eat
            else if(this.bord[x+2][y] instanceof Pawn &&this.bord[x+2][y].owner.isPlayerOne()==this.bord[x][y].owner.isPlayerOne())
            {
                eatSpecific(new Position(x+1,y),this.bord[pos.getX()][pos.getY()]);
            }
        }
        //Left neighbor
        if(x>0&&this.bord[x-1][y] instanceof Pawn &&this.bord[x][y].owner.isPlayerOne()!=this.bord[x-1][y].owner.isPlayerOne()){
            if(x-1==0)//On the edge - eat
            {
                eatSpecific(new Position(x-1,y),this.bord[pos.getX()][pos.getY()]);
            }
            //Enemy between them - eat
            else if(this.bord[x-2][y] instanceof Pawn && this.bord[x-2][y].owner.isPlayerOne()==this.bord[x][y].owner.isPlayerOne())
            {
                eatSpecific(new Position(x-1,y),this.bord[pos.getX()][pos.getY()]);
            }
        }
    }
    private void eatSpecific(Position position, ConcretePiece eat){

        ((Pawn)(eat)).kill();
        this.killedPieces.add(((Pawn)(eat)));
        //Add to the moved stack
        this.latMovedPiece.add(this.bord[position.getX()][position.getY()]);
        //*MARK (-1,-1) AS DEAD*
        this.bord[position.getX()][position.getY()].addMove(new Position(-1,-1));
        //Delete from bord
        this.bord[position.getX()][position.getY()]=null;

    }
    private Position findTheKing(){
        for(int i=0;i<this.bordSize;i++){
            for(int j=0;j<this.bordSize;j++)
            {
                if(this.bord[i][j] instanceof King)
                    return new Position(i,j);
            }
        }
        return new Position(-1,-1);//Mark as error
    }
    private void resetPieceHistory(){
        for(int i=0;i<this.bordSize;i++){
            for(int j=0;j<this.bordSize;j++){
                if(this.bord[i][j]!=null){
                    this.bord[i][j].addMove(new Position(i,j));
                }
            }
        }
    }
    private void setIDPiece(){
        int firstCounter=1, secondCounter=1;
        for(int i=0;i<this.bordSize;i++){
            for(int j=0;j<this.bordSize;j++){
                if(this.bord[j][i]!=null){
                    if(this.bord[j][i].owner.isPlayerOne())
                        this.bord[j][i].setID(firstCounter++);
                    else
                        this.bord[j][i].setID(secondCounter++);
                }
            }
        }
    }
    private void printStat(Player wonP){

        //Section 1

        // Use a Set to filter out duplicates
        Set<ConcretePiece> uniqueSet = new HashSet<>(this.latMovedPiece);

        //Get the pieces that's on the bord
        List<ConcretePiece> pieceList= new ArrayList<>(uniqueSet);

        //Sort by number of moves
        pieceList.sort(new movesComparator());

        //First the winning player
        for(int i=0;i<pieceList.size();i++){
            if(pieceList.get(i).getOwner().isPlayerOne()==wonP.isPlayerOne()){
                String print =pieceList.get(i).movesString();
                if(print!=null)
                    System.out.println(print);
            }
        }

        //Second the loosing player
        for(int i=0;i<pieceList.size();i++){
            if(pieceList.get(i).getOwner().isPlayerOne()!=wonP.isPlayerOne()){
                String print =pieceList.get(i).movesString();
                if(print!=null)
                    System.out.println(print);
            }
        }
        System.out.println("***************************************************************************");

        //Section 2
        List<Pawn> sortByKill = new ArrayList<>(this.killedPieces);
        //Sort by kill
        sortByKill.sort(new killsComparator());
        //Sort by winning player
        sortByWin2(sortByKill,wonP);

        for(int i=0;i<sortByKill.size();i++){
            if(sortByKill.get(i).getKills()<1)//There is a chance that will be a paw thats eat and redo it and still will be in that set
                break;
            String print="";
                if(sortByKill.get(i).getOwner().isPlayerOne())
                    print+="D";
                else
                    print+="A";
                print+=sortByKill.get(i).getID()+": "+sortByKill.get(i).getKills()+" kills";
                System.out.println(print);
        }

        System.out.println("***************************************************************************");

        //Section 3
        // Use a Set to filter out duplicates
        uniqueSet = new HashSet<>(this.latMovedPiece);

        //Get the pieces that's on the bord
        pieceList= new ArrayList<>(uniqueSet);
        //Sort
        pieceList.sort(new disComparator());
        //Sort by winning player
        sortByWin3(pieceList,wonP);

        for(int i=0;i<pieceList.size();i++){
            if(pieceList.get(i).getDistance()<1) {//There is a chance that will be a piece thats didnt move (eat and revive)
                i = pieceList.size();//skip to the end of the loop because sorted
                break;
            }
            String print="";
            if(pieceList.get(i) instanceof King)
                print+="K";
            else if(pieceList.get(i).getOwner().isPlayerOne())
                print+="D";
            else
                print+="A";
            print+=pieceList.get(i).getID()+": "+pieceList.get(i).getDistance()+" squares";
            System.out.println(print);
        }

        System.out.println("***************************************************************************");

        //Section 4

        List<Position> steps=getStepCountList();
        //System.out.println("THS SIZE IS: "+steps.size());
        for(int i=0;i<steps.size();i++){
            String print ="(";
            print+=steps.get(i).getX()+", ";
            print+=steps.get(i).getY()+")";
            print+=steps.get(i).getCountSteps()+" pieces";
            System.out.println(print);
        }
        System.out.println("***************************************************************************");
    }
    private void sortByWin2(List<Pawn> list,Player wonP){
        for(int i=1;i<list.size();i++){//Start at 1
            Pawn a=list.get(i-1), b=list.get(i);
            if(a.getKills()==b.getKills()&&a.getID()==b.getID()&&a.getOwner().isPlayerOne()!=wonP.isPlayerOne()){
                //Swap
                Pawn temp = list.remove(i-1);
                list.add(i,temp);
            }
        }
    }
    private void sortByWin3(List<ConcretePiece> list,Player wonP){
        for(int i=1;i<list.size();i++){//Start at 1
            ConcretePiece a=list.get(i-1), b=list.get(i);
            if(a.getDistance()==b.getDistance()&&a.getID()==b.getID()&&a.getOwner().isPlayerOne()!=wonP.isPlayerOne()){
                //Swap
                ConcretePiece temp = list.remove(i-1);
                list.add(i,temp);
            }
        }
    }
    private void stepCountReset(){
        for(int i=0;i<this.stepCount.length;i++){
            for(int j=0;j<this.stepCount[0].length;j++){
                this.stepCount[i][j]=new ArrayList<>();
            }
        }
    }
    private List<Position>getStepCountList(){
        List<Position> ans = new ArrayList<>();
        for(int i=0;i<this.stepCount.length;i++){
            for(int j=0;j<this.stepCount[0].length;j++){
                int numberOfSteps=new HashSet<>(stepCount[i][j]).size();
                if(numberOfSteps>1)//To avoid duplicates
                //if(stepCount[i][j].size()>1)
                    ans.add(new Position(i,j,numberOfSteps));
            }
        }
        ans.sort(new stepsComparator());
        return ans;
    }
}
