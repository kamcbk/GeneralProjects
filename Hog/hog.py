"""The Game of Hog, taken from CS61A at UC Berkeley"""

from dice import six_sided, four_sided, make_test_dice
from ucb import main, trace, interact

GOAL_SCORE = 100  # The goal of Hog is to score 100 points.

######################
# Phase 1: Simulator #
######################


def roll_dice(num_rolls, dice=six_sided):
    """Simulate rolling the DICE exactly NUM_ROLLS > 0 times. Return the sum of
    the outcomes unless any of the outcomes is 1. In that case, return 1.

    Inputs:
        num_rolls:  The number of dice rolls that will be made.
        dice:       A function that simulates a single dice roll outcome.

    Return:
        An integer of points earned for your turn.
    """
    # These assert statements ensure that num_rolls is a positive integer.
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls > 0, 'Must roll at least once.'

    #Simulate rolls
    rolls = [dice() for num in range(num_rolls)]

    #Determine whether there exists 1 in the dice you rolled
    if 1 in rolls:
        return 1
    else:
        return sum(rolls)

def free_bacon(opponent_score):
    """A player who chooses to roll zero dice scores points equal to ten 
    minus the minimum of the ones and tens digit of the opponent's score.
    
    Inputs:
        opponent_score: The total integer score of the opponent.

    Return:
        The integer points scored from rolling 0 dice.

    """
    #Making sure score isn't over 100 points
    assert opponent_score < 100, 'The game should be over.'

    #Return 10 minus the min of the first or second digit in your opponent's score
    return 10 - min(opponent_score // 10, opponent_score % 10)


def take_turn(num_rolls, opponent_score, dice=six_sided):
    """Simulate a turn rolling NUM_ROLLS dice, which may be 0 (Free Bacon).
    Return the points scored for the turn by the current player.

    num_rolls:       The number of dice rolls that will be made.
    opponent_score:  The total integer score of the opponent.
    dice:            A function that simulates a single dice roll outcome.
    """
    #Asserting to make sure human inputs are legal
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls >= 0, 'Cannot roll a negative number of dice in take_turn.'
    assert num_rolls <= 10, 'Cannot roll more than 10 dice.'
    assert opponent_score < 100, 'The game should be over.'

    #Free Bacon
    if num_rolls == 0:
        return free_bacon(opponent_score)
    #Regular turn
    else:
        return roll_dice(num_rolls, dice=dice)



def is_swap(player_score, opponent_score):
    """
    Swine_Swap!
    After points for the turn are added to the current player's score, if 
    the first (leftmost) digit of the current player's score multiplied by 
    the last (rightmost) digit of the current player's score are equal to 
    the first (leftmost) digit of the other player's score multiplied by 
    the last (rightmost) digit of the other player's score, then the two 
    scores are swapped. We will assume that all player scores have 3 digits 
    or fewer.

    Inputs:
        player score: The total integer score of the player.
        opponent_score: The total integer score of the opponent.

    Return:
        A boolean on whether the two scores should be swapped
    """
    #Create function to determine which numbers to multiply
    def swine(score):
        if score >= 100:
            return (score // 100, score % 10)
        elif score >= 10:
            return (score // 10, score % 10)
        else:
            return (score, score)

    #Finding digits for player and opponent score
    playerLeft, playerRight = swine(player_score)
    opponentLeft, opponentRight = swine(opponent_score)

    #Determine if they're equal
    return playerLeft * playerRight == opponentLeft * opponentRight


def other(player):
    """Return the other player, for a player PLAYER numbered 0 or 1.

    >>> other(0)
    1
    >>> other(1)
    0
    """
    return 1 - player


def silence(score0, score1):
    """Announce nothing (see Phase 2)."""
    return silence


def play(strategy0, strategy1, score0=0, score1=0, dice=six_sided,
         goal=GOAL_SCORE, say=silence, feral_hogs=True):
    """Simulate a game and return the final scores of both players, with Player
    0's score first, and Player 1's score second.

    A strategy is a function that takes two total scores as arguments (the
    current player's score, and the opponent's score), and returns a number of
    dice that the current player will roll this turn.

    Inputs:
        strategy0:  The strategy function for Player 0, who plays first.
        strategy1:  The strategy function for Player 1, who plays second.
        score0:     Starting score for Player 0
        score1:     Starting score for Player 1
        dice:       A function of zero arguments that simulates a dice roll.
        goal:       The game ends and someone wins when this score is reached.
        say:        The commentary function to call at the end of the first turn.
        feral_hogs: A boolean indicating whether the feral hogs rule should be active.

    Return:
        A simulated game of Hog
    """
    player = 0  # Which player is about to take a turn, 0 (first) or 1 (second)

    #Setting tracker for Fedral Hog
    old_roll0 = 0
    old_roll1 = 0

    #Setting comentary
    comment = both(announce_lead_changes(), say_scores)

    #Simulating game
    while (goal > score0) and (goal > score1):
        #Player strategies and turns

        #Player0
        if player == 0:
            num_rolls0 = strategy0(score0, score1)
            score0 += take_turn(num_rolls0, score1, dice = dice)
            if (abs(num_rolls0 - old_roll0) == 2) and feral_hogs: #Feral Hogs
                score0 += 3
            old_roll0 = num_rolls0

        #Player1
        else:
            num_rolls1 = strategy1(score0, score1)
            score1 += take_turn(num_rolls1, score0, dice = dice)
            if (abs(num_rolls1 - old_roll1) == 2) and feral_hogs: #Feral Hogs
                score1 += 3
            old_roll1 = num_rolls1

        #Swine Swap
        if is_swap(score0, score1):
            score0, score1 = score1, score0

        #Other player's turn
        player = other(player)

        #Added Commentary
        comment = comment(score0, score1)
        
    return score0, score1


#######################
# Phase 2: Commentary #
#######################


def say_scores(score0, score1):
    """A commentary function that announces the score for each player."""
    print("Player 0 now has", score0, "and Player 1 now has", score1)
    return say_scores

def announce_lead_changes(previous_leader=None):
    """Return a commentary function that announces lead changes.

    """
    def say(score0, score1):
        if score0 > score1:
            leader = 0
        elif score1 > score0:
            leader = 1
        else:
            leader = None
        if leader != None and leader != previous_leader:
            print('Player', leader, 'takes the lead by', abs(score0 - score1))
        return announce_lead_changes(leader)
    return say

def both(f, g):
    """Return a commentary function that says what f says, then what g says.

    """
    def say(score0, score1):
        return both(f(score0, score1), g(score0, score1))
    return say

def announce_highest(num = 1):
    """Return a blank function to run GUI

    """
    return


#######################
# Phase 3: Strategies #
#######################


def always_roll(n):
    """Return a strategy that always rolls N dice.

    A strategy is a function that takes two total scores as arguments (the
    current player's score, and the opponent's score), and returns a number of
    dice that the current player will roll this turn.

    Inputs:
        n: An integer of the number of dice

    Return:
        A strategy function that always rolls N dice.
    """
    def strategy(score, opponent_score):
        return n
    return strategy


def make_averaged(fn, num_samples=1000):
    """Return a function that returns the average value of FN when called.

    """
    #Defining function
    def average(*args):
        return sum([fn(*args) for i in range(1000)]) / num_samples

    return average



def max_scoring_num_rolls(dice=six_sided, num_samples=1000):
    """Return the number of dice (1 to 10) that gives the highest average turn
    score by calling roll_dice with the provided DICE over NUM_SAMPLES times.
    Assume that the dice always return positive outcomes.

    >>> dice = make_test_dice(1, 6)
    >>> max_scoring_num_rolls(dice)
    1
    """
    avg_score = 0 #Average number in num_rolls
    max_score = 1 #highest avg num_rolls (so far...)

    for num_dice in range(1, 11):
        average = make_averaged(roll_dice, num_samples = num_samples)(num_dice, dice)
        if average > avg_score:
            max_score = average
            max_score = num_dice
    return max_score


def winner(strategy0, strategy1):
    """Return 0 if strategy0 wins against strategy1, and 1 otherwise."""
    score0, score1 = play(strategy0, strategy1)
    if score0 > score1:
        return 0
    else:
        return 1


def average_win_rate(strategy, baseline=always_roll(4)):
    """Return the average win rate of STRATEGY against BASELINE. Averages the
    winrate when starting the game as player 0 and as player 1.
    """
    win_rate_as_player_0 = 1 - make_averaged(winner)(strategy, baseline)
    win_rate_as_player_1 = make_averaged(winner)(baseline, strategy)

    return (win_rate_as_player_0 + win_rate_as_player_1) / 2


def run_experiments():
    """Run a series of strategy experiments and report results."""
    if True:  # Change to False when done finding max_scoring_num_rolls
        six_sided_max = max_scoring_num_rolls(six_sided)
        print('Max scoring num rolls for six-sided dice:', six_sided_max)

    if False:  # Change to True to test always_roll(8)
        print('always_roll(6) win rate:', average_win_rate(always_roll(6)))

    if False:  # Change to True to test bacon_strategy
        print('bacon_strategy win rate:', average_win_rate(bacon_strategy))

    if False:  # Change to True to test swap_strategy
        print('swap_strategy win rate:', average_win_rate(swap_strategy))

    if False:  # Change to True to test final_strategy
        print('final_strategy win rate:', average_win_rate(final_strategy))



def bacon_strategy(score, opponent_score, margin=8, num_rolls=5):
    """This strategy rolls 0 dice if that gives at least MARGIN points, and
    rolls NUM_ROLLS otherwise.
    """
    if free_bacon(opponent_score) >= margin:
        return 0
    else:
        return num_rolls


def swap_strategy(score, opponent_score, margin=8, num_rolls=4):
    """This strategy rolls 0 dice when it triggers a beneficial swap. It also
    rolls 0 dice if it gives at least MARGIN points and does not trigger a
    non-beneficial swap. Otherwise, it rolls NUM_ROLLS.
    """
    if is_swap(score, opponent_score):
        if score < (opponent_score - margin):
            return 0
    else:
        return num_rolls



def final_strategy(score, opponent_score):
    """My strategy: First check if you are winning and if implementing a zero 
    creates a swap. This is done because it hurts to roll a 0 so we roll 4 
    dice instead, 4 because we don't want to roll too much (chanes of Pig 
    Out increase). Otherwise, we use bacon_strategy with swap_strategy as its 
    num_rolls in order to check for a beneficial swap. The number for margin 
    gives us the highest score when tested.
    """
    if is_swap(score + take_turn(0, opponent_score), opponent_score and 
        score > opponent_score): #No hurtful swap
        return 4
    else:
        return bacon_strategy(score, opponent_score, 5, 
                                swap_strategy(score, opponent_score, 4))


##########################
# Command Line Interface #
##########################

# NOTE: Functions in this section do not need to be changed. They use features
# of Python not yet covered in the course.


@main
def run(*args):
    """Read in the command-line argument and calls corresponding functions.

    This function uses Python syntax/techniques not yet covered in this course.
    """
    import argparse
    parser = argparse.ArgumentParser(description="Play Hog")
    parser.add_argument('--run_experiments', '-r', action='store_true',
                        help='Runs strategy experiments')

    args = parser.parse_args()

    if args.run_experiments:
        run_experiments()