###A homemade k-Nearest Neighbors algorithm###

#Author: Kevin Marroquin
#fast_distance function taken from data8.org/su17. Modified for use here.

#Loading imports
import numpy as np
import pandas as pd

class knnModel():
    
    #Initializing model
    def __init__(self, train_data = None, 
                 test_data = None, predictions = None):
        self.train_data = train_data
        self.test_data = test_data
        self.predictions = None
        
    #Creating train test split
    def train_test_split(self, data, proportion, feature_space):
        """Create a train test split by inputting a dataframe. Split is given 
        by proportion and split column-wise by feature-space. Returns train 
        and test data."""
        #Creating sample
        rand_sam = np.arange(len(data))
        np.random.shuffle(rand_sam)

        #Breaking up my data 75-25% split
        split_pt = int(np.round(len(data) * proportion))
        data_train = data.iloc[rand_sam[:split_pt], :]
        data_test = data.iloc[rand_sam[split_pt:], :]

        #Gathering 20 features
        basics = np.array(["Title", "Artist", "Genre"])
        train_data = data_train.loc[:, 
                                    np.concatenate((basics, feature_space))]
        test_data = data_test.loc[:, 
                                    np.concatenate((basics, feature_space))]

        return (train_data, test_data)
        
    
    #Inputting training data
    def train(self, train_data):
        """Training data in current model."""
        assert type(train_data) is pd.core.frame.DataFrame, "Wrong input type"
        self.train_data = train_data
        self.test_data = None
        self.predictions = None
    
    #Classifying using testing data
    def predict(self, test_data, k = 7):
        """Creating a classifier to give a list of predictions of values 
        based on test data
        """
        assert type(test_data) is pd.core.frame.DataFrame, "Wrong input type"
        self.test_data = test_data
        temp_test = self.test_data.drop(["Title", "Artist", "Genre"], 
                                        axis = 1)
        temp_train = self.train_data.drop(["Title", "Artist", "Genre"], 
                                         axis = 1)
        
        #Making distance function
        distFunc = lambda row: fast_distances(row, temp_train)

        #Making new pandas function
        createDF = lambda data: pd.DataFrame({"label": self.train_data.Genre,
                                              "distance": data}
                                            ).sort_values("distance")
        
        #Applying functions
        self.predictions = temp_test.apply(distFunc, axis = 1
                                        ).apply(createDF
                                        ).apply(freqValue)
        
        return self.predictions
        
        
    #Scoring train and test data
    def score(self, target_var):
        """Determines how accurate our model predictes vs actual by calling
        a target variable in inputted """
        assert type(self.train_data) is pd.core.frame.DataFrame, "train is null"
        assert type(self.test_data) is pd.core.frame.DataFrame, "test is null"
        assert self.predictions is not None, "Train and predict first"
        
        return np.mean(self.predictions == self.test_data[target_var])

#Calculating distance faster a simple function
def fast_distances(test_row, train_rows):
    """An array of the distances between test_row and each row in train_rows.

    Takes 2 arguments:
      test_row: A row of a table containing features of one
        test song (e.g., test_20.iloc[0, :]).
      train_rows: A table of features (for example, the whole
        table train_20).
        
    Returns distance array of valures from test row and all of the train rows
    """
    counts_matrix = np.asmatrix(train_rows)
    diff = (
        np.tile(np.array(test_row), [counts_matrix.shape[0], 1]) - 
        counts_matrix
    )
    distances = np.squeeze(np.asarray(np.sqrt(np.square(diff).sum(1))))
    return distances


#Gathering the 5 nearest neighbors 
def freqValue(df, label = "label", num_neighbor = 5):
    """Returns the most k most frequent value in a column in a dataframe. 
    
    Arguments:
        df - DataFrame of values
        label (optional) - Name of column to find data
        num_neighbors (optional) - Number of k neighbors
    """
    from collections import Counter
    
    #Dictionary giving labels in label (keys) and their frequency (values)
    x = dict(Counter(
        df.iloc[:num_neighbor, :].loc[:,label]
    ))
    return max(x, key=lambda i: x[i])