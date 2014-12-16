/*
Author: Ciaran O'Connor
Title: WizardPMind
CA318
All work is my own except some remaining snippets from "ImageMind"
 */

import org.w2mind.net.Action;
import org.w2mind.net.Mind;
import org.w2mind.net.RunError;
import org.w2mind.net.State;

import java.awt.*;
import java.util.Random;

public class WizardPMind implements Mind {




    int [][] grid = new int[22][22];
    private Point pacman;
    private Point ghost1;
    private Point ghost2;
    private Point ghost3;
    private Point ghost4;
    private int lastDirection ;
    private Random rand = new Random();


    public void newrun() throws RunError {
       lastDirection=0;
    }

    private Point pointGivenDirection(int direction){
        if(direction == 8){
            return new Point(pacman.x+1,pacman.y);

        }
        else if(direction == 9){
            return new Point(pacman.x-1,pacman.y);
        }
        else if(direction == 7){
            return new Point(pacman.x,pacman.y-1);
        }
        else if(direction == 6){
            return new Point(pacman.x,pacman.y+1);
        }
        else {
            return new Point(pacman.x,pacman.y);
        }
    }

    //ends a run
    public void endrun() throws RunError {
    }

    /**
     *
     * @param state the state of the game
     * @return
     */
    public Action getaction(State state) {
        //Edible dots = 0
        //Dot eaten = 2
        //walls = 1
        //Teleport =3
        //Potion = 4
        //pacman = 5;
        //ghost1 = 6;
        //ghost2 = 7;
        //ghost3 = 8;
        //ghost4 = 9;
        String s = state.toString();
        //get our grid into 2d array
        int b = 0;
        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 21; j++) {
                int val = Integer.parseInt(s.split("(?<=\\G.)")[b]);
                grid[i][j] = val;
                b++;
                if (val == 5) {
                    pacman = new Point(j,i);
                }
                else if(val == 6){
                    ghost1 = new Point(j,i);
                }
                else if(val == 7){
                    ghost2 = new Point(j,i);
                }
                else if(val == 8){
                    ghost3 = new Point(j,i);
                }
                else if(val == 9){
                    ghost4 = new Point(j,i);
                }
            }
        }
        if(lastDirection == 0){
            Point p;
            int direction;
            do{
                direction = rand.nextInt((9 - 6) + 1) + 6;
                p = pointGivenDirection(direction);
            }while (!isMoveValid(p.y,p.x));
            lastDirection = direction;
        }
        //action to send back

        //find where to go
        lastDirection = randomValidMove();
        String a = String.format("%d", lastDirection);

        return new Action(a);
    }

    private int  randomValidMove(){

        int move = lastDirection;
        Point p = pointGivenDirection(lastDirection);
        while(!isMoveValid(p.y,p.x)){
            move = rand.nextInt((9 - 6) + 1) + 6;
            p =pointGivenDirection(move);
        }
        return move;

    }

    private boolean isMoveValid(int h, int w){
        return grid[h][w]!=1;
    }




}





