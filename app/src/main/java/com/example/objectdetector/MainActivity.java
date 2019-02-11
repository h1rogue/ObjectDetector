package com.example.objectdetector;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap bitmaImage;
    private Button snap, detect;
    private TextView textviewLabel;
    private FirebaseVisionImage image;
    private FirebaseVisionLabelDetector labelDetector;
    private FirebaseVisionCloudLabelDetector cloudlabeldetector;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        imageView = findViewById(R.id.imageview);
        snap = findViewById(R.id.button);
        detect = findViewById(R.id.button2);
        textviewLabel = findViewById(R.id.textView);

        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectImage();
            }
        });
    }

    private void detectImage() {
     progressDialog.show();
        image = FirebaseVisionImage.fromBitmap(bitmaImage);
        Log.d("working", "image vision is working");

        FirebaseVisionLabelDetectorOptions options = new FirebaseVisionLabelDetectorOptions
                .Builder()
                .setConfidenceThreshold(0.7f)
                .build();


        cloudlabeldetector=
                FirebaseVision.getInstance().getVisionCloudLabelDetector();

        cloudlabeldetector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionCloudLabel> firebaseVisionCloudLabels) {
                        progressDialog.dismiss();
                        for(FirebaseVisionCloudLabel label: firebaseVisionCloudLabels){
                            String cloudlabel="Object: "+label.getLabel()+"\n"+"Confidence: "+label.getConfidence();
                            textviewLabel.setText(cloudlabel);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "labelling failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        Log.d("working", "image labeler is working");
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmaImage = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmaImage);
        }
    }
}
