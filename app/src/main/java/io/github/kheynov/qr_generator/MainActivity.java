package io.github.kheynov.qr_generator;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button genQR;
    Button genBAR;
    ImageView imageView;
    EditText editText;
    String dataToEncode;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        genBAR = findViewById(R.id.genBAR);
        genQR = findViewById(R.id.genQR);
        imageView = findViewById(R.id.img_view);
        editText = findViewById(R.id.edit_text);
        pb = findViewById(R.id.progressBar);
        pb.setVisibility(ProgressBar.INVISIBLE);
        genQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataToEncode = editText.getText().toString();
                if (dataToEncode.equals("") || dataToEncode == null) {
                    Toast.makeText(getApplicationContext(), "Error! Edit Text is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    GenerateQR gencode_Task = new GenerateQR();
                    gencode_Task.execute(dataToEncode, "qr");
                }
            }
        });
        genBAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataToEncode = editText.getText().toString();
                if (dataToEncode.equals("") || dataToEncode == null) {
                    Toast.makeText(getApplicationContext(), "Error! Edit Text is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    GenerateQR gencode_Task = new GenerateQR();
                    gencode_Task.execute(dataToEncode, "bar");
                }
            }
        });

    }
    class GenerateQR extends AsyncTask<String,Void, Bitmap>{
        BitMatrix bitMatrix;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(getApplicationContext(), "Started Generation", Toast.LENGTH_SHORT).show();
            pb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String[] objects) {
            BarcodeFormat format = null;
            if (Objects.equals(objects[1], "qr")){
                format = BarcodeFormat.QR_CODE;
            }else if(Objects.equals(objects[1], "bar")){
                format = BarcodeFormat.CODE_128;
            }
            try {
                bitMatrix = new MultiFormatWriter().encode(objects[0], format, 800, 800);
            } catch (IllegalArgumentException e) {
                return null;
            } catch (WriterException e) {
                e.printStackTrace();
            }
            int BitmapWidth = bitMatrix.getWidth();
            int BitmapHeight = bitMatrix.getHeight();
            int[] pixels = new int[BitmapWidth * BitmapHeight];
            for (int i = 0; i < BitmapHeight; i++) {
                int offset = i * BitmapWidth;
                for (int j = 0; j < BitmapWidth; j++) {
                    pixels[offset + j] = bitMatrix.get(j, i) ? getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(BitmapWidth, BitmapHeight, Bitmap.Config.ARGB_4444);
            bitmap.setPixels(pixels, 0, BitmapWidth, 0, 0, BitmapWidth, BitmapHeight);
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            pb.setVisibility(ProgressBar.INVISIBLE);
//  Toast.makeText(getApplicationContext(), "Generation finished", Toast.LENGTH_SHORT).show();
            imageView.setImageBitmap(bitmap);
        }
    }
}

