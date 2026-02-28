
package com.unitol.namakoti;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.canhub.cropper.CropImageView;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class CropperActivity extends Activity {

    // Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    private static final String TAG = "CropperActivity";

    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

    Bitmap croppedImage;
    private String selectedImagePath = "";

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cropper);

        // Initialize components of the app
        final CropImageView cropImageView = findViewById(R.id.CropImageView);
        ImageView logo = findViewById(R.id.logo);
        ImageView back = findViewById(R.id.back);
        logo.setImageResource(R.drawable.upload_image);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedImagePath = extras.getString(ConstantsManager.SELECTED_IMAGE_PATH);
            if (!TextUtils.isEmpty(selectedImagePath)) {
                Debug.e(TAG, "url:" + selectedImagePath);
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(selectedImagePath)
                        .transition(BitmapTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                DisplayMetrics me = NamaKotiUtils.getDisplayMetrics(CropperActivity.this);
                                int h = me.heightPixels;
                                int ht = (int) (0.2f * h);
                                int w = me.widthPixels;
                                int wd = (int) (0.6f * w);
                                Log.i(TAG, "selectedImagePath: h: " + h + ", ht:" + ht + ", w:" + w + ",wd:" + wd + ", path:" + selectedImagePath);
                                Bitmap bitmap = Bitmap.createScaledBitmap(resource, wd, ht, false);
                                cropImageView.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                            }
                        });
            }
        } else {
            Log.i(TAG, "selectedImagePath is null");
        }

        //Sets the rotate button
        final Button rotateButton = findViewById(R.id.Button_rotate);
        rotateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
            }
        });

        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);

        final Button cropButton = findViewById(R.id.Button_crop);
        cropButton.setOnClickListener(v -> {
            croppedImage = cropImageView.getCroppedImage();
            ImageView croppedImageView = findViewById(R.id.croppedImageView);
            croppedImageView.setImageBitmap(croppedImage);

            String root = getFilesDir().toString();//Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/Namakoti");
            if (!myDir.exists()) {
                boolean success = myDir.mkdirs();
                Debug.e("cacheDir", "success::" + success + ">>" + myDir.getAbsolutePath() + "<< is success");
            }
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Handwritten-" + n + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists()) {
                boolean success =   file.delete();
                Debug.e("cacheDir", "success::" + success + ">>" + file.getAbsolutePath() + "<< is success");
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                croppedImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                MyApplication.mEditor.putString(getResources().getString(R.string.hand_written_image_namakoti), fname).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setResult(RESULT_OK);
            finish();
        });

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
