# Hog


<p align="center">
<img src="https://github.com/kamcbk/GeneralProjects/blob/master/hog/HogGUI.png">
</p>
<p align="center">
Hog GUI in action!
</p>
<br>
Hog is a small 2 player game where one uses simulated dice with a given set of rules: 

* Players roll a certain number of dice, up to 10, and the sum becomes the score for the turn.
* However there are a few caveats; if any of the dice outcomes is 1, the score for the turn is 1. This is known as Pig Out.
* One can choose to roll 0 dice. The score for the turn is 10 minus the min of the left and right most values of the opponent's score. This is known as Free Bacon
* If the number of dice you rolled is 2 away from the dice you rolled from the previous turn, you get 3 extra points. This is known as Feral Hogs.
* After all transactional points have been given, if by the end the left most and right most digit of your score multiplied equals the left and right most digit of your opponents score, you and your opponent swap scores. A single digit number has the same value for its left and right most digit. This is a Swine Swap.
* The winner of the game is the first person to reach 100 points


### Prerequisites

Python +3.3
A working terminal where you can run Python (Windows/non-terminal users can use GitBash: https://git-scm.com/downloads). (This way is my suggested way; there may be others but this is the only one I'm supporting)



### Running

To start the game, in your terminal type

```
python hog_gui.py
```
And a window should pop open with the game. The rules themselves are set up automatically in the program. If you find youself alone, you can play against me (or rather, my algorithm) by typing 

```
python hog_gui.py -f
```
See if you can beat it!


## Authors

* The actual game and a good chuck of the code (including the GUI) comes from one of the projects in UC Berkeley's CS61A course. Slight modeifications were done, however most of the game has come from the course. Kudos to the CS61A staff for making it.

* Kevin Marroquin


## Acknowledgments

* CS61A Staff
* Paul Hilfinger

