//in progress creating template for the project (movie dataset reading, masking, KNN for K=1)
import java.io.*;
import java.util.SplittableRandom;
public class Test {
    // Use we use 'static' for all methods to keep things simple, so we can call those methods main
    static void Assert (boolean res) // We use this to test our results - don't delete or modify!
    {
        if(!res)	{
            System.out.print("Something went wrong.");
            System.exit(0);
        }
    }

    // Copy your vector operations here:
    static double dot(double [] u, double [] v) {
        Assert(u.length == v.length);
        double result = 0.0;
        for(int i = 0; i < u.length; i++) {
            result += u[i] * v[i];
        }

        return result;
    }

    static int NumberOfFeatures = 15;
    static double[] toFeatureVector(double id, String genre, double runtime, double year, double imdb, double rt, double budget, double boxOffice) {


        double[] feature = new double[NumberOfFeatures];

        switch (genre) { // We also use represent each movie genre as an integer number:
            case "Action":    feature[0] = 1; break;
            case "Fantasy":   feature[1] = 1; break;
            case "Romance":   feature[2] = 1; break;
            case "Sci-Fi":    feature[3] = 1; break;
            case "Adventure": feature[4] = 1; break;
            case "Horror":    feature[5] = 1; break;
            case "Comedy":    feature[6] = 1; break;
            case "Thriller":  feature[7] = 1; break;
            default: Assert(false);
        }

        switch ((int)year) {
            case 2021:  feature[8] = 1; break;
            case 2022:  feature[9] = 1; break;
            case 2023:  feature[10] = 1; break;
            default: Assert(false);
        }

        if (imdb < 5.0) {
            feature[11] = 1;
        }
        else if (imdb > 7.5) {
            feature[12] = 1;
        }

        if (runtime <= 100) {
            feature[13] = 1;
        }
        else {
            feature[14] = 1;
        }


        return feature;
    }

    // We are using the dot product to determine similarity:
    static double similarity(double[] u, double[] v) {
        return dot(u, v);
    }

    // We have implemented KNN classifier for the K=1 case only. You are welcome to modify it to support any K
    static int knnClassify(double[][] trainingData, int[] trainingLabels, double[] testFeature) {
        int bestMatch = -1;
        double bestSimilarity = - Double.MAX_VALUE;  // We start with the worst similarity that we can get in Java.

        for (int i = 0; i < trainingData.length; i++) {
            double currentSimilarity = similarity(testFeature, trainingData[i]);
            if (currentSimilarity > bestSimilarity) {
                bestSimilarity = currentSimilarity;
                bestMatch = i;
            }
        }
        return trainingLabels[bestMatch];
    }


    static void loadData(String filePath, double[][] dataFeatures, int[] dataLabels) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int idx = 0;
            br.readLine(); // skip header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Assuming csv format: MovieID,Title,Genre,Runtime,Year,Lead Actor,Director,IMDB,RT(%),Budget,Box Office Revenue (in million $),Like it
                double id = Double.parseDouble(values[0]);
                String genre = values[2];
                double runtime = Double.parseDouble(values[10]);
                double year = Double.parseDouble(values[3]);
                double imdb = Double.parseDouble(values[7]);
                double rt = Double.parseDouble(values[6]);
                double budget = Double.parseDouble(values[9]);
                double boxOffice = Double.parseDouble(values[8]);

                dataFeatures[idx] = toFeatureVector(id, genre, runtime, year, imdb, rt, budget, boxOffice);
                dataLabels[idx] = Integer.parseInt(values[11]); // Assuming the label is the last column and is numeric
                idx++;
            }
        }
    }

    public static void main(String[] args) {

        double[][] trainingData = new double[100][];
        int[] trainingLabels = new int[100];
        double[][] testingData = new double[100][];
        int[] testingLabels = new int[100];
        try {
            // You may need to change the path:
            loadData("bin\\training-set.csv", trainingData, trainingLabels);            
            loadData("bin\\testing-set.csv", testingData, testingLabels); 
        }
        catch (IOException e) {
            System.out.println("Error reading data files: " + e.getMessage());
            return;
        }
        // Compute accuracy on the testing set
        int correctPredictions = 0;

        // Add some lines here: ...
        // We compare each case where the training data matches the testing data and find for each correct prediction
        for (int i = 0; i < testingData.length; i++) {
            int predictedLabel = knnClassify(trainingData, trainingLabels, testingData[i]);
            if (predictedLabel == testingLabels[i]) {
                correctPredictions++;
            }
        }
        double accuracy = (double) correctPredictions / testingData.length * 100;
        System.out.printf("A: %.2f%%\n", accuracy);
    }
}