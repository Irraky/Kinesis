// Copyright 2019 Alpha Cephei Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.kaldi.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.kaldi.Assets;
import org.kaldi.KaldiRecognizer;
import org.kaldi.Model;
import org.kaldi.RecognitionListener;
import org.kaldi.SpeechService;
import org.kaldi.Vosk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KaldiActivity extends Activity implements
        RecognitionListener {

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int  STATE_DONE= 2;
    static private final int STATE_FILE = 3;
    static private final int STATE_MIC  = 4;

    // enum to know in which screen we are
    enum menuEnum {
        MAINMENU,
        ADD,
        WAKE
    };
    // variable of menu enum
    menuEnum menu = menuEnum.MAINMENU;

    String TAG = "Test";
    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;


    private Model model;
    private SpeechService speechService;
    TextView resultView;

    @Override
    public void onCreate(Bundle state) {
        // lines to bypass thread things with network (it's a bad method)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(state);
        setContentView(R.layout.main);

        // Setup layout
        resultView = findViewById(R.id.result_text);
        setUiState(STATE_START);


        findViewById(R.id.recognize_mic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recognizeMicrophone();
            }
        });

        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new SetupTask(this).execute();
        getMacAdresses();
    }
    // recup all key and value => name of computer + MAC addresses
    public String getMacAdresses() {
        SharedPreferences keyValues = getApplicationContext().getSharedPreferences("name_icons_list", Context.MODE_PRIVATE);
        Map <String, ?> macAddresses = keyValues.getAll();
        int i = 1;
        String listAddresses = new String();
        for (String s: macAddresses.keySet()) {
            listAddresses = listAddresses.concat(i + ": " + s + " MAC: " +  macAddresses.get(s) + "\n");
            i++;
        }
        Log.d(TAG, "" + listAddresses);
        /*TextView textAddresses = findViewById(R.id.macAddresses);
        textAddresses.setText(listAddresses);*/
        return (listAddresses);
    }
    public void test(){
        // my list of names, icon locations
        Map<String, String> nameIcons = new HashMap<String, String>();
        nameIcons.put("ordi", "F0-1D-BC-A0-BC-86");
        nameIcons.put("Bob", "another/location/to/bob/icon.png");
        nameIcons.put("another name", "last/location/icon.png");

        SharedPreferences keyValues = getApplicationContext().getSharedPreferences("name_icons_list", Context.MODE_PRIVATE);
        SharedPreferences.Editor keyValuesEditor = keyValues.edit();

        for (String s : nameIcons.keySet()) {
            // use the name as the key, and the icon as the value
            keyValuesEditor.putString(s, nameIcons.get(s));
        }
        keyValuesEditor.commit();
        Map <String, ?> truc = keyValues.getAll();
        Log.d("test", "test: " + truc);
    }

    void wakeOnLan(String ipStr, String macStr) {
        int PORT = 9;

        try {
            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(ipStr);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();

            System.out.println("Wake-on-LAN packet sent.");
        }
        catch (Exception e) {
            System.out.println("Failed to send Wake-on-LAN packet: " + e);
            System.exit(1);
        }
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<KaldiActivity> activityReference;

        SetupTask(KaldiActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                Log.d("KaldiDemo", "Sync files in the folder " + assetDir.toString());

                Vosk.SetLogLevel(0);

                activityReference.get().model = new Model(assetDir.toString() + "/model-android");
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                activityReference.get().setErrorState(String.format(activityReference.get().getString(R.string.failed), result));
            } else {
                activityReference.get().setUiState(STATE_READY);
                activityReference.get().recognizeMicrophone();
            }
        }
    }

    private static class RecognizeTask extends AsyncTask<Void, Void, String> {
        WeakReference<KaldiActivity> activityReference;
        WeakReference<TextView> resultView;

        RecognizeTask(KaldiActivity activity, TextView resultView) {
            this.activityReference = new WeakReference<>(activity);
            this.resultView = new WeakReference<>(resultView);
        }

        @Override
        protected String doInBackground(Void... params) {
            KaldiRecognizer rec;
            long startTime = System.currentTimeMillis();
            StringBuilder result = new StringBuilder();
            try {
                rec = new KaldiRecognizer(activityReference.get().model, 16000.f, "[\"oh zero one two three four five six seven eight nine\"]");

                InputStream ais = activityReference.get().getAssets().open("10001-90210-01803.wav");
                if (ais.skip(44) != 44) {
                    return "";
                }
                byte[] b = new byte[4096];
                int nbytes;
                while ((nbytes = ais.read(b)) >= 0) {
                    if (rec.AcceptWaveform(b, nbytes)) {
                        result.append(rec.Result());
                    } else {
                        result.append(rec.PartialResult());
                    }
                }
                result.append(rec.FinalResult());
            } catch (IOException e) {
                return "";
            }
            return String.format(activityReference.get().getString(R.string.elapsed), result.toString(), (System.currentTimeMillis() - startTime));
        }

        @Override
        protected void onPostExecute(String result) {
            activityReference.get().setUiState(STATE_READY);
            resultView.get().append(result + "\n");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                new SetupTask(this).execute();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (speechService != null) {
            speechService.cancel();
            speechService.shutdown();
        }
    }


    public boolean isInString(String s, String [] arr) {
        for (String s2: arr) {
            if (s.contains(s2))
                return true;
        }
        return false;
    }

    @Override
    public void onResult(String hypothesis) {
        resultView.append(hypothesis + "\n");
        Log.d(TAG, "onResult: " + hypothesis);
        if (hypothesis.contains("allumé") && menu == menuEnum.MAINMENU) {
            Log.d("onResult", "onResult: allumer ordinateur détecté" );
            ((Button) findViewById(R.id.returnMenu)).setVisibility(View.VISIBLE);
            menu = menuEnum.WAKE;
            String text = getMacAdresses();
            TextView textAddresses = findViewById(R.id.macAddresses);
            textAddresses.setText(text);
        }

        else if (hypothesis.contains("éteindre") && menu == menuEnum.MAINMENU)
            Log.d("onResult", "onResult: éteindre ordinateur détecté" );
        else if (hypothesis.contains("ajoute") && menu == menuEnum.MAINMENU)
            Log.d("onResult", "onResult: ajouter ordinateur détecté" );
        else if (hypothesis.contains("supprimer") && menu == menuEnum.MAINMENU)
            Log.d("onResult", "onResult: supprimer ordinateur détecté" );
        else if (hypothesis.contains("test")) {
            Log.d("onResult", "onResult: test ordinateur détecté" );
            test();
        }
        else if (hypothesis.contains("retour") && menu != menuEnum.MAINMENU) {
            Log.d("onResult", "onResult: retour ordinateur détecté");
            //recognizeMicrophone();
            menu = menuEnum.MAINMENU;
            ((Button) findViewById(R.id.returnMenu)).setVisibility(View.GONE);
            TextView textAddresses = findViewById(R.id.macAddresses);
            textAddresses.setText("");
        }
        else if (menu == menuEnum.WAKE) {
            int num;
            Log.d(TAG, "onResult 42: " + hypothesis);
            if (isInString(hypothesis, new String[]{"hein", "eau","eaux","en","un", "premier"}))
                num = 1;
            else if (isInString(hypothesis, new String[]{"deux", "deuxième"}))
                num = 2;
            else if (isInString(hypothesis, new String[]{"trois", "troisième"}))
                num = 3;
            else if (isInString(hypothesis, new String[]{"quatre", "quatrième"}))
                num = 4;
            else if (isInString(hypothesis, new String[]{"cinq", "cinquième"}))
                num = 5;
            else if (isInString(hypothesis, new String[]{"six", "sixième"}))
                num = 6;
            else if (isInString(hypothesis, new String[]{"sept", "septième"}))
                num = 7;
            else if (isInString(hypothesis, new String[]{"huit", "huitème", "huitièmement"}))
                num = 8;
            else if (isInString(hypothesis, new String[]{"neuf", "neuvième"}))
                num = 9;
            else
                num = -1;
            Log.d(TAG, "NULMEROOOO: " + num);
            if (num == 1) {
                wakeOnLan("192.168.0.134", "F0-1D-BC-A0-BC-86");
            }
        }


    }

    @Override
    public void onPartialResult(String hypothesis) {
        resultView.append(hypothesis + "\n");
    }

    @Override
    public void onError(Exception e) {
        setErrorState(e.getMessage());
    }

    @Override
    public void onTimeout() {
        speechService.cancel();
        speechService = null;
        setUiState(STATE_READY);
    }

    private void setUiState(int state) {
        switch (state) {
            case STATE_START:
                resultView.setText(R.string.preparing);
                resultView.setMovementMethod(new ScrollingMovementMethod());
                findViewById(R.id.recognize_mic).setEnabled(false);
                break;
            case STATE_READY:
                resultView.setText(R.string.ready);
                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
                findViewById(R.id.recognize_mic).setEnabled(true);
                break;
            case STATE_DONE:
                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
                findViewById(R.id.recognize_mic).setEnabled(true);
                break;
            case STATE_FILE:
                resultView.setText(getString(R.string.starting));
                findViewById(R.id.recognize_mic).setEnabled(false);
                break;
            case STATE_MIC:
                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.stop_microphone);
                resultView.setText(getString(R.string.say_something));
                findViewById(R.id.recognize_mic).setEnabled(true);
                break;
        }
    }

    private void setErrorState(String message) {
        resultView.setText(message);
        ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
        findViewById(R.id.recognize_mic).setEnabled(false);
    }

    public void recognizeFile() {
        setUiState(STATE_FILE);
        new RecognizeTask(this, resultView).execute();
    }

    public void recognizeMicrophone() {
        if (speechService != null) {
            setUiState(STATE_DONE);
            speechService.cancel();
            speechService = null;
        } else {
            setUiState(STATE_MIC);
            try {
                KaldiRecognizer rec = new KaldiRecognizer(model, 16000.0f);
                speechService = new SpeechService(rec, 16000.0f);
                speechService.addListener(this);
                speechService.startListening();
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        }
    }

}
