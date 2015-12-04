package com.gavin.amazingcamera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gavin.amazingcamera.adapter.PhotoAdapter;
import com.gavin.amazingcamera.base.eventbus.EventBusConfiguration;
import com.gavin.amazingcamera.base.view.BaseActivity;
import com.gavin.amazingcamera.databinding.ActivityMainBinding;
import com.gavin.amazingcamera.delegate.OcrControllerDelegate;
import com.gavin.amazingcamera.eventbus.OcrEvent;
import com.gavin.amazingcamera.photopicker.PhotoPickerActivity;
import com.gavin.amazingcamera.photopicker.utils.PhotoPickerIntent;
import com.gavin.amazingcamera.util.BitmapUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity {

    enum RequestCode {
        Button(R.id.button),
        ButtonNoCamera(R.id.button_no_camera),
        ButtonOnePhoto(R.id.button_one_photo),
        ButtonPhotoGif(R.id.button_photo_gif);

        @IdRes
        final int mViewId;

        RequestCode(@IdRes int viewId) {
            mViewId = viewId;
        }
    }

    private ActivityMainBinding activityBinding;

    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;

    private ArrayList<String> selectedPhotos = new ArrayList<>();

    private final static int REQUEST_CODE = 1;

    private final int SYSTEM_CAMERA_REQUESTCODE = 2;
    private final int MYAPP_CAMERA_REQUESTCODE = 3;
    private final String SAVE_PIC_DIR = "AmazingCamera";
    private Uri imageFileUri = null;
    private final int TYPE_FILE_IMAGE = 1;
    private final int TYPE_FILE_VEDIO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        photoRecyclerView = activityBinding.recyclerView;
        photoAdapter = new PhotoAdapter(this, selectedPhotos);

        photoRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));
        photoRecyclerView.setAdapter(photoAdapter);

        activityBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(RequestCode.Button);
            }
        });

        activityBinding.buttonNoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(RequestCode.ButtonNoCamera);
            }
        });

        activityBinding.buttonOnePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(RequestCode.ButtonOnePhoto);
            }
        });

        activityBinding.buttonPhotoGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(RequestCode.ButtonPhotoGif);
            }
        });

        // 打开系统原生的相机界面
        activityBinding.fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "打开了系统原生的相机", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFileUri = getOutFileUri(TYPE_FILE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                startActivityForResult(intent, SYSTEM_CAMERA_REQUESTCODE);
            }
        });

        // 打开自定义相机的相机界面
        activityBinding.fabAmazing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "打开了自定义的相机", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (checkCameraHardWare(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), MyCameraActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "不好意思，您的设备并不存在相机！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected EventBusConfiguration loadEventBusConfiguration() {
        return EventBusConfiguration.KEEP_ALIVE_ONCREATE_PAIR;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void previewPhoto(Intent intent) {
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<String> photos = new ArrayList<>();
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            activityBinding.photoLayout.setVisibility(View.GONE);
            activityBinding.recyclerView.setVisibility(View.VISIBLE);
            imageFileUri = null;

            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();

            if (photos != null) {
                selectedPhotos.addAll(photos);
            }

            photoAdapter.notifyDataSetChanged();
        } else if (resultCode == RESULT_OK && requestCode == SYSTEM_CAMERA_REQUESTCODE) {
            if (imageFileUri != null) {
                Log.d("MyPicture", imageFileUri.getEncodedPath());
                activityBinding.photoLayout.setVisibility(View.VISIBLE);
                activityBinding.recyclerView.setVisibility(View.GONE);
                setPicToImageView(activityBinding.photoImage, new File(imageFileUri.getEncodedPath()));
                imageFileUri = null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay!
            onClick(RequestCode.values()[requestCode].mViewId);
        } else {
            // permission denied, boo! Disable the
            // functionality that depends on this permission
            Toast.makeText(this, "No read storage permission! Cannot perform the action.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {

        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                // No need to explain to user as it is obvious
                return false;
            default:
                return true;
        }
    }

    // 点击按钮的事件
    private void checkPermission(@NonNull RequestCode requestCode) {

        int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionState != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        requestCode.ordinal());

            }
        } else {
            // Permission granted
            onClick(requestCode.mViewId);
        }
    }

    private void onClick(@IdRes int viewId) {

        switch (viewId) {
            case R.id.button: {
                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                intent.setPhotoCount(9);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            }

            case R.id.button_no_camera: {
                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                intent.setPhotoCount(7);
                intent.setShowCamera(false);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            }

            case R.id.button_one_photo: {
                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                intent.setPhotoCount(1);
                intent.setShowCamera(true);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            }

            case R.id.button_photo_gif: {
                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                intent.setPhotoCount(4);
                intent.setShowCamera(true);
                intent.setShowGif(true);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            }
        }
    }

    // -----------------------Android大图的处理方式---------------------------
    private void setPicToImageView(ImageView imageView, File imageFile) {
        int imageViewWidth = imageView.getWidth();
        int imageViewHeight = imageView.getHeight();
        BitmapFactory.Options opts = new BitmapFactory.Options();

        // 设置这个，值得到Bitmap的属性信息放入Opts，而不是把Bitmap加载到内存中
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getPath(), opts);

        int bitmapWidth = opts.outWidth;
        int bitmapHeight = opts.outHeight;

        int scale = Math.max(imageViewWidth / bitmapWidth, imageViewHeight / bitmapHeight);

        // 缩放的比例
        opts.inSampleSize = scale;
        // 设置为false，表示不仅Bitmap的苏醒，也要加载bitmap
        opts.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath(), opts);

        String bitmap64Str = BitmapUtil.convertIconToString(bitmap);

        Log.d("MyPicture", "获得的图片的Bitmap64位Str为：" + bitmap64Str);
        Toast.makeText(getApplicationContext(), "获得的图片的Bitmap64位Str为：" + bitmap64Str, Toast.LENGTH_SHORT).show();

        OcrControllerDelegate.ocrController.getIDCardInfo(bitmap64Str);

        imageView.setImageBitmap(bitmap);
    }

    // -----------------------生成Uri---------------------------------------
    /**
     * 得到输出文件的URI
     */
    private Uri getOutFileUri(int fileType) {
        return Uri.fromFile(getOutFile(fileType));
    }

    /**
     * 深处输入文件
     *
     * @param fileType
     * @return
     */
    private File getOutFile(int fileType) {

        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_REMOVED.equals(storageState)) {
            Toast.makeText(getApplicationContext(), "不好意思，您的设备没有SD卡！", Toast.LENGTH_SHORT).show();
            return null;
        }

        File mediaStorageDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                , SAVE_PIC_DIR);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyPictures", "创建图片存储路径目录失败");
                Log.d("MyPictures", "mediaStorageDir : " + mediaStorageDir.getPath());
                return null;
            }
        }

        File file = new File(getFilePath(mediaStorageDir, fileType));

        return file;
    }

    /**
     * 自动生成输出文件的路径
     *
     * @param mediaStorageDir
     * @param fileType
     * @return
     */
    private String getFilePath(File mediaStorageDir, int fileType) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());

        String filePath = mediaStorageDir.getPath() + File.separator;

        if (fileType == TYPE_FILE_IMAGE) {
            filePath += ("IMG_" + timeStamp + ".jpg");
        } else if (fileType == TYPE_FILE_VEDIO) {
            filePath += ("VIDEO_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return filePath;
    }

    /**
     * 监测设备是否拥有摄像头
     *
     * @param context
     * @return
     */
    private boolean checkCameraHardWare(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }

    // Ocr的订阅事件
    public void onEventMainThread(OcrEvent event) {

    }
}
