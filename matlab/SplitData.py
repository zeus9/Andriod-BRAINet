# Data splitting (into train and test) program

import os
import csv

# Can hard code data paths here
# Raw string declaration needed by an 'r' before the path string for script to work on a Windows machine
# Can edit by removing 'r' declaration and adding appropriate qualifiers for Linux and Mac OS machines
# Hardcoded Data Locations for now
base_dir = r'D:\MC\BrainnetProject\first_part_of_data'
train_data_dir = r'D:\MC\BrainnetProject\Training_data'
test_data_dir = r'D:\MC\BrainnetProject\Testing_data'



# Split Data to Train and Test, 'noofrowsindatasample' is the no of rows in a csv file(which is the same in all csv file for our data)
class SplitData:
  def __init__(self, basedatapath = base_dir, traindatapath = train_data_dir, testdatapath = test_data_dir):

    # self.total_rows_in_datafile = noofrowsindatasample

    self.base_dir, self.train_data_dir, self.test_data_dir = basedatapath, traindatapath, testdatapath

    self.train_data_percent = 0
    self.test_data_percent = 0


  # computes now of rows in train and test data files
  def computeRowCounts(self, total_rows_in_datafile):
    train_data_row_count = round((self.train_data_percent * total_rows_in_datafile) / 100)

    # 1 added to test_data_row_count to account in the 65th row (to offset Bankers rounding in Python)
    test_data_row_count = train_data_row_count + round((self.train_data_percent * total_rows_in_datafile) / 100)

    # To account for odd number of rows in excel file data sample as round function in python3 is weird
    if((total_rows_in_datafile % 2) is not 0):
      test_data_row_count = test_data_row_count + 1

    return train_data_row_count, test_data_row_count


  # Split to train and test data
  def split(self, traindatapercent, testdatapercent):
    # Compute train and test data files row counts from percentages
    self.train_data_percent, self.test_data_percent = traindatapercent, testdatapercent


    # Start reading base data
    # for foldername in os.listdir(base_dir):

    # 'dirnames' is not needed in case you are windering!
    for rootpath, dirnames, filenames in os.walk(self.base_dir):
      for filename in filenames:
          if (filename.endswith(".csv")):
            # Record full paths to file
            filepath = os.path.join(rootpath, filename)

              # Read CSV file
            with open(filepath) as csv_read_file:
                csv_reader = csv.reader(csv_read_file, delimiter=",")

                # get row count of CSV
                row_count = sum(1 for row in csv_reader)
                train_data_row_count, test_data_row_count = self.computeRowCounts(row_count)

                # Go back to start of file after counting all the rows
                csv_read_file.seek(0)

                # To create directories for data sample if not existing already
                trainfile_path = self.train_data_dir + filepath[len(self.base_dir):]
                testfile_path = self.test_data_dir + filepath[len(self.base_dir):]
                os.makedirs(trainfile_path[:-len(filename)], exist_ok=True)
                os.makedirs(testfile_path[:-len(filename)], exist_ok=True)

                # Write to Train Data file and Test data file
                with open(trainfile_path, 'a', newline="") as train_write_file, open(testfile_path, 'a', newline="") as test_write_file:
                  train_data_writer = csv.writer(train_write_file, dialect='excel', lineterminator='\n')
                  test_data_writer = csv.writer(test_write_file, dialect='excel', lineterminator='\n')

                  line_count = 0

                  for row in csv_reader:
                    line_count = line_count + 1
                    if(line_count <= train_data_row_count):
                      train_data_writer.writerow(row)

                    elif (line_count > train_data_row_count and line_count <= test_data_row_count):
                      test_data_writer.writerow(row)


data = SplitData()
# Input percentages of train and test data in that order
print("\nSplitting To Train and Test Data, please wait...")
data.split(10, 10)
print("Done.\n")



