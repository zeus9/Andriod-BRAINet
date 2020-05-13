package cse535.brainet.other_ml_algos;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.widget.Toast;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomForestAlgorithm {
    private Classifier classifier;
    //    private Instances testingDataset;
    private Context context;
    CSVLoader loader;
    Instances dataSet;

    public RandomForestAlgorithm(Context context) {
        this.context = context;
        try {
            Classifier cls = null;
            AssetManager assetManager = context.getAssets();
            try {
                ObjectInputStream ois = new ObjectInputStream(
                        assetManager.open(
                                "bayes.model"));
                cls = (Classifier) ois.readObject();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cls != null) {
                this.classifier = cls;
//                this.testingDataset = getDataSet(testingData);
            } else {
                Toast.makeText(context, "Classifier was null", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RandomForestAlgorithm(String path) {
        try {
            Classifier cls = null;

            try {
                ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(path));
                cls = (Classifier) ois.readObject();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cls != null) {
                this.classifier = cls;
//                this.testingDataset = getDataSet(testingData);
            } else {
                Toast.makeText(context, "Classifier was null", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Instances getDataSet(String testingData) throws Exception {
        StringToWordVector filter = new StringToWordVector();
        int classIdx = 0;
        CSVLoader loader = new CSVLoader();

        File testingDataFile = new File(testingData);
        loader.setSource(testingDataFile);
        Instances dataSet = loader.getDataSet();
        dataSet.setClassIndex(classIdx);
        filter.setInputFormat(dataSet);
        dataSet = Filter.useFilter(dataSet, filter);
        return dataSet;
    }



    public static void mains(String[] args) {
        System.out.println("Starting...");
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:\\Users\\ryane\\AndroidStudioProjects\\CSE535-BraiNetProject2\\BraiNet_Android\\app\\src\\main\\assets\\random_forest.model"));

            System.out.println("Loading data");
            Instances trainingDataset = getDataSet("C:\\\\Users\\\\ryane\\\\GoglandProjects\\\\playground\\\\eeg_proj\\\\model_data\\train.csv");
            System.out.println("Data loaded");

            RandomForest classifier = new RandomForest();
            System.out.println("Training");
            classifier.buildClassifier(trainingDataset);

            System.out.println("Writing");
            oos.writeObject(classifier);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String tryEntry(String dataSetName, int indexOfEntry) {
        String retVal;

        List<String> list = Arrays.asList("S007", "S009", "S031", "S036", "S038", "S053", "S062", "S065", "S091", "S096");

        try {
            Instances testingDataset = getDataSet(dataSetName);

            double index = classifier.classifyInstance(testingDataset.instance(indexOfEntry));
            retVal = "You appear to be subject [" + list.get((int) index) + "] in this test";
//            retVal = testingDataset.attribute(0).value((int) index);
            // if the testing data set doesn't contain the predicted value, it will cause an out of index error
        } catch (Exception e) {
            retVal = e.toString();
        }

        return retVal;
    }

    public String tryEntryInt(String dataSetName, int indexOfEntry) {
        String retVal = "";

        List<String> list = Arrays.asList("S007", "S009", "S031", "S036", "S038", "S053", "S062", "S065", "S091", "S096");

        try {
            Instances testingDataset = getDataSet(dataSetName);

            double index = classifier.classifyInstance(testingDataset.instance(indexOfEntry));
            retVal =  list.get((int) index);
//            retVal = testingDataset.attribute(0).value((int) index);
            // if the testing data set doesn't contain the predicted value, it will cause an out of index error
        } catch (Exception e) {
            retVal = e.toString();
        }

        return retVal;
    }

    public static void main(String[] args) {
        /*
        *   Correct: 315.0
            Wrong: 1085.0
            Accuracy: 0.225
        * */
        RandomForestAlgorithm brainNetBayes = new RandomForestAlgorithm("C:\\Users\\ryane\\AndroidStudioProjects\\CSE535-BraiNetProject2\\BraiNet_Android\\app\\src\\main\\assets\\random_forest.model");

        List<String> list = new ArrayList<>();
        list.add("S007");
        list.add("S009");
        list.add("S031");
        list.add("S036");
        list.add("S038");
        list.add("S053");
        list.add("S062");
        list.add("S065");
        list.add("S091");
        list.add("S096");

        double correct = 0;
        double wrong = 0;

        for (int i = 0; i < list.size(); i++) {
            String test = "C:\\Users\\ryane\\AndroidStudioProjects\\CSE535-BraiNetProject2\\BraiNet_Android\\app\\src\\main\\assets\\" + list.get(i) + "_test.csv";

            try {
                for (int in = 1; in <= 140; in++) {
                    String eval = brainNetBayes.tryEntryInt(test, in);
                    if (list.get(i).equals(eval)) {
                        System.out.println(list.get(i) + " match");
                        correct++;
                    } else {
                        System.out.println(list.get(i) + " mismatch [" + eval + "]");
                        wrong++;
                    }

                }

//                System.out.println(list.get(i));
//                brainNetBayes.printAccuracy(test, trainingDataSet);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        System.out.println("Correct: " + correct);
        System.out.println("Wrong: " + wrong);
        System.out.println("Accuracy: " + correct / (correct + wrong));
    }

    public void printAccuracy(String testingDataPath, Instances trainingDataSet) throws Exception {
        Instances testingDataSet = getDataSet(testingDataPath);
        Evaluation eval = new Evaluation(trainingDataSet);
        eval.evaluateModel(classifier, testingDataSet);
        System.out.println("** Naive Bayes Evaluation with Datasets **");
        System.out.println(eval.toSummaryString());
    }

    private class LoadDataSetTask extends AsyncTask<String, Integer, Instances> {
        protected Instances doInBackground(String... fileNames) {

            Instances loadedData = null;
            try {
                loader.setSource(new File(context.getExternalFilesDir(null), fileNames[0]));
                loadedData = loader.getDataSet();
                loadedData.setClassIndex(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return loadedData;
        }



        protected void onProgressUpdate(Integer... progress) { }

        protected void onPostExecute(Instances result) {
            StringToWordVector filter = new StringToWordVector();
            try {
                filter.setInputFormat(dataSet);
                RandomForestAlgorithm.this.dataSet = Filter.useFilter(dataSet, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}