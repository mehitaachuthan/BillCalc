package test.example.billcalc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.widget.Toast;
import android.provider.MediaStore;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.widget.Button;
import android.view.View;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.net.Uri;
import android.util.Log;
import java.io.Serializable;
import java.util.Set;
import java.util.Iterator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.Text;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button camera_button = findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dispatchTakePictureIntent();
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch(ActivityNotFoundException e){
            Toast toast_error_camera = Toast.makeText(getApplicationContext(), "Error in opening camera", Toast.LENGTH_SHORT);
            toast_error_camera.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            // iterate through the bundle and get the key values
            Set<String> keys = extras.keySet();
            Iterator<String> pointer = keys.iterator();
            while(pointer.hasNext()){
                String key_name = pointer.next();
                Log.i("Keys:", key_name);
                Log.i("Keys", extras.get(key_name).toString());
            }
            // get bitmap image from the bundle data and display in image view on screen
            Bitmap imageBitmap = (Bitmap)(extras.get("data"));
            ImageView camera_img = findViewById(R.id.camera_image);
            camera_img.setImageBitmap(imageBitmap);
            InputImage input_img = InputImage.fromBitmap(imageBitmap, 0);
            // build text recognizer
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            Task<Text> result = recognizer.process(input_img).addOnSuccessListener(new OnSuccessListener<Text>(){
                @Override
                public void onSuccess(Text visionText){
                    // display the text
                    String text_in_img = visionText.getText();
                    TextView img_text = findViewById(R.id.image_text);
                    img_text.setText(text_in_img);
                }
            }).addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception e){
                    Toast failed_text_recognition = Toast.makeText(getApplicationContext(), "Could Not Read Text", Toast.LENGTH_SHORT);
                    failed_text_recognition.show();
                }
            });
            Toast toast_image_taken = Toast.makeText(getApplicationContext(), "Taken the image", Toast.LENGTH_SHORT);
            toast_image_taken.show();
        } else {
            Toast toast_failed_camera_return = Toast.makeText(getApplicationContext(), "Failed Camera Return", Toast.LENGTH_SHORT);
            toast_failed_camera_return.show();
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        // save name of file
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureFromStorage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast toast_take_image_from_storage = Toast.makeText(getApplicationContext(), "Could Not Retrieve Image From Storage", Toast.LENGTH_SHORT);
                toast_take_image_from_storage.show();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

        private void galleryAddPic() {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(currentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }

}