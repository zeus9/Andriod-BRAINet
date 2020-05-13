import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.DecisionStump;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;


public class DecisionStumpAlgorithm {
    private static final String TRAINING_DATA_SET_FILENAME = "C:\\Users\\ryane\\workspace\\BrainNetServer\\data\\nominal_S001R01.csv";
    private static final String TESTING_DATA_SET_FILENAME = "C:\\Users\\ryane\\workspace\\BrainNetServer\\data\\nominal_S001R02.csv";
//    private static final String PREDICTION_DATA_SET_FILENAME = "C:\\Users\\ryane\\workspace\\BrainNetServer\\data\\nominal_S001R03.csv";

    private Classifier classifier;
    private Instances trainingDataSet;

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

    public DecisionStumpAlgorithm(String trainingFilePath) {
        try {
            this.classifier = new DecisionStump();
            this.trainingDataSet = getDataSet(trainingFilePath);
            classifier.buildClassifier(trainingDataSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(new DecisionStumpAlgorithm(TRAINING_DATA_SET_FILENAME).tryEntry("C:\\Users\\ryane\\workspace\\BrainNetServer\\data\\nominal_S001R01.csv", 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void process(String testingDataPath) throws Exception {
        Instances testingDataSet = getDataSet(testingDataPath);
        Evaluation eval = new Evaluation(this.trainingDataSet);
        eval.evaluateModel(classifier, testingDataSet);
        System.out.println("** Naive Bayes Evaluation with Datasets **");
        System.out.println(eval.toSummaryString());
    }

    public String tryEntry(String pathToData, int indexOfEntry) {
        String retVal;

        try {
            Instances instanceToTry = getDataSet(pathToData);
            double index = classifier.classifyInstance(instanceToTry.instance(indexOfEntry));
            retVal = trainingDataSet.attribute(0).value((int) index);
        } catch (Exception e) {
            retVal = e.toString();
        }

        return retVal;
    }
}