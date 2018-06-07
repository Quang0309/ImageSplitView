package com.example.cpu10475_local.customimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

public class SplitImageView extends View {
    private Bitmap left,right;
    int currentLine;
    int mWidht,mHeight;
    Runnable decodeResource;
    Handler handler;
    Paint linePaint;
    boolean isTouch = false;
    int rLeft,rRight;
    int colorDivLine = -1;
    public SplitImageView(Context context) {
        super(context);
        init();
    }

    public SplitImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.SplitImageView,0,0);
        try {
            rLeft = a.getResourceId(R.styleable.SplitImageView_imgLeft,0);
            rRight = a.getResourceId(R.styleable.SplitImageView_imgRight,0);
            colorDivLine = a.getColor(R.styleable.SplitImageView_colorDivLine,0);
         //   Log.e("color",Integer.toString(R.styleable.SplitImageView_colorDivLine));
        }finally {
            a.recycle();
        }
        init();
    }

    public SplitImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.SplitImageView,0,0);
        try {
            rLeft = a.getResourceId(R.styleable.SplitImageView_imgLeft,0);
            rRight = a.getResourceId(R.styleable.SplitImageView_imgRight,0);

        }finally {
            a.recycle();
        }
        init();
    }
    private void init()
    {
        decodeResource = new Runnable() {
            @Override
            public void run() {
                if (rLeft==0||rRight==0)
                    return;
                left = BitmapFactory.decodeResource(getResources(),rLeft);
                right = BitmapFactory.decodeResource(getResources(),rRight);
                left = Bitmap.createScaledBitmap(left,mWidht,mHeight,false);
                right = Bitmap.createScaledBitmap(right,mWidht,mHeight,false);
            }
        };
        handler = new Handler();
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if(colorDivLine!=-1)
            linePaint.setColor(colorDivLine);
        else
            linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(10);

        setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
               // Log.e("position",Float.toString(event.getX()));

                currentLine = (int) event.getX();
               // Log.e("position",Integer.toString(currentLine));
                invalidate();

                return true;
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    /*case MotionEvent.ACTION_DOWN:
                    {
                       *//* Log.e("X",Float.toString(event.getX()));
                        Log.e("X",Float.toString(currentLine));*//*
                        if(event.getX() > currentLine-50 && event.getX()<currentLine+50)
                        {
                           *//* Log.e("action","down");*//*
                            isTouch = true;
                        }
                        else
                            return false;
                        break;
                    }*/
                    case MotionEvent.ACTION_MOVE:
                    {
                        if((event.getX() > currentLine-50 && event.getX()<currentLine+50) || isTouch)
                        {
                            isTouch = true;
                            currentLine = (int) event.getX();
                        }
                        else
                            return false;
                        break;
/*
                        if(isTouch)
                        {
                           *//* Log.e("action","move");*//*
                            currentLine = (int) event.getX();
                        }
                        break;*/
                    }
                   case MotionEvent.ACTION_UP:
                    {

                        isTouch = false;
                        break;
                    }
                }
                invalidate();
                return true;
            }
        });


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(right == null ||left == null)
            return;
        canvas.save();
        canvas.clipRect(currentLine,0,mWidht,mHeight);
        canvas.drawBitmap(right,0,0,null);
        canvas.restore();
        canvas.save();
        canvas.clipRect(0,0,currentLine,mHeight);
        canvas.drawBitmap(left,0,0,null);
        canvas.restore();
        canvas.drawLine(currentLine,0,currentLine,mHeight,linePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mHeight = h;
        mWidht = w;
        currentLine = mWidht/2;
        handler.post(decodeResource);
    }
}
