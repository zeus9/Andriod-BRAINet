package cse535.brainet;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static cse535.brainet.RequestType.API;
import static cse535.brainet.RequestType.LOCAL;

public class MainActivity extends AppCompatActivity {
    private ClassifierService service;

    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    Intent batteryStatus;

    private BrainNetBayes brainNetBayes;

    private Spinner spinner;

    private Random rng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rng = new Random();

        spinner = findViewById(R.id.spinner);
        List<String> list = Arrays.asList("S007", "S009", "S031", "S036", "S038", "S053", "S062", "S065", "S091", "S096");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

//        String url = "http://10.152.89.73:8080"; // Ryan local machine
//        String url = "http://70.176.85.69:8080"; // Cloud server
        String url = "http://192.168.1.11:8080"; // Jk's local server (mobile hotspot)
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        service = retrofit.create(ClassifierService.class);
        batteryStatus = this.registerReceiver(null, ifilter);

        // Copy testing data to external directory for use with weka
        brainNetBayes = new BrainNetBayes(this);
//        batteryConsumptionTest();
        findViewById(R.id.authenticateButton).setOnClickListener(v -> sendRequest(API));
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
        out.close();
    }

    public float getBatteryPercent(){
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
        System.out.println("battery: " + batteryPct);
        return batteryPct;
    }

    public void sendRequest(RequestType requestType){

        int indexOfEntry = rng.nextInt(140) + 1; // [1,140]
        Log.d("MainActivity", "Random number: " + indexOfEntry);

        if(requestType == LOCAL) {

            try {
                AssetManager assetManager = getAssets();
                String testingFile = spinner.getSelectedItem() + "_test.csv";
                InputStream infile = assetManager.open(testingFile);
                File outFile = new File(getExternalFilesDir(null), "test.csv");
                FileOutputStream outfile;
                outfile = new FileOutputStream(outFile);
                copyFile(infile, outfile);
                outFile = new File(getExternalFilesDir(null), "test.csv");

                String result = brainNetBayes.tryEntry(outFile.getPath(), indexOfEntry);
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                Log.d("MainActivity", "Guess: " + result);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error while evaluating", Toast.LENGTH_LONG).show();
            }
        }else {
            queryAgainstApi((String) spinner.getSelectedItem(), indexOfEntry);
        }
    }

    private void queryAgainstApi(String user, final int indexOfEntry) {
        new PredictUserTask().execute(user, Integer.toString(indexOfEntry), null);
    }

    private void queryOnDevice(int indexOfEntry) {
        Uri uri = Uri.parse("android.resource://cse535.brainnet/raw/combined_testing_data.csv");
//        BrainNetBayes brainNetBayes = new BrainNetBayes("C:\\Users\\ryane\\AndroidStudioProjects\\CSE535-BraiNetProject2\\BraiNet_Android\\data\\Combined_Training_data.csv");

        startPowerConsumption("local_run");
        String result = brainNetBayes.tryEntry("", 0);
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        stopPowerConsumption();
    }

    private void batteryConsumptionTest() {
//        BrainNetBayes brainNetBayes = new BrainNetBayes("C:\\Users\\ryane\\AndroidStudioProjects\\CSE535-BraiNetProject2\\BraiNet_Android\\data\\Combined_Training_data.csv");
        BrainNetBayes brainNetBayes = new BrainNetBayes(this);


        List<String> userList = Arrays.asList("S007", "S009", "S031", "S036", "S038", "S053", "S062", "S065", "S091", "S096");
        float startBattery = getBatteryPercent();
        int numEntries = 139;
        for(int j = 0; j < 1000; j++) {
            for (String user : userList) {
                for (int i = 1; i < numEntries; i++) {
//                    brainNetBayes.tryEntry(user, i);
                    service.classify(j + "\ndone\n");
                }
            }
        }
        float stopBattery = getBatteryPercent();

        float totalBatteryUsage = (startBattery - stopBattery);
        Toast.makeText(this, "Battery usage: started at " + startBattery + " ended at " + stopBattery, Toast.LENGTH_SHORT).show();
    }

    private void startPowerConsumption(String fileName) {
        Debug.startMethodTracing(fileName);
    }

    private void stopPowerConsumption() {
        Debug.stopMethodTracing();
    }

    private class PredictUserTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... params) {
            String response = "";
            try{
                Call<String> call = service.classify(params[0] + "\n" + Integer.parseInt(params[1]) + "\ndone\n");
                response = call.execute().body();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onProgressUpdate(Integer... progress) { }

        protected void onPostExecute(String result) {
            Toast.makeText(MainActivity.this, "Predicted user: " + result, Toast.LENGTH_LONG).show();
        }
    }
}
