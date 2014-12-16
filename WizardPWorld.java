/*
Author: Ciaran O'Connor
Student number: 12326096
Title: WizardPWorld
CA318
All work is my own except some remaining snippets from "ImageWorld"
 */


import org.w2mind.net.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;




public class WizardPWorld extends AbstractWorld {

    /**
     * Nested class Node
     * Used in A* search and calculating manhattan distance as heuristic
     */
    private class Node{
        Node parent;
        Point location;
        int heuristic;
        int gScore;
        int fcost;

        Node(){}

        Node(Point location){
            this.location =  location;
        }

        public void calcFcost(){
            calcG();
            calcHeuristic();
            this.fcost = gScore+heuristic;
        }

        private void calcG(){
            this.gScore = parent.gScore +10;
        }

        @Override
        public boolean equals(Object object)
        {
            boolean same = false;

            if (object != null && object instanceof Node)
            {
                same = this.location.equals(((Node) object).location);
            }

            return same;
        }
        //calculate manhattan distance
        public void calcHeuristic(){
            //steps vertically + horizontally to target
            int horizontal = Math.abs(pacman.x-location.x);
            int vertical = Math.abs(pacman.y-location.y);
            this.heuristic = (horizontal+vertical)*10;
        }
    }

    public static final int GRID_SIZE = 21; //21*21 2d
    private Point pacman;
    private Point ghost1;
    private Point ghost2;
    private Point ghost3;
    private Point ghost4;
    private int lives = 3;
    private int score = 0; //point =1 potion=12
    private int numDots = 177;
    private int lastScorePotionAdded;


    int pacManGrid[][]  = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, //Row 1
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,0,1,1,0,1,1,1,0,1,1,0,1,1,1,0,1},
            {1,0,1,1,1,0,1,1,0,1,1,1,0,1,1,0,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,1,0,1,1,0,1,1,1,0,1,1,0,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,0,1,1,1,0,1,1,1,0,1,1,1,0,1,1,1,1},
            {1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1},
            {1,1,1,1,0,1,0,1,1,1,1,1,1,1,0,1,0,1,1,1,1},
            {3,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,3}, //portal
            {1,1,1,1,0,1,0,1,1,1,1,1,1,1,0,1,0,1,1,1,1},
            {1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1},
            {1,1,1,1,0,1,0,1,1,1,1,1,1,1,0,1,0,1,1,1,1},
            {1,0,0,0,0,1,0,1,1,1,1,1,1,1,0,1,0,0,0,0,1},
            {1,0,1,1,1,0,0,0,0,1,1,1,0,0,0,0,1,1,1,0,1},
            {1,0,0,0,0,0,1,1,0,1,1,1,0,1,1,0,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,1,0,1},
            {1,0,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1} //Row 21
      //col 1                                      //21
    };

    //Edible dots = 0
    //Dot eaten = 9
    //walls = 1
    //door =3
    //Potion = 4


    List<String> scorecols;


    //Actions
    public static final int ACTION_LEFT = 9;
    public static final int ACTION_RIGHT = 8;
    public static final int ACTION_UP = 7;
    public static final int ACTION_DOWN = 6;
    public static final int NO_ACTIONS = 5;

    //Directory
    private String SUPPORT_DIR = "images";

    //image files
    private String IMG_PACMAN = SUPPORT_DIR + "/pacmanH.png";
    private String IMG_GHOSTR = SUPPORT_DIR + "/snake.png";
    private String IMG_END = SUPPORT_DIR + "/LoseVolda.png";
    private String IMG_WALL = SUPPORT_DIR + "/wall.png";
    private String IMG_BACKGROUND = SUPPORT_DIR + "/background.jpg";
    private String IMG_GHOSTP = SUPPORT_DIR + "/snake.png";
    private String IMG_DOT = SUPPORT_DIR + "/dot.png";
    private String IMG_POTION = SUPPORT_DIR + "/potion.png";
    private String IMG_WIN = SUPPORT_DIR + "/win.png";
    private String IMG_DOOR = SUPPORT_DIR + "/door.png";

    // transient - don't serialise these:
    private transient ArrayList<BufferedImage> buf;
    private transient InputStream pacManStream, ghostStream,ghost2Stream, endStream,wallStream, backgroundStream, dotStream,
            potionStream,winStream,doorStream;
    private transient BufferedImage pacManImg, ghostImg, endImg, wallImg, backgroundImg, ghost2Img, dotImg, potionImg,
            winImg, doorImg;
    private int imgwidth, imgheight;


    private void initPos() {
        // x = w, y = h
        ghost1 = new Point(6,8);
        ghost2 = new Point(19,19);
        ghost3 = new Point(8,19);
        ghost4 = new Point(1,1);
        pacman = new Point(10,12);
    }


    private int moveGhost1(int pacmanDirection){
        //ghost one targets pacman directly
        return moveGhost(ghost1,pacman);
    }

    /**
     * Ghost two is a flanker. It Targets the block 4 ahead of pacman
     * If the distance is equal to 4 then he targets pacman directly
     * @param pacmanDirection the direction pacman is travelling
     * @return int the direction to travel
     */
    private int moveGhost2(int pacmanDirection){

        //targets 4 spaces ahead of pacman
        Point target = new Point(pacman.x,pacman.y);
        if(pacmanDirection==ACTION_RIGHT){
            target.x=target.x+4;
        }
        else if(pacmanDirection==ACTION_LEFT){
            target.x=target.x-4;
        }
        else if(pacmanDirection==ACTION_DOWN){
            target.y=target.y+4;
        }
        else{
            target.y=target.y-4;
        }
        return moveGhost(ghost2,target);
    }

    /**
     * choices the direction the ghost will travel
     * gets vertical and horizontal distance and tries to take the longest
     * @param ghost the ghost to move
     * @param target point (x,y) we are targetting
     * @return int direction to travel
     */
    private int moveGhost(Point ghost,Point target){
        int horizontalDistance;
        int verticalDistance;
        verticalDistance = Math.abs(ghost.y - target.y);
        horizontalDistance = Math.abs(ghost.x - target.x);
        //horizontal distance using portal
        if((verticalDistance >= horizontalDistance)){
            if((ghost.y < target.y)&&isMoveValid(ghost.y+1,ghost.x)){
                return ACTION_DOWN;
            }
            else if ((ghost.y > target.y)&&isMoveValid(ghost.y-1,ghost.x)){
                return ACTION_UP;
            }
            else if((ghost.x < target.x)&&isMoveValid(ghost.y,ghost.x+1)){
                return ACTION_RIGHT;
            }
            else{
                return ACTION_LEFT;
            }
        }
        else { //h w
            if(ghost.x<target.x&&isMoveValid(ghost.y,ghost.x+1)){
                return ACTION_RIGHT;
            }
            else if(ghost.x>target.x&&isMoveValid(ghost.y,ghost.x-1)) {
                return ACTION_LEFT;
            }
            else if(ghost.y < target.y&&isMoveValid(ghost.y+1,ghost.x)){
                return ACTION_DOWN;
            }
            else {
                return ACTION_UP;
            }
        }
    }

    /**
     *
     * @param point a point (x,y) ob the map
     * @return whats at the points position on the grid
     */
    private int gridPos(Point point){
        return pacManGrid[point.y][point.x];
    }

    /**
     * uses a* algorithm with manhatten distance heuristic
     * to find the shortest path to the target
     * @param ghost the ghost to move
     * @param target the target for the ghost
     * @return the next block on the shortest path
     */
    private Point aStarMove(Point ghost, Point target){
        //lists
        ArrayList<Node> openList = new ArrayList<Node>();
        ArrayList<Node> closedList = new ArrayList<Node>();

        //adjacent nodes
        Node adjacentNodes[] = new Node[4];


        //1) Add the starting square (or node) to the open list.
        Node currentNode = new Node(ghost);
        currentNode.gScore=10;
        currentNode.calcHeuristic();
        currentNode.fcost = currentNode.heuristic +currentNode.gScore;
        openList.add(currentNode);

        //2) Repeat the following:
        while((!currentNode.location.equals(target))||(openList.isEmpty())){
            //a) Look for the lowest F cost square on the open list. We refer to this as the current square.
            Node lowestCost=openList.get(0);
            int cost = lowestCost.fcost;
            for(int i=0;i<openList.size();i++){
                Node n = openList.get(i);
                if(n.fcost<=cost){
                    cost = n.fcost;
                    lowestCost = n;
                }
            }
            //b) Switch it to the closed list.

            currentNode = lowestCost;
            closedList.add(lowestCost);
            openList.remove(lowestCost);

            //c) For each of the 4 squares adjacent to this current square
            //If it is not walkable or if it is on the closed list, ignore it. Otherwise do the following.
            //If it isn’t on the open list, add it to the open list.
            // Make the current square the parent of this square. Record the F, G, and H costs of the square.
            adjacentNodes[0] = new Node(new Point((currentNode.location.x+1),currentNode.location.y)); //right
            adjacentNodes[1] = new Node(new Point((currentNode.location.x-1),currentNode.location.y)); //left
            adjacentNodes[2] = new Node(new Point(currentNode.location.x,(currentNode.location.y-1))); //up
            adjacentNodes[3] = new Node(new Point((currentNode.location.x),(currentNode.location.y+1))); //down

            for(int i=0;i<4;i++){

                if((gridPos(adjacentNodes[i].location)!=1)
                        &&(!closedList.contains(adjacentNodes[i])))
                {
                    if(!openList.contains(adjacentNodes[i])){
                        //set the parent
                        adjacentNodes[i].parent =currentNode;
                        //calc the heuristic
                        adjacentNodes[i].calcFcost();
                        openList.add(adjacentNodes[i]);
                    }
                }
            }


        }

        //d) Stop when you:
        //Add the target square to the closed list, in which case the path has been found (see note below), or
        //Fail to find the target square, and the open list is empty. In this case, there is no path.
        Node path = currentNode;
       do{
            path = path.parent;
        }while(!path.parent.location.equals(ghost));
        return path.location;
    }

    /**
     * @param h height
     * @param w width
     * @return true if there isnt a wall at a given point
     */
    private boolean isMoveValid(int h, int w){
        return pacManGrid[h][w]!=1;
    }

    /**
     * moves a point
     * @param startpos Point(x,y) to move on the map
     * @param direction direction in which the point will travel
     * movement has already been validated
     * @return the new point on the map
     */
    private Point move(Point startpos, int direction) {
        //have to check for portal
        Point portalL = new Point(0,10);
        Point portalR = new Point(20,10);
        if (direction == ACTION_LEFT){
            startpos.x = startpos.x-1;
        }
        if (direction == ACTION_RIGHT) {
            startpos.x = startpos.x +1;
        }
        if(direction == ACTION_DOWN){
            startpos.y = startpos.y +1;
        }
        if(direction == ACTION_UP){
            startpos.y = startpos.y -1;
        }
        if(startpos.equals(portalL)){
            startpos.setLocation(portalR);
        }
        else if(startpos.equals(portalR)){
            startpos.setLocation(portalL);
        }
        return startpos;
    }

    //is the run finished
    private boolean runFinished() {
        return (won() || lost());
    }

    /**
     * has pacman been eaten by a ghost?
     * @return true if a ghost is at the location of pacman
     */
    private boolean eaten(){
        if(isGhostInPosition(pacman)){
            return true;
        }
        return false;
    }

    /**
     * @param pos position to check
     * @return is there a ghost in this position
     */
    private boolean isGhostInPosition(Point pos){
        if(pos.x == ghost1.x && pos.y == ghost1.y){
            return true;
        }
        if(pos.x == ghost2.x && pos.y == ghost2.y){
            return true;
        }
        if(pos.x == ghost3.x && pos.y == ghost3.y){
            return true;
        }
        if(pos.x == ghost4.x && pos.y == ghost4.y){
            return true;
        }
        return false;
    }

    /**
     * if pacman is on a dot or potion remove the dot
     * increase the score
     */
    private void updateScore(){
        if(pacManGrid[pacman.y][pacman.x]==0){
            score++;
            numDots--;
            pacManGrid[pacman.y][pacman.x]=2;
        }
        else if(pacManGrid[pacman.y][pacman.x]==4){
            score = score +12;
            pacManGrid[pacman.y][pacman.x]=9;
        }
    }

    /**
     * Adda potion to the game
     * a potion is 12 times as much as a dot
     */
    public void addPotion(){
        //when they get to x score add a potion in a random position
        int x;
        int y;
        Point potion;
        if(score>=7 && score%7==0){
            //get a random point
            Random rand = new Random();
            do{
                x = rand.nextInt((19 - 1) + 1) + 1;
                y = rand.nextInt((19 - 1) + 1) + 1;
                potion = new Point(x,y);
            }while((isGhostInPosition(potion))||(pacManGrid[potion.y][potion.x]==1)
                    ||(potion.equals(pacman)||lastScorePotionAdded==score));
            //place a potion in our map
            pacManGrid[potion.y][potion.x] = 4;
            lastScorePotionAdded = score;
        }
    }

    //initialise image
    //most of this code taken from imageworld
    private void initImages()                    // sets up new buffer to hold images
    {
        if (imagesDesired) {
            buf = new ArrayList<BufferedImage>();            // buffer is cleared for each timestep, multiple images per timestep

            if (pacManStream == null)                    // block is only executed once (only read from disk once)
            {
                try {
                    ImageIO.setUseCache(false);        // use memory, not disk, for temporary images

                    pacManStream = getClass().getResourceAsStream(IMG_PACMAN);         // read from disk
                    ghostStream = getClass().getResourceAsStream(IMG_GHOSTR);
                    ghost2Stream =getClass().getResourceAsStream(IMG_GHOSTP);
                    endStream = getClass().getResourceAsStream(IMG_END);
                    wallStream = getClass().getResourceAsStream(IMG_WALL);
                    backgroundStream = getClass().getResourceAsStream(IMG_BACKGROUND);
                    dotStream = getClass().getResourceAsStream(IMG_DOT);
                    potionStream = getClass().getResourceAsStream(IMG_POTION);
                    winStream = getClass().getResourceAsStream(IMG_WIN);
                    doorStream = getClass().getResourceAsStream(IMG_DOOR);



                    pacManImg = javax.imageio.ImageIO.read(pacManStream);
                    ghostImg = javax.imageio.ImageIO.read(ghostStream);
                    ghost2Img =javax.imageio.ImageIO.read(ghost2Stream);
                    endImg = javax.imageio.ImageIO.read(endStream);
                    wallImg = javax.imageio.ImageIO.read(wallStream);
                    backgroundImg = javax.imageio.ImageIO.read(backgroundStream);
                    dotImg = javax.imageio.ImageIO.read(dotStream);
                    potionImg = javax.imageio.ImageIO.read(potionStream);
                    winImg =javax.imageio.ImageIO.read(winStream);
                    doorImg = javax.imageio.ImageIO.read(doorStream);


                    imgwidth = wallImg.getWidth();        // dimensions of jpg covering one square of the grid
                    imgheight = wallImg.getHeight();
                } catch (IOException e) {
                }
            }
        }
    }
    //if we have eaten/collected all the dots we have won
    private  boolean won(){
        return numDots ==0;
    }

    //if we are out of lives we have lost
    private boolean lost(){
        return lives==0;
    }

    //displays the images for the game
    //based on tiles of size 32x32
    private void addImage()            // adds image to buffer
    {
        if (imagesDesired) {
            BufferedImage img = new BufferedImage((imgwidth*21) , (imgheight*21) , BufferedImage.TYPE_INT_RGB);
            //background image
            img.createGraphics().drawImage(backgroundImg, 0, 0, null);
            //finish game
            if (runFinished()) {
                //we lost
                if(lost()){
                    img.createGraphics().drawImage(endImg, 0, 0, null);
                }
                //we won
                else if(won()){
                    img.createGraphics().drawImage(winImg, 0, 0, null);
                }
            }
            else {
                //Draw our pacman grid
                for (int i = 0; i < 21; i++) {
                    for (int j = 0; j < 21; j++) {
                        //add wall image
                        if (pacManGrid[i][j] == 1) {
                            img.createGraphics().drawImage(wallImg, (imgwidth * j), (imgheight * i), null);
                        }
                        //add dot image
                        if (pacManGrid[i][j] == 0) {
                            img.createGraphics().drawImage(dotImg, (imgwidth * j), (imgheight * i), null);
                        }
                        //add potion image
                        if(pacManGrid[i][j] == 4){
                            img.createGraphics().drawImage(potionImg, (imgwidth * j), (imgheight * i), null);
                        }
                        //add door image
                        if(pacManGrid[i][j] ==3){
                            img.createGraphics().drawImage(doorImg, (imgwidth * j), (imgheight * i), null);
                        }
                    }
                }
                //draw elements
                //show our ghosts (snakes)
                img.createGraphics().drawImage(ghostImg, (imgwidth * ghost1.x), imgheight * ghost1.y, null);
                img.createGraphics().drawImage(ghost2Img, (imgwidth * ghost2.x), imgheight * ghost2.y, null);
                img.createGraphics().drawImage(ghost2Img, (imgwidth * ghost3.x), imgheight * ghost3.y, null);
                img.createGraphics().drawImage(ghost2Img, (imgwidth * ghost4.x), imgheight * ghost4.y, null);
                //show our pacman (broom)
                img.createGraphics().drawImage(pacManImg, (imgwidth * pacman.x), imgheight * pacman.y, null);
                }

                buf.add(img);
            }

    }


    public void newrun() throws RunError {
        initPos();
        scorecols = new LinkedList<String>();
        scorecols.add("points");
    }




    public void endrun() throws RunError {
    }

    /**
     * Make sure all elements in the map then convert to string
     * @return the state, used by the mind
     * @throws RunError
     */
    public State getstate() throws RunError {
        //grid
        int[][] grid = new int[22][22];
        for (int i=0;i<21;i++){
            for(int j=0;j<21;j++){
                grid[i][j]=pacManGrid[i][j];
            }
        }
        //add our pacman & ghosts to the grid for niceness
        grid[pacman.y][pacman.x] = 5;
        grid[ghost1.y][ghost1.x] = 6;
        grid[ghost2.y][ghost2.x] = 7;
        grid[ghost3.y][ghost3.x] = 8;
        grid[ghost4.y][ghost4.x] = 9;

        //convert grid to string so we can send it as a state
        String x ="";
        for(int i=0;i<21;i++){
            for (int j=0;j<21;j++){
                x+=grid[i][j];
            }
        }

        return new State(x);
    }

    /**
     * Takes the action of the mind and does it
     * moves the ghosts
     * Displays the images
     * @param action
     * @return
     * @throws RunError
     */
    public State takeaction(Action action) throws RunError
    // Add any number of images to a list of images for this step.
    // The first image on the list for this step should be the image before we take the action.
    {
        initImages();            // If run with images off, imagesDesired = false and this does nothing.

        addImage();                // image before my move
        // If run with images off, imagesDesired = false and this does nothing.

        String s = action.toString();
        String[] a = s.split(",");

        int pacmanDirection = Integer.parseInt(a[0]);

        updateScore();
        addPotion();

        pacman = move(pacman, pacmanDirection);

        addImage();                // intermediate image, before opponent moves

        if(eaten()){
            lives--;
            initPos();                // loop round, new image will be shown in next step
        }else {
            ghost1 = move(ghost1, moveGhost1(pacmanDirection));
            ghost2 = move(ghost2,moveGhost2(pacmanDirection));
            ghost3 = aStarMove(ghost3,pacman);
            //ghost4 = moveGhost4(pacmanDirection);
            ghost4 = aStarMove(ghost4,pacman);
            // addImage(); 			// new image will be shown in next step

            if (eaten()) {
                lives--;
                addImage();            // show the "capture" image
                initPos();            // loop round, new image will be shown in next step
            }
        }

        if (runFinished())        // there will be no loop round
            addImage();

    // The last timestep of the run shows the final state, and no action can be taken in this state.
    // Whatever is the last image built on the run will be treated as the image for this final state.

        return getstate();
    }

    //gets the score of the game. Points collected
    public Score getscore() throws RunError {
        String s = String.format("%d", score);

        List<Comparable> values = new LinkedList<Comparable>();
        values.add(score);

        return new Score(s, runFinished(), scorecols, values);
    }


    public ArrayList<BufferedImage> getimage() throws RunError

    {
        return buf;
    }
}