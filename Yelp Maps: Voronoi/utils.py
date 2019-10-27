"""Utility functions for future use."""

from math import sqrt
from random import sample


def map_and_filter(s, map_fn, filter_fn):
    """
    Return a new list containing the result of calling map_fn on each
    element of sequence s for which filter_fn returns a true value.

    """
    return [map_fn(s) for s in s if filter_fn(s)]

def key_of_min_value(d):
    """
    Returns the key in dict d that corresponds to the minimum value of d.

    """
    return min(d, key = lambda x: d[x])

def zipper(*sequences):
    """
    Returns a list of lists, where the i-th list contains the i-th
    element from each of the argument sequences.

    Ex.
    >>> zip(range(0, 3), range(3, 6))
    [[0, 3], [1, 4], [2, 5]]
    >>> for a, b in zip([1, 2, 3], [4, 5, 6]):
    ...     print(a, b)
    1 4
    2 5
    3 6
    >>> for triple in zip(['a', 'b', 'c'], [1, 2, 3], ['do', 're', 'mi']):
    ...     print(triple)
    ['a', 1, 'do']
    ['b', 2, 're']
    ['c', 3, 'mi']

    """
    return list(map(list, zip(*sequences)))

def enumerate(s, start=0):
    """
    Returns a list of lists, where the i-th list contains i+start and the
    i-th element of s.

    Ex.
    >>> enumerate([6, 1, 'a'])
    [[0, 6], [1, 1], [2, 'a']]
    >>> enumerate('five', 5)
    [[5, 'f'], [6, 'i'], [7, 'v'], [8, 'e']]

    """
    return zipper(range(start, len(s) + start,), s)

def distance(pos1, pos2):
    """
    Return the Euclidean distance between pos1 and pos2, which are pairs.

    """
    return sqrt((pos1[0] - pos2[0]) ** 2 + (pos1[1] - pos2[1]) ** 2)

def mean(s):
    """
    Return the arithmetic mean of a sequence of numbers s.

    """
    assert len(s) > 0, 'cannot find mean of empty sequence'
    return sum(s) / len(s)
