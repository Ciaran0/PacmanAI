PacmanAI
========

[CA318] Advanced Algorithms and AI Assignment.  
To build a world and simple mind for [w2mind.computing.dcu](http://w2mind.computing.dcu.ie/)  

World is a pacman style world where pacman is controlled by the mind and everything else is controlled by the world, including the ghosts.

###Approach taken
I used A* algorithim with manhatten distance as the heuristic. 

#Wizard style PacMan

##Overview

Game is mainly traditional to original pacman. Paman is the broom. 
He is chased by 4 ghosts or in this game four snakes.
The wizard has 3 lives. He loses a life evry time a snake catches him
The wizard has to collect the dots which count 1 point towards the score
Potions appear in the game, they are worth 12 points towards the score.

##State

State in this game is 2 dimensialal grid in which all elements are represented

Wall = 1
Edible dot = 0
Dot eaten = 2
Door / portal = 3
Potion = 4
wizard = 5
Snake 1 = 6
Snake 2 = 7
Snake 3 = 8
Snake 4 = 9

##Action

Action gets an action from the mind. For this word this is an int 6 -9

Move down = 6
Move up = 7
Move right = 8
Move left = 9

##Score Format

The score is based off how many dots and potions you collect
Dots are worth 1 point towards the score. Potions 12.

