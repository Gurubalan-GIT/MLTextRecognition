/*
Author: Gurubalan Harikrishnan
Tools: Android Studio Canary-3.2.0
This is licensed and hence for educational purposes only.
 */
package textrecognition.its.guru.mltextrecognition;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button snapButton;
    private Button detectButton;
    private ImageView imageView;
    private TextView textView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        snapButton=findViewById(R.id.snapButton);
        detectButton=findViewById(R.id.detectButton);
        imageView=findViewById(R.id.imageView);
        textView=findViewById(R.id.textView);
        //Setting onClick listeners.
        snapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();
            }
        });
    }
    //Creating methods to enable Camera API.
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
            bitmap = (Bitmap) extras.get("data");
            //Setting bitmap image to the image taken from the camera without saving the image. Image is cached.
            imageView.setImageBitmap(bitmap);
        }
    }
    private void detectText(){
        //Using MLkit from Firebase Vision and using a pre-built model.
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processText(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    /*Extracting the result to a list. The block will recognize the text and store in the form of a block object.
    The blocks are stored in a list of return type Block from Firebase Vision.*/
    private void processText(FirebaseVisionText text) {
        List<FirebaseVisionText.Block> blocks = text.getBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(MainActivity.this, "No Text recognized.", Toast.LENGTH_LONG).show();
            return;
        }
        //Traversing through the blocks stored in the list and extracting each character to a TextView and displaying them.
        for (FirebaseVisionText.Block block : text.getBlocks()) {
            String txt = block.getText();
            textView.setTextSize(24);
            textView.setText(txt);
        }
    }
}
