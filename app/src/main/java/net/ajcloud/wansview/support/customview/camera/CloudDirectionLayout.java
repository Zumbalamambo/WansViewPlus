package net.ajcloud.wansview.support.customview.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


/**
 * Created by
 */
public class CloudDirectionLayout extends FrameLayout {
	private ViewDragHelper mDragger;
    private View mAutoBackView;
    private Point mAutoBackOriginPos = new Point();
    private Dir lastCirlDir = Dir.CENTER;
    private Dir curCirlDir = Dir.CENTER;
    private Dir clickCirDir = Dir.CENTER;
    private OnSteerListener listener;
    private Paint mBlackPaint;
    private Paint mGreenPaint;
    private Paint mTransparentPaint;
    private Bitmap top, bottom, left, right;
    int height;
    int width;
    int centerX;
    int centerY;
    int outImageRadius;
    int innerImageRadius;
    int outRadius;
    int innerRadius;
    private boolean showSector = false;
    private int mStart = -1;

    public CloudDirectionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);//必须 
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                if (!canDrag()) {
                    return false;
                }
                return child == mAutoBackView;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
            	float cx = left + width / 6 - width/2;
            	float cy = child.getTop() + width / 6 - width / 2;
            	
            	if (cx * cx + cy * cy > (width/3) * (width/3)) {
            		return child.getLeft();
            	} else {
            		return left;
            	}
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
            	float cx = child.getLeft() + width / 6 - width/2;
            	float cy = top + width / 6 - width / 2;
            	if (cx * cx + cy * cy > (width/3) * (width/3)) {
            		return child.getTop();
            	} else {
            		return top;
            	}
            }

            /* 手指释放的时候回调 */
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                //mAutoBackView手指释放时可以自动回去
                if (releasedChild == mAutoBackView) {
                	curCirlDir = Dir.CENTER;
                    mDragger.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
                    invalidate();
                }
            }

            //在边界拖动时回调
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
               // mDragger.captureChildView(mEdgeTrackerView, pointerId);
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
            	return getMeasuredWidth() - child.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
            	return getMeasuredHeight()-child.getMeasuredHeight();
            }
        });
        
        mDragger.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        initPaint();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        if (!canDrag()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (listener != null) {
                    listener.onTouchDown();
                }
                if ((mStart = pointInSector(x, y)) > 0) {
                    showSector = true;
                    switch (mStart) {
                        case 45:
                            curCirlDir = Dir.Bottom;
                            mDragger.smoothSlideViewTo(mAutoBackView,
                                    mAutoBackOriginPos.x, height - mAutoBackView.getHeight());
                            break;
                        case 135:
                            curCirlDir = Dir.Left;
                            mDragger.smoothSlideViewTo(mAutoBackView,
                                    0, mAutoBackOriginPos.y);
                            break;
                        case 225:
                            curCirlDir = Dir.Top;
                            mDragger.smoothSlideViewTo(mAutoBackView,
                                    mAutoBackOriginPos.x, 0);
                            break;
                        case 315:
                            curCirlDir = Dir.Right;
                            mDragger.smoothSlideViewTo(mAutoBackView,
                                    width - mAutoBackView.getWidth(), mAutoBackOriginPos.y);
                            break;
                        default:
                            curCirlDir = Dir.CENTER;
                            break;
                    }
                    if (listener != null) {
                        if (curCirlDir == Dir.Bottom) {
                            listener.onBottomTouch();
                        } else if (curCirlDir == Dir.Left) {
                            listener.onLeftTouch();
                        } else if (curCirlDir == Dir.Top) {
                            listener.onTopTouch();
                        } else if (curCirlDir == Dir.Right) {
                            listener.onRightTouch();
                        }
                    }
                    clickCirDir = curCirlDir;
                    lastCirlDir = curCirlDir;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Dir tmpCirDir = CirlDeDirection();
                if ( clickCirDir!= Dir.CENTER && clickCirDir != tmpCirDir) {
                    break;
                }
                clickCirDir = Dir.CENTER;
                curCirlDir = tmpCirDir;
            	if (lastCirlDir != curCirlDir) {
            		if (curCirlDir != Dir.CENTER){
            			showSector = true;
            			if (listener != null) {
                            Log.e("lastCirlDir" + curCirlDir, "onStop:" + lastCirlDir);

                            if (curCirlDir == Dir.Bottom) {
                                listener.onBottomTouch();
                            } else if (curCirlDir == Dir.Left) {
                                listener.onLeftTouch();
                            } else if (curCirlDir == Dir.Top) {
                                listener.onTopTouch();
                            } else if (curCirlDir == Dir.Right) {
                                listener.onRightTouch();
                            }
            			}
            		} else {
                        if (lastCirlDir != Dir.CENTER) {
                            listener.onStop();
                        }
            			showSector = false; 
            		}
                    lastCirlDir = curCirlDir;
            		invalidate();
            	}
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                showSector = false;
                if (listener != null) {
                    listener.onStop();
                    listener.onTouchLeave();
                }
                mDragger.smoothSlideViewTo(mAutoBackView, mAutoBackOriginPos.x, mAutoBackOriginPos.x);
                curCirlDir = Dir.CENTER;
                lastCirlDir = Dir.CENTER;
                clickCirDir = Dir.CENTER;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if(mDragger.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mAutoBackOriginPos.x = mAutoBackView.getLeft();
        mAutoBackOriginPos.y = mAutoBackView.getTop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAutoBackView = getChildAt(0);
    }

    private void initPaint() {
        mBlackPaint = new Paint();
        mBlackPaint.setColor(getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_paint1));
        mBlackPaint.setStyle(Paint.Style.FILL);
        mBlackPaint.setAntiAlias(true);

        mGreenPaint = new Paint();
        mGreenPaint.setColor(getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_paint2));
        mGreenPaint.setStyle(Paint.Style.FILL);
        mGreenPaint.setAntiAlias(true);

        mTransparentPaint = new Paint();
        mTransparentPaint.setColor(getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_inner_circle));
        mTransparentPaint.setStyle(Paint.Style.FILL);
        mTransparentPaint.setAntiAlias(true);
        initBitmap();
    }

    public OnSteerListener getListener() {
        return listener;
    }

    public void setListener(OnSteerListener listener) {
        this.listener = listener;
    }

    private void initBitmap() {
        top = BitmapFactory.decodeResource(getResources(), net.ajcloud.wansview.R.mipmap.wv_direction_up);
        bottom = BitmapFactory.decodeResource(getResources(), net.ajcloud.wansview.R.mipmap.wv_direction_downs);
        right = BitmapFactory.decodeResource(getResources(), net.ajcloud.wansview.R.mipmap.wv_direction_right);
        left = BitmapFactory.decodeResource(getResources(), net.ajcloud.wansview.R.mipmap.wv_direction_left);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        height = h;
        width = w;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        centerX = width / 2;
        centerY = height / 2;
        outImageRadius = height / 2;
        innerImageRadius = height / 3;
        outRadius = height / 2;
        innerRadius = height / 16 * 3;
        Shader mShader = null ;
        Paint p = new Paint();         
        canvas.drawColor(Color.TRANSPARENT);
        if (showSector) {
        	if (curCirlDir == Dir.Bottom){
        		//if (listener != null) listener.onBottomTouch();
        		mShader = new LinearGradient(0, 0, 0, height, 
                        new int[] { getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_paint2), getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_paint2),
                		getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_inner_circle)}, new float[]{0 , 0.5f, 1.0f}, Shader.TileMode.REPEAT);
        	} else if (curCirlDir == Dir.Left) {
        		//if (listener != null) listener.onLeftTouch();
        		mShader = new LinearGradient(width, 0, 0, 0, 
                        new int[] { getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_paint2),getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_paint2),
                		getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_inner_circle)}, new float[]{0 , 0.5f, 1.0f}, Shader.TileMode.REPEAT);
        	} else if (curCirlDir == Dir.Top) {
        		//if (listener != null) listener.onTopTouch();
        		mShader = new LinearGradient(0, height, 0, 0,
                        new int[] { getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_paint2), getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_paint2),
                		getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_inner_circle)}, new float[]{0 , 0.5f, 1.0f}, Shader.TileMode.REPEAT);
        	} else if (curCirlDir == Dir.Right){
        		//if (listener != null) listener.onRightTouch();
        		mShader = new LinearGradient(0, 0, width, 0,  
                        new int[] { getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_paint2), getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_paint2),
                		getResources().getColor(net.ajcloud.wansview.R.color.wv_direction_inner_circle)}, new float[]{0 , 0.5f, 1.0f}, Shader.TileMode.REPEAT);
        	} else {
        	}
        	p.setShader(mShader);
        	canvas.drawCircle(centerX, centerY, outImageRadius, p);
        } else {
            mBlackPaint.setStrokeWidth(0);
            canvas.drawCircle(centerX, centerY, outImageRadius, mGreenPaint);
        }
        canvas.drawBitmap(top,
                null,
                new Rect(centerX - top.getWidth() / 2,
                        centerY - outImageRadius + (outImageRadius - innerImageRadius) / 2 - top.getHeight() / 2,
                        centerX + top.getWidth() / 2,
                        centerY - outImageRadius + (outImageRadius - innerImageRadius) / 2 + top.getHeight() / 2),
                mBlackPaint );
        
        canvas.drawBitmap(bottom,
                null,
                new Rect(centerX - bottom.getWidth() / 2,
                        centerY + outImageRadius - (outImageRadius - innerImageRadius) / 2 - bottom.getHeight() / 2,
                        centerX + bottom.getWidth() / 2,
                        centerY + outImageRadius - (outImageRadius - innerImageRadius) / 2 + bottom.getHeight() / 2),
                mBlackPaint);
        
        canvas.drawBitmap(left,
                null,
                new Rect(centerX - outImageRadius + (outImageRadius - innerImageRadius) / 2 - left.getWidth() / 2,
                        centerY - left.getHeight() / 2,
                        centerX - outImageRadius + (outImageRadius - innerImageRadius) / 2 + left.getWidth() / 2,
                        centerY + left.getHeight() / 2),
                mBlackPaint);
        
        canvas.drawBitmap(right,
                null,
                new Rect(centerX + outImageRadius - (outImageRadius - innerImageRadius) / 2 - right.getWidth() / 2,
                        centerY - right.getHeight() / 2,
                        centerX + outImageRadius - (outImageRadius - innerImageRadius) / 2 + right.getWidth() / 2,
                        centerY + right.getHeight() / 2),
                mBlackPaint);
        
        mTransparentPaint.setStrokeWidth(0);
        //canvas.drawCircle(centerX, centerY, innerImageRadius, mTransparentPaint);
    }

    /**
     * @param x 点击横坐标
     * @param y 点击纵坐标
     * @return -1 不在圆圈内
     * 315上
     * 45右
     * 135下
     * 225左
     */
    private int pointInSector(float x, float y) {

        double r = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        if (r > outRadius || r < innerRadius)
            return -1;
        double k = (y - centerY) / (x - centerX);
        if (k >= -1 && k < 1) {
            if (x - centerX > 0) {
                return 315;//右
            } else {
                return 135;//左
            }
        }
        if (k > 1 || k < -1) {
            if (y - centerY > 0) {
                return 45;//下
            } else {
                return 225;//上
            }
        }
        return -1;
    }
    
    private Dir CirlDeDirection() {
    	Point point = new Point();
    	Rect rect = new Rect(mAutoBackView.getLeft(),mAutoBackView.getTop(),
    			mAutoBackView.getRight(), mAutoBackView.getBottom());
    	point.x = width * 7 / 8;
    	point.y = width / 2;
    	if (isInRect(rect, point)) {
    		return Dir.Right;
    	}
    	
    	point.x = width / 2;
    	point.y = height / 8;
    	if (isInRect(rect, point)) {
    		return Dir.Top;
    	}
    	
    	point.x = width / 8;
    	point.y = height /  2;
    	if (isInRect(rect, point)) {
    		return Dir.Left;
    	}
    	
    	point.x = width / 2;
    	point.y = height * 7 / 8;
    	if (isInRect(rect, point)) {
    		return Dir.Bottom;
    	}
    	
    	return Dir.CENTER;
    }
    
    private boolean isInRect(Rect rect, Point point) {
        return point.x >= rect.left && point.x <= rect.right &&
                point.y >= rect.top && point.y <= rect.bottom;
    }
    
    /**
     * 关于方向的枚举
     * 
     * @author Administrator
     * 
     */
    public enum Dir {
    	Left, Right, Top, Bottom, CENTER
    }

    private boolean canDrag() {
        if (null == listener || !listener.canDrag()) {
            return false;
        }
        return true;
    }

    public interface OnSteerListener {
        void onTopTouch();

        void onBottomTouch();

        void onRightTouch();

        void onLeftTouch();

        void onStop();

        void onTouchDown();
        void onTouchLeave();
        boolean canDrag();
    }
}
