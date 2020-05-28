package com.example.mpi2_pd2_1;


import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class audioTab extends Fragment {
    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 222;

    private Button audioRecordButton;
    private EditText audioFileNameTextField;
    private ListView audioFileListView;
    private MediaRecorder audioRecorder;

    private boolean audioIsBeingRecorded = false;

    public audioTab() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_audio_tab, container, false);

        audioFileNameTextField = (EditText) rootView.findViewById(R.id.audioFileNameTextField);
        audioRecordButton = (Button) rootView.findViewById(R.id.audioRecordButton);
        audioFileListView = (ListView) rootView.findViewById(R.id.audioFileList);

        final String root = Environment.getExternalStorageDirectory().toString();
        final File directory = new File(root + "/saved_recordings");

        readAllFiles(root, directory, audioFileListView);

        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

            audioRecordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!audioIsBeingRecorded) {

                        audioRecorder = new MediaRecorder();
                        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

                        if (!directory.exists()) {
                            directory.mkdirs();
                        }

                        String fileName = "audioRecording-" + getDateTimeNow() + ".3gp";
                        File file = new File(directory, fileName);

                        if (file.exists()) {
                            file.delete();
                        }

                        audioRecordButton.setText("Audio is recording …");
                        audioRecorder.setOutputFile(directory + "/" + fileName);
                        audioFileNameTextField.setText(fileName);

                        try {
                            audioRecorder.prepare();
                            audioRecorder.start();
                            audioIsBeingRecorded = true;
                            Toast.makeText(getActivity(), "Starting Recording", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.d("Kristapsaudio", e.toString());

                        }
                    } else {
                        try {
                            readAllFiles(root, directory, audioFileListView);
                            audioRecorder.stop();
                            audioRecorder.reset();
                            audioIsBeingRecorded = false;
                            audioRecordButton.setText("Press To Record Audio");
                            Toast.makeText(getActivity(), "Stopping Recording", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.d("Kristapsaudio", e.toString());
                            Log.d("Kristapsaudio", "Could not stop audio recording");
                        }
                    }

                }
            });
        } else {
            String[] permissionRequest = {Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissionRequest, RECORD_AUDIO_PERMISSION_REQUEST_CODE);
        }

        return rootView;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        audioRecorder.release();
        deleteAllFiles();
    }

    public void readAllFiles(String root, File dir, ListView listView){
        try {
            listView = audioFileListView;
            List<String> fileListArray = new ArrayList<String>();
            File[] files = dir.listFiles();
            for (File file : files) {
                Log.d("Kristapsfiles", "FileName:" + file.getName());
                fileListArray.add(file.getName());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    this.getContext(),
                    android.R.layout.simple_list_item_1,
                    fileListArray);
            listView.setAdapter(arrayAdapter);
        } catch (Exception e) {
            Log.d("INFO", "No files are in folder");
        }
    }

    private void deleteAllFiles(){
        final String root = Environment.getExternalStorageDirectory().toString();
        final File dir = new File(root + "/saved_recordings");
        File[] files = dir.listFiles();
        for (File file : files) {
            file.delete();
        }
    }

    private String getDateTimeNow(){
        // Set date formatting. Needed for date to string conversion
        String pattern = "MM-dd-yyyy_HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);

        Date currentTime = Calendar.getInstance().getTime();

        return df.format(currentTime);
    }

}
