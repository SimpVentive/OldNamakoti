package com.namakoti.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.namakoti.R;
import com.namakoti.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BaseFragment extends Fragment {

    protected BaseActivity mBaseActivity;
    protected Context mBaseContext;
    private ProgressDialog mProgressDialog;
    private FragmentManager mFragmentManager;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        mBaseActivity = (BaseActivity) getActivity();
        mBaseContext = getContext();
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void setToolbarTitle(String title) {
        if (mBaseActivity.getSupportActionBar() != null && title != null) {
            mBaseActivity.getSupportActionBar().setTitle(title);
            mBaseActivity.getSupportActionBar().setLogo(R.drawable.ic_nk_logo_small);

//            getSupportActionBar().setDisplayShowCustomEnabled(true);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    public static View getToolbarLogoIcon(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if (potentialViews.size() > 0) {
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }

    /**
     * Shows the progress dialog
     */
    protected void showProgress(final boolean show) {
        try {
            if (mProgressDialog == null && show) {
                // First time initialize and show
                mProgressDialog = ProgressDialog.show(getActivity(), "", "Please wait...", true, false);
            } else if (mProgressDialog != null && show && !mProgressDialog.isShowing()) {
                mProgressDialog.show();
            } else if (mProgressDialog != null && !show && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void selectImage(final int requestCamera, final int selectFile) {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                String userChoosenTask;

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    cameraIntent(requestCamera);
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    galleryIntent(selectFile);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void cameraIntent(int requestCamera) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        startActivityForResult(intent, requestCamera);
    }

    protected void galleryIntent(int selectFile) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, ""), selectFile);
    }

    protected Bitmap onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbnail;
    }

    protected Bitmap onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }

    protected String getStringImage(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap = getResizedBitmap(bitmap, 500);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }

        return "";
    }

    /**
     * reduces the size of the image
     *
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * This method used to set profile picture from base64
     *
     * @param base64Image
     */
    protected Bitmap getBitmapFromString(String base64Image) {
        Bitmap bm = null;
        if (base64Image != null) {
            byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
            bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }
        return bm;
    }


    /**
     * Add a fragment here
     */
    public void addFragment(final int id, Fragment fragment, boolean addToBackStack) {
        mFragmentManager = mBaseActivity.getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(id, fragment, fragment.getClass().getName());

        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        } else {
            transaction.addToBackStack(null);
        }
        transaction.commit();

        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fr = mFragmentManager.findFragmentById(id);
                if (fr != null) {
                    if (Constants.LOG)
                        Log.e("fragment=", fr.getClass().getSimpleName());
                }
            }
        });
    }

    public void popFragment(String tag) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

}
