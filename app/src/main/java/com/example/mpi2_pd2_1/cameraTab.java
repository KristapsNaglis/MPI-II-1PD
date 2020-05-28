package com.example.mpi2_pd2_1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class cameraTab extends Fragment {

    public cameraTab() {
        // Required empty public constructor
    }


    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private ImageView imageView;
    private LinearLayout layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_camera_tab, container, false);

        Button button = (Button) rootView.findViewById(R.id.takeImageButton);
        imageView = (ImageView) rootView.findViewById(R.id.thumbnailImage);
        layout = (LinearLayout) rootView.findViewById(R.id.linear);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                try {
                    startActivityForResult(intent,
                            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    //  throws runtime exception :"Failed to connect to camera service"
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root);

        deleteDir(dir);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            // convert byte array to Bitmap


            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageView.setImageBitmap(bitmap);

            // Save image to external memory
            saveImage(bitmap);

            // Put image into carousel gallery
            ImageView imageView = new ImageView(layout.getContext());
            //imageView.setId(next);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setMaxHeight(200);

            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            layout.addView(imageView);
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean deleteDir(File dir) {
        Boolean isDirEmpty;
        String[] children = dir.list();
        isDirEmpty = children == null || children.length <= 0;
        // Delete all files inside directory
        Log.d(TAG, "deleteDir: Before delete. Is empty? - " + isDirEmpty);
        if (dir.isDirectory()) {
            //String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }




}
