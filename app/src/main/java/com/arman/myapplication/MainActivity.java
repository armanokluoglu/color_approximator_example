package com.arman.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private ImageView colorView;
    private Button button;
    private TextView opTimer;
    private Button avgButton;
    private Button domButton;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private TextView red;
    private TextView green;
    private TextView blue;
    private int[] pixelsOfBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        imageView = findViewById(R.id.imageView);
        colorView = findViewById(R.id.colorView);
        opTimer = findViewById(R.id.opTimer);
        button = findViewById(R.id.button);
        avgButton = findViewById(R.id.avgButton);
        domButton = findViewById(R.id.domButton);
        red = findViewById(R.id.red);
        green = findViewById(R.id.green);
        blue = findViewById(R.id.blue);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        avgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int[] color = getColor();
            red.setText("Red: " + color[0]);
            green.setText("Green: " + color[1]);
            blue.setText("Blue: " + color[2]);
            }
        });

        domButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            int[] color = getDominantColor();
            red.setText("Red: " + color[0]);
            green.setText("Green: " + color[1]);
            blue.setText("Blue: " + color[2]);
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            red.setText("");
            green.setText("");
            blue.setText("");
            opTimer.setText("");
            colorView.setImageDrawable(null);
            avgButton.setVisibility(View.VISIBLE);
            domButton.setVisibility(View.VISIBLE);
        }
    }

    private int[] getDominantColor() {
        long start = System.currentTimeMillis();
        Bitmap artwork = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        int width = artwork.getWidth();
        int height = artwork.getHeight();
        pixelsOfBitmap = new int[width * height];
        artwork.getPixels(pixelsOfBitmap, 0, width, 0, 0, width, height);
        int r = 0;
        int g = 0;
        int b = 0;
        int redCluster = 0;
        int r_redCluster = 0;
        int g_redCluster = 0;
        int b_redCluster = 0;
        int greenCluster = 0;
        int r_greenCluster = 0;
        int g_greenCluster = 0;
        int b_greenCluster = 0;
        int blueCluster = 0;
        int r_blueCluster = 0;
        int g_blueCluster = 0;
        int b_blueCluster = 0;

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int pixel = getPixel(x,y, width);
                if(Color.red(pixel) > Color.blue(pixel) && Color.red(pixel) > Color.green(pixel)) {
                    redCluster += 1;
                    r += Color.red(pixel);
                    r_redCluster += Color.red(pixel);
                    g_redCluster += Color.green(pixel);
                    b_redCluster += Color.blue(pixel);
                } else if(Color.blue(pixel) > Color.red(pixel) && Color.blue(pixel) > Color.green(pixel)) {
                    blueCluster += 1;
                    b += Color.blue(pixel);
                    r_blueCluster += Color.red(pixel);
                    g_blueCluster += Color.green(pixel);
                    b_blueCluster += Color.blue(pixel);
                } else if(Color.green(pixel) > Color.blue(pixel) && Color.green(pixel) > Color.red(pixel)) {
                    greenCluster += 1;
                    g += Color.green(pixel);
                    r_greenCluster += Color.red(pixel);
                    g_greenCluster += Color.green(pixel);
                    b_greenCluster += Color.blue(pixel);
                }
            }
        }

        if(r > b && r > g) {
            r = r_redCluster / redCluster;
            g = g_redCluster / redCluster;
            b = b_redCluster / redCluster;
        } else if(b > r && b > g) {
            r = r_blueCluster / blueCluster;
            g = g_blueCluster / blueCluster;
            b = b_blueCluster / blueCluster;
        } else if(g > r && g > b) {
            r = r_greenCluster / greenCluster;
            g = g_greenCluster / greenCluster;
            b = b_greenCluster / greenCluster;
        }

        int[] color = new int[]{r, g, b};
        colorView.setImageBitmap(createImage(colorView.getWidth(),colorView.getHeight(), getIntFromColor(r, g, b)));
        long end = System.currentTimeMillis();
        opTimer.setText("The operation was completed in " + (end - start) + " milliseconds");
        return color;
    }

    private int[] getColor() {
        long start = System.currentTimeMillis();
        Bitmap bitmapImageView = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        int width = bitmapImageView.getWidth();
        int height = bitmapImageView.getHeight();
        pixelsOfBitmap = new int[width * height];
        bitmapImageView.getPixels(pixelsOfBitmap, 0, width, 0, 0, width, height);
        int[] color = new int[3];
        int redValue = 0;
        int blueValue = 0;
        int greenValue = 0;
        int whiteCounter = 0;

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int pixel = getPixel(x,y, width);
                if(Color.red(pixel) == Color.blue(pixel) && Color.red(pixel) == Color.green(pixel)) {
                    whiteCounter++;
                } else {
                    redValue += Color.red(pixel);
                    blueValue += Color.blue(pixel);
                    greenValue += Color.green(pixel);
                }
            }
        }

        redValue = redValue/((height*width) - whiteCounter);
        blueValue = blueValue/((height*width) - whiteCounter);
        greenValue = greenValue/((height*width) - whiteCounter);
        color[0] = redValue;
        color[1] = greenValue;
        color[2] = blueValue;

        colorView.setImageBitmap(createImage(colorView.getWidth(),colorView.getHeight(), getIntFromColor(redValue, greenValue, blueValue)));
        long end = System.currentTimeMillis();
        opTimer.setText("The operation was completed in " + (end - start) + " milliseconds");
        return color;
    }

    private int getPixel(int x, int y, int width) {
        return pixelsOfBitmap[x + y * width];
    }

    private Bitmap createImage(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }

    private int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}