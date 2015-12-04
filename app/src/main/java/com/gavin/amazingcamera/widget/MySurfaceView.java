package com.gavin.amazingcamera.widget;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * Author: Gavin
 * E-mail: gavin.zhang@healthbok.com
 * Date:  2015/12/3 0003
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;
    private Resources mResources;
    private Camera mCamera = null;
    private SurfaceHolder surfaceHolder = null;
    private List<Size> mSupportedPreviewSizes;
    private Size mPreviewSize;

    public MySurfaceView(Context context, Camera camera) {
        super(context);
        this.mContext = context;
        this.mCamera = camera;
        this.mResources = mContext.getResources();
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
    }
    public MySurfaceView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        //根本没有可处理的SurfaceView
        if (surfaceHolder.getSurface() == null){
            return ;
        }

        //先停止Camera的预览
        try{
            mCamera.stopPreview();
        }catch(Exception e){
            e.printStackTrace();
        }

        // 这里可以做变换。
        if(mCamera != null){
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(parameters);
        }

        //重新开启Camera的预览功能
        try{
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        Integer widthdp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1024, mResources.getDisplayMetrics());
        Integer heightdp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 768, mResources.getDisplayMetrics());
        final int width = widthdp;
        final int height = heightdp;
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }
}
