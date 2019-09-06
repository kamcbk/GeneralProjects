# Hog


![alt text](https://github.com/kamcbk/GeneralProjects/blob/master/hog/HogGUI.png)
<p align="center">
<img src="https://github.com/kamcbk/GeneralProjects/blob/master/hog/HogGUI.png">
</p>

Hog is a small 2 player game where one uses simulated dice with a given set of rules: 

* Players roll a certain number of dice, up to 10, and the sum becomes the score for the turn.
* However there are a few caveats; if any of the dice outcomes is 1, the score for the turn is 1. This is known as Pig Out.
* One can choose to roll 0 dice. The score for the turn is 10 minus the min of the left and right most values of the opponent's score. This is known as Free Bacon
* If the number of dice you rolled is 2 away from the dice you rolled from the previous turn, you get 3 extra points. This is known as Feral Hogs.
* After all transactional points have been given, if by the end the left most and right most digit of your score multiplied equals the left and right most digit of your opponents score, you and your opponent swap scores. A single digit number has the same value for its left and right most digit. This is a Swine Swap.
* The winner of the game is the first person to reach 100 points


### Prerequisites

Python +3.3
A working terminal where you can run Python (Windows/non-terminal users can use GitBash: https://git-scm.com/downloads)

```
Give examples
```

### Running

To start the game, in your terminal type

```
python hog_gui.py
```
And a window should pop open with the game. The rules themselves are set up automatically in the program but here's a list of them:

In Hog, two players alternate turns trying to be the first to end a turn with at least 100 total points. On each turn, the current player chooses some number of dice to roll, up to 10. That player's score for the turn is the sum of the dice outcomes.

To spice up the game, we will play with some special rules:

Pig Out. If any of the dice outcomes is a 1, the current player's score for the turn is 1.
Example 1: The current player rolls 7 dice, 5 of which are 1's. They score 1 point for the turn.
Example 2: The current player rolls 4 dice, all of which are 3's. Since Pig Out did not occur, they score 12 points for the turn.

Free Bacon. A player who chooses to roll zero dice scores points equal to ten minus the minimum of the ones and tens digit of the opponent's score.
Example 1: The opponent has 13 points, and the current player chooses to roll zero dice. The minimum of 1 and 3 is 1, so the current player gains 10 - 1 = 9 points.
Example 2: The opponent has 85 points, and the current player chooses to roll zero dice. The minimum of 8 and 5 is 5, so the current player gains 10 - 5 = 5 points.
Example 3: The opponent has 7 points, and the current player chooses to roll zero dice. The minimum of 0 and 7 is 0, so the current player gains 10 - 0 = 10 points.

Feral Hogs. If the number of dice you roll is exactly 2 away (absolute difference) from the number of dice you rolled on the previous turn, you get 3 extra points for the turn. Treat the turn before the first turn as rolling 0 dice.

Example 1:
Both players start out at 0. (0, 0)
Player 0 rolls 3 dice and gets 1 point. (1, 0)
Player 1 rolls 4 dice and gets 1 point. (1, 1)
Player 0 rolls 5 dice and gets 1 point. 5 is 2 away from 3, so they get a bonus of 3. (5, 1)
Player 1 rolls 2 dice and gets 1 point. 2 is 2 away from 4, so they get a bonus of 3. (5, 5)
Player 0 rolls 7 dice and gets 1 point. 7 is 2 away from 5, so they get a bonus of 3. (9, 5)
Player 1 rolls 0 dice and gets 10 points. 0 is 2 away from 2, so they get a bonus of 3. (9, 18)

Example 2:
Both players start out at 0. (0, 0)
Player 0 rolls 2 dice and gets 3 points. 2 is 2 away from 0, so they get a bonus of 3. (6, 0)

Swine Swap. After points for the turn are added to the current player's score, if the first (leftmost) digit of the current player's score multiplied by the last (rightmost) digit of the current player's score are equal to the first (leftmost) digit of the other player's score multiplied by the last (rightmost) digit of the other player's score, then the two scores are swapped. (You can assume that all player scores have 3 digits or fewer.)
Example 1: At the end of a turn, the players have scores of 2 and 4. Since 2 * 2 != 4 * 4, the scores are not swapped.
Example 2: At the end of a turn, the players have scores of 22 and 4. Since 2 * 2 != 4 * 4, the scores are not swapped.
Example 3: At the end of a turn, the players have scores of 28 and 4. Since 2 * 8 == 4 * 4, the scores are swapped.
Example 4: At the end of a turn, the players have scores of 124 and 2. Since 1 * 4 == 2 * 2, the scores are swapped.
Example 5: At the end of a turn, the players have scores of 44 and 28. Since 4 * 4 == 2 * 8, the scores are swapped.
Example 6: At the end of a turn, the players have scores of 2 and 0. Since 2 * 2 != 0 * 0, the scores are not swapped.
Example 7: At the end of a turn, the players have scores of 10 and 0. Since 1 * 0 == 0 * 0, the scores are swapped.



And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Authors

The actual game and a good chuck of the code (including the GUI) comes from one of the projects in UC Berkeley's CS61A course. Slight modeifications were done, however most of the game has come from the course. Kudos to the CS61A staff for making it.

Kevin Marroquin


## Acknowledgments

* CS61A Staff
* Paul Hilfinger

