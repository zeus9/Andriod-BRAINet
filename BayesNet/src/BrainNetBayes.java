import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;

public class BrainNetBayes {
    private Classifier classifier;
    private Instances testingDataset;

    @SuppressWarnings("Duplicates")
    private static Instances getDataSet(String fileName) throws Exception {
        StringToWordVector filter = new StringToWordVector();
        int classIdx = 0;
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(fileName));
        Instances dataSet = loader.getDataSet();
        dataSet.setClassIndex(classIdx);
        filter.setInputFormat(dataSet);
        dataSet = Filter.useFilter(dataSet, filter);
        return dataSet;
    }

    public BrainNetBayes(String testingFilePath) {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream("./bayes.model"));
            Classifier cls = (Classifier) ois.readObject();
            ois.close();

            this.classifier = cls;

            this.testingDataset =  getDataSet(testingFilePath);
//            printAccuracy(testingFilePath, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printAccuracy(String testingDataPath, String trainingDataPath) throws Exception {
        Instances testingDataSet = getDataSet(testingDataPath);
        Instances trainingDataSet = getDataSet(trainingDataPath);
        Evaluation eval = new Evaluation(trainingDataSet);
        eval.evaluateModel(classifier, testingDataSet);
        System.out.println("** Naive Bayes Evaluation with Datasets **");
        System.out.println(eval.toSummaryString());
    }

    public static void main(String[] args) {
        try {
//            System.out.println(new BrainNetBayes("/Users/jk/dev/CSE535-BraiNetProject/matlab/Combined_Testing_data.csv").tryEntry(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String tryEntry(int indexOfEntry) {
        String retVal;

        try {
            double index = classifier.classifyInstance(testingDataset.instance(indexOfEntry));
            retVal = testingDataset.attribute(0).value((int) index);
        } catch (Exception e) {
            retVal = e.toString();
        }

        return retVal;
    }
}