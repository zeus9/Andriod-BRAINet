# Data Combining Program

import sys
import os
import csv

data_to_combine_path = r"D:\MC\BrainnetProject\Testing_data"
combined_data_path = r"D:\MC\BrainnetProject\Combined_Training_data"
# You can either  provide final combined data file name as a command line argument or hard code  it
combined_data_filename = "combinedData.csv"

class CombineData:
    def __init__(self, datatocombinepath = data_to_combine_path, combineddatalocation = combined_data_path, combineddatafilename = combined_data_filename):
        self.data_to_combine_path = datatocombinepath
        self.combined_data_path = combineddatalocation
        self.combined_data_filename = self.getCombinedDataFilename()


    def getCombinedDataFilename(self, combineddatafilename = combined_data_filename) -> str:
        combined_data_filename = ""

        # Check if file is called from command line (if its the main function and not imported as a module)
        if __name__ == "__main__" and len(sys.argv) > 1:
        # Get Combined data file name as a command line argument
            combined_data_filename = sys.argv[1]
        elif not combined_data_filename:
            combined_data_filename = combineddatafilename

        if not combined_data_filename.endswith(".csv"):
            combined_data_filename = combined_data_filename + ".csv"

        return combined_data_filename


    def getCombinedfilePath(self):
        return os.path.join(self.combined_data_path, self.combined_data_filename)


    def combine(self):

        combined_file_path = self.getCombinedfilePath()

        os.makedirs(self.data_to_combine_path, exist_ok=True)
        os.makedirs(self.combined_data_path, exist_ok=True)

        # 'dirnames' not needed (in case you are wondering!)
        for rootpath, dirnames, filenames in os.walk(self.data_to_combine_path):
            for filename in filenames:
                    if(filename.endswith(".csv")):
                        # Record full paths to file
                        filepath = os.path.join(rootpath, filename)

                        # Read CSV Data to combine
                        with open(filepath) as csvfreadile:
                            csvreader = csv.reader(csvfreadile, delimiter=",")

                            # Write CSV Data to file
                            with open(combined_file_path, 'a', newline="") as combinedcsvfile:
                                csvwriter = csv.writer(combinedcsvfile, dialect="excel", lineterminator = '\n')

                                # Write Header to first row for transposed data with 65 columns
                                csvwriter.writerow(list(range(0,66)))

                                for row in csvreader:
                                    csvwriter.writerow(row)



# main program

data = CombineData()
print("\nCombining data and writing to file...")
data.combine()
print("Done.")
print("Combined Data can be found at: \"" + data.getCombinedfilePath() + "\"")
print("")