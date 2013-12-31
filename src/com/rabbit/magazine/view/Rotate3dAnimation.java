package com.rabbit.magazine.view;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.graphics.Camera;
import android.graphics.Matrix;

public class Rotate3dAnimation extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private Camera mCamera;
    private String mAxis;

    public Rotate3dAnimation(float fromDegrees, float toDegrees,float centerX, float centerY,String axis) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mAxis=axis;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);
        float centerX = mCenterX;
        float centerY = mCenterY;
        Camera camera = mCamera;
        Matrix matrix = t.getMatrix();
        camera.save();
        if("Y".equals(mAxis)){
        	camera.rotateY(degrees);
        }else if("X".equals(mAxis)){
        	camera.rotateX(degrees);
        }
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}
