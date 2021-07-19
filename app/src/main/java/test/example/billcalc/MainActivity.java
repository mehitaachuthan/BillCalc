package test.example.billcalc;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.widget.Toast;
import android.provider.MediaStore;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.widget.Button;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

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
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Toast toast_image_taken = Toast.makeText(getApplicationContext(), "Taken the image", Toast.LENGTH_SHORT);
            toast_image_taken.show();
            // call image view and set the bit map
        }
    }
}