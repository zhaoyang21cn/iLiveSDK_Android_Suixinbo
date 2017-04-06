package com.tencent.qcloud.suixinbo.avcontrollers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;

import com.tencent.av.opengl.GraphicRendererMgr;
import com.tencent.av.opengl.glrenderer.GLCanvas;
import com.tencent.av.opengl.texture.BasicTexture;
import com.tencent.av.opengl.texture.StringTexture;
import com.tencent.av.opengl.texture.YUVTexture;
import com.tencent.av.opengl.ui.GLRootView;
import com.tencent.av.opengl.ui.GLView;
import com.tencent.av.opengl.utils.Utils;
import com.tencent.av.sdk.AVView;
import com.tencent.av.utils.QLog;
import com.tencent.qcloud.suixinbo.utils.SxbLog;

/**
 * 视频界面类
 */
public class GLVideoView extends GLView {

    private static final String TAG = "GLVideoView";

    private static final long LOADING_ELAPSE = 80;

    public static final float MIN_SCALE = 0.75f;
    public static final float MAX_SCALE = 4.00f;

    private static final int NONE = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int TOP = 4;
    private static final int BOTTOM = 8;

    private YUVTexture mYuvTexture;
    private BasicTexture mLoadingTexture;
    private StringTexture mStringTexture;

    private int mX = 0;
    private int mY = 0;
    private int mWidth = 0;
    private int mHeight = 0;

    private int mRotation = 0;
    private boolean mIsPC = false;
    private boolean mMirror = false;

    private float mScale = 1.0f;
    private int mPivotX = 0;
    private int mPivotY = 0;
    // private boolean mZooming = false;

    private float mOffsetX = 0;
    private float mOffsetY = 0;
    private int mPosition = NONE;
    private boolean mDragging = false;

    private int mLoadingAngle = 0;
    private boolean mLoading = false;
    private long mLastLoadingTime = 0;

    private String mIdentifier = null;
    private int mVideoSrcType = 0;
    private boolean isFristFrame = false;
    private Context mContext;

    private boolean mNeedRenderVideo = true;
    private GraphicRendererMgr mGraphicRenderMgr = null;

    public GLVideoView(Context context, GraphicRendererMgr graphicRenderMgr) {
        mContext = context;
        mYuvTexture = new YUVTexture(mContext);
        mYuvTexture.setGLRenderListener(new YUVTexture.GLRenderListener() {
            @Override
            public void onRenderFrame() {
                invalidate();
            }

            @Override
            public void onRenderReset() {
                flush();
                invalidate();
            }

            @Override
            public void onRenderFlush() {
                flush();
                invalidate();
            }

            @Override
            public void onRenderInfoNotify(int width, int height, int angle) {
                if (QLog.isColorLevel()) {
                    QLog.d(TAG, QLog.CLR, "onRenderInfoNotify uin: " + mIdentifier + ", mVideoSrcType: " + mVideoSrcType + ", width: " + width + ", height: " + height + ", angle: " + angle);
                }

                if (isFristFrame == false) {
                    SxbLog.i(TAG, "PerformanceTest  end     " +  SxbLog.getTime());
                    isFristFrame = true;
                }
                mYuvTexture.setTextureSize(width, height);
                // refresh();
                invalidate();
            }
        });
        mGraphicRenderMgr = graphicRenderMgr;
    }

    @Override
    protected void onDetachFromRoot() {
        GLRootView view = getGLRootView();
        if (view != null)
            view.removeCallbacks(loadingRunnable);
        super.onDetachFromRoot();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (mStringTexture != null) {
            mStringTexture.recycle();
            mStringTexture = null;
        }
        if (mLoadingTexture != null) {
            mLoadingTexture.recycle();
            mLoadingTexture = null;
        }
        if (mYuvTexture != null) {
            mYuvTexture.recycle();
            mYuvTexture = null;
        }
        loadingRunnable = null;
    }

    int mGlVersion = -1;

    @Override
    protected void renderBackground(GLCanvas canvas) {
        if (mNeedRenderVideo == false) {
            mNeedRenderVideo = false;
        }
        int width = getWidth();
        int height = getHeight();
        Rect p = getPaddings();
        int x = p.left;
        int y = p.top;
        int w = width - p.left - p.right;
        int h = height - p.top - p.bottom;

        if (mGlVersion == -1) {
            mGlVersion = Utils.getGLVersion(mContext);
        }


        if (mGlVersion == 1) {
            if ((mYuvTexture == null || !hasVideo() || !mNeedRenderVideo) && mBackground != null) {
                mBackground.draw(canvas, x, y, w, h);
            }
            return;
        }

        canvas.fillRect(0, 0, width, height, mBackgroundColor);

        if (mBackgroundColor != Color.BLACK && mNeedRenderVideo) {
            canvas.fillRect(x, y, w, h, Color.BLACK);
        }
        if ((mYuvTexture == null || !hasVideo() || !mNeedRenderVideo) && mBackground != null) {
            mBackground.draw(canvas, x, y, w, h);
        }
    }

    @Override
    protected void render(GLCanvas canvas) {
        Rect p = getPaddings();
        // render background
        // if ((p.left != 0 || p.top != 0 || p.right != 0 || p.bottom != 0)
        // || TextUtils.isEmpty(mUin) || mYuvTexture == null || !hasVideo()) {
        renderBackground(canvas);
        // }
        // render video
        if (null != mIdentifier && mYuvTexture != null && mYuvTexture.canRender() && mNeedRenderVideo) {
            // disable loading
            enableLoading(false);

            //
            int uiWidth = getWidth();
            int uiHeight = getHeight();

			/*
             * if(Utils.getGLVersion(mContext) == 1){ int TextureWidth = Utils.nextPowerOf2(uiWidth); int TextureHeight =
			 * Utils.nextPowerOf2(uiHeight); uiWidth = (int)(uiWidth*uiWidth/(float)TextureWidth); uiHeight = (int)(uiHeight
			 * *uiHeight/(float)TextureHeight); }
			 */

            int width = uiWidth - p.left - p.right;
            int height = uiHeight - p.top - p.bottom;
            int angle = mYuvTexture.getImgAngle();
            int rotation = (angle + mRotation + 4) % 4;

            float x = p.left;
            float y = p.top;
            float w = width;
            float h = height;
            if (rotation % 2 != 0) {
                float tmp = x;
                x = y;
                y = tmp;
                tmp = w;
                w = h;
                h = tmp;
                tmp = width;
                width = height;
                height = (int) tmp;
            }

            float imgW = mYuvTexture.getImgWidth();
            float imgH = mYuvTexture.getImgHeight();
            float sRatio = imgW / imgH;

            float targetW = w;
            float targetH = h;
            float tRatio = targetW / targetH;

            if (hasBlackBorder(rotation)) {
                if (rotation % 2 == 0) {
                    w = targetW;
                    h = w / sRatio;
                    if (h > targetH) {
                        h = targetH;
                        w = h * sRatio;
                        x += (targetW - w) / 2;
                        // y = 0;
                    } else {
                        // x = 0;
                        y += (targetH - h) / 2;
                    }
                } else {
                    h = targetH;
                    w = h * sRatio;
                    if (w > targetW) {
                        w = targetW;
                        h = w / sRatio;
                        // x = 0;
                        y += (targetH - h) / 2;
                    } else {
                        x += (targetW - w) / 2;
                        // y = 0;
                    }
                }
                targetW = w;
                targetH = h;
                tRatio = targetW / targetH;
            }

            x = x * mScale + mPivotX * (1 - mScale);
            y = y * mScale + mPivotY * (1 - mScale);
            w = w * mScale;
            h = h * mScale;

            if (!mDragging && mPosition != NONE) {
                if ((mPosition & (LEFT | RIGHT)) == (LEFT | RIGHT)) {
                    mOffsetX = width / 2 - (x + w / 2);
                } else if ((mPosition & LEFT) == LEFT) {
                    mOffsetX = -x;
                } else if ((mPosition & RIGHT) == RIGHT) {
                    mOffsetX = width - w - x;
                }
                if ((mPosition & (TOP | BOTTOM)) == (TOP | BOTTOM)) {
                    mOffsetY = height / 2 - (y + h / 2);
                } else if ((mPosition & TOP) == TOP) {
                    mOffsetY = -y;
                } else if ((mPosition & BOTTOM) == BOTTOM) {
                    mOffsetY = height - h - y;
                }
                mPosition = NONE;
                if (QLog.isColorLevel()) {
                    QLog.d(TAG, QLog.CLR, "render uin: " + mIdentifier + ", mVideoSrcType: " + mVideoSrcType + ", width: " + width + ", height: " + height + ", mScale: " + mScale + ", mPivotX: " + mPivotX + ", mPivotY: " + mPivotY + ", x: " + x + ", y: " + y + ", w: " + w + ", h: " + h + ", mOffsetX: " + mOffsetX + ", mOffsetY: " + mOffsetY + ", mWidth: " + mWidth + ", mHeight: " + mHeight);
                }
            }
            x += mOffsetX;
            y += mOffsetY;

            mX = (int) x;
            mY = (int) y;
            mWidth = (int) w;
            mHeight = (int) h;

            if (sRatio > tRatio) {
                float newSourceH = imgH;
                float newSourceW = newSourceH * tRatio;
                if (Utils.getGLVersion(mContext) == 1) {
                    newSourceW = imgW * newSourceW / Utils.nextPowerOf2((int) imgW);
                }
                float offset = (imgW - newSourceW) / 2;
                mYuvTexture.setSourceSize((int) newSourceW, (int) newSourceH);
                mYuvTexture.setSourceLeft((int) offset);
                mYuvTexture.setSourceTop(0);
            } else {
                float newSourceW = imgW;
                float newSourceH = newSourceW / tRatio;
                if (Utils.getGLVersion(mContext) == 1) {
                    newSourceH = imgH * newSourceH / Utils.nextPowerOf2((int) imgH);
                }
                float offset = (imgH - newSourceH) / 2;
                mYuvTexture.setSourceSize((int) newSourceW, (int) newSourceH);
                mYuvTexture.setSourceLeft(0);
                mYuvTexture.setSourceTop((int) offset);
            }

            if (Utils.getGLVersion(mContext) == 1) {
                float newSourceW = imgW;
                float newSourceH = imgH;
                float offset = 0;
                mYuvTexture.setSourceSize((int) newSourceW, (int) newSourceH);
                mYuvTexture.setSourceLeft(0);
                mYuvTexture.setSourceTop((int) offset);
            }

            canvas.save(GLCanvas.SAVE_FLAG_MATRIX);
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            canvas.translate(centerX, centerY);
            if (mMirror) {
                if (mRotation % 2 == 0) {
                    canvas.scale(-1, 1, 1);
                } else {
                    canvas.scale(1, -1, 1);
                }
            }
            canvas.rotate(rotation * 90, 0, 0, 1);
            if (rotation % 2 != 0) {
                canvas.translate(-centerY, -centerX);
            } else {
                canvas.translate(-centerX, -centerY);
            }
            mYuvTexture.draw(canvas, mX, mY, mWidth, mHeight);
            canvas.restore();
        }
        // render loading
        if (mLoading && mLoadingTexture != null) {
            mLoadingAngle = mLoadingAngle % 360;
            int uiWidth = getWidth();
            int uiHeight = getHeight();
            int width = mLoadingTexture.getSourceWidth();
            int height = mLoadingTexture.getSourceHeight();
            if (width > uiWidth) {
                width = uiWidth;
            }
            if (height > uiHeight) {
                height = uiHeight;
            }
            canvas.save(GLCanvas.SAVE_FLAG_MATRIX);
            canvas.translate(uiWidth / 2, uiHeight / 2);
            canvas.rotate(mLoadingAngle, 0, 0, 1);
            canvas.translate(-width / 2, -height / 2);
            mLoadingTexture.draw(canvas, 0, 0, width, height);
            canvas.restore();
            long now = System.currentTimeMillis();
            if (now - mLastLoadingTime >= LOADING_ELAPSE) {
                mLastLoadingTime = now;
                mLoadingAngle += 8;
            }
        }
        // render text
        if (mStringTexture != null) {
            int uiWidth = getWidth();
            int uiHeight = getHeight();
            int width = mStringTexture.getSourceWidth();
            int height = mStringTexture.getSourceHeight();
            if (width > uiWidth) {
                width = uiWidth;
            }
            if (height > uiHeight) {
                height = uiHeight;
            }
            canvas.save(GLCanvas.SAVE_FLAG_MATRIX);
            canvas.translate(uiWidth / 2 - width / 2, uiHeight / 2 - height / 2);
            mStringTexture.draw(canvas, 0, 0, width, height);
            canvas.restore();
        }
    }

    @Override
    public void setRotation(int rotation) {
        rotation = rotation % 360;
        switch (rotation) {
            case 270: // 270 degree
                rotation = 1;
                break;
            case 180: // 180 degree
                rotation = 2;
                break;
            case 90: // 90 degree
                rotation = 3;
                break;
            default:// 0 degree
                rotation = 0;
                break;
        }
        if (mRotation != rotation) {
            mRotation = rotation;
            mScale = 1.0f;
            mPivotX = 0;
            mPivotY = 0;
            mOffsetX = 0;
            mOffsetY = 0;
            // refresh();
            if (getVisibility() == VISIBLE) {
                invalidate();
            }
        }
    }

    public boolean hasVideo() {
        if (mYuvTexture != null) {
            return mYuvTexture.canRender();
        }
        return false;
    }

    public int getRotation() {
        return mRotation;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public void emptyIdentifier() {
        mIdentifier = null;
    }

    public int getVideoSrcType() {
        return mVideoSrcType;
    }

    public void setRender(String identifier, int videoSrcType) {
        if (null == identifier || videoSrcType == AVView.VIDEO_SRC_TYPE_NONE) {
            mIdentifier = null;
            mVideoSrcType = 0;
            return;
        }
        mIdentifier = identifier;
        mVideoSrcType = videoSrcType;
        String key = mIdentifier + "_" + mVideoSrcType;// 一个uin可能有多路视频，用uin做key不够，这里用uin_srcType做key
        if (null == mYuvTexture) {
            SxbLog.e(TAG, "null == mYuvTexture");
        }
        mGraphicRenderMgr.setGlRender(key, mYuvTexture);

        mScale = 1.0f;
        mPivotX = 0;
        mPivotY = 0;
        mOffsetX = 0;
        mOffsetY = 0;
    }

    public void clearRender() {
        if (null != mIdentifier) {
            String key = mIdentifier + "_" + mVideoSrcType;// 一个uin可能有多路视频，用uin做key不够，这里用uin_srcType做key
            mGraphicRenderMgr.setGlRender(key, null);
        }
        mIdentifier = null;
        mVideoSrcType = 0;

        mScale = 1.0f;
        mPivotX = 0;
        mPivotY = 0;
        mOffsetX = 0;
        mOffsetY = 0;
    }

    public void setIsPC(boolean isPC) {
        if (QLog.isColorLevel()) {
            QLog.d(TAG, QLog.CLR, "setBlackBorder uin: " + mIdentifier + ", mVideoSrcType: " + mVideoSrcType + ", mIsPC: " + mIsPC + ", isPC: " + isPC);
        }
        if (mIsPC != isPC) {
            mIsPC = isPC;
            mScale = 1.0f;
            mPivotX = 0;
            mPivotY = 0;
            mOffsetX = 0;
            mOffsetY = 0;
            // refresh();
            invalidate();
        }
    }

    public boolean isPC() {
        return mIsPC;
    }

    public void setMirror(boolean mirror) {
        if (QLog.isColorLevel()) {
            QLog.d(TAG, QLog.CLR, "setMirror uin: " + mIdentifier + ", mVideoSrcType: " + mVideoSrcType + ", mMirror: " + mMirror + ", mirror: " + mirror);
        }
        if (mMirror != mirror) {
            mMirror = mirror;
            invalidate();
        }
    }

    public boolean isMirror() {
        return mMirror;
    }

    public float getScale() {
        return mScale;
    }

    public void setScale(float scale, int x, int y, boolean isEnd) {
        if (QLog.isColorLevel()) {
            QLog.d(TAG, QLog.CLR, "setScale uin: " + mIdentifier + ", mVideoSrcType: " + mVideoSrcType + ", scale: " + scale + ", x: " + x + ", y: " + y + ", isEnd: " + isEnd + ", mOffsetX: " + mOffsetX + ", mOffsetY: " + mOffsetY + ", mX: " + mX + ", mY: " + mY + ", mWidth: " + mWidth + ", mHeight: " + mHeight);
        }
        // mZooming = !isEnd;
        if (isEnd) {
            if (scale < 1.0f) {
                scale = 1.0f;
                mPosition = NONE;
                mPosition |= LEFT | RIGHT;
                mPosition |= TOP | BOTTOM;// 防止屏幕分享双击时offset没更新导致画面看不见了
            }
            if (scale > MAX_SCALE) {
                scale = MAX_SCALE;
                mPosition = NONE;
                mPosition |= LEFT;
                mPosition |= TOP;// 防止屏幕分享双击还原时offset没更新导致画面看不见了
            }
        } else {
            if (scale < MIN_SCALE) {
                scale = MIN_SCALE;
            } else if (scale > MAX_SCALE) {
                scale = MAX_SCALE;
            }
        }
        if (mRotation % 2 != 0) {
            int tmp = x;
            x = y;
            y = tmp;
        }
        mScale = scale;
        mPivotX = x;
        mPivotY = y;
        // refresh();
        invalidate();
    }

    public void setOffset(int offsetX, int offsetY, boolean isEnd) {
        if (QLog.isColorLevel()) {
            QLog.d(TAG, QLog.CLR, "setOffset uin: " + mIdentifier + ", mVideoSrcType: " + mVideoSrcType + ", offsetX: " + offsetX + ", offsetY: " + offsetY + ", isEnd: " + isEnd);
        }
        mDragging = !isEnd;
        if (isEnd) {
            Rect p = getPaddings();
            int uiWidth = getWidth();
            int uiHeight = getHeight();
            int width = uiWidth - p.left - p.right;
            int height = uiHeight - p.top - p.bottom;
            int angle = mYuvTexture.getImgAngle();
            int rotation = (angle + mRotation + 4) % 4;
            if (rotation % 2 != 0) {
                int tmp = width;
                width = height;
                height = tmp;
            }
            mPosition = NONE;
            if (mX >= 0 && mX + mWidth <= width) {
                if (mWidth <= width) {
                    mPosition |= LEFT | RIGHT;
                }
            } else if (mX >= 0) {
                if (mWidth <= width) {
                    mPosition |= LEFT | RIGHT;
                } else {
                    mPosition |= LEFT;
                }
            } else if (mX + mWidth <= width) {
                if (mWidth <= width) {
                    mPosition |= LEFT | RIGHT;
                } else {
                    mPosition |= RIGHT;
                }
            }
            if (mY >= 0 && mY + mHeight <= height) {
                if (mHeight <= height) {
                    mPosition |= TOP | BOTTOM;
                }
            } else if (mY >= 0) {
                if (mHeight <= height) {
                    mPosition |= TOP | BOTTOM;
                } else {
                    mPosition |= TOP;
                }
            } else if (mY + mHeight <= height) {
                if (mHeight <= height) {
                    mPosition |= TOP | BOTTOM;
                } else {
                    mPosition |= BOTTOM;
                }
            }
            if (QLog.isColorLevel()) {
                QLog.d(TAG, QLog.CLR, "setOffset uin: " + mIdentifier + ", mVideoSrcType: " + mVideoSrcType + ", mPosition: " + mPosition + ", width: " + width + ", height: " + height + ", mX: " + mX + ", mY: " + mY + ", mWidth: " + mWidth + ", mHeight: " + mHeight);
            }
        }
        int x = offsetX;
        int y = offsetY;
        if (mRotation % 2 != 0) {
            int tmp = x;
            x = y;
            y = tmp;
            if (mRotation == 1) {
                y = -y;
            } else {
                x = -x;
            }
        } else if (mRotation == 2) {
            x = -x;
            y = -y;
        }
        mOffsetX += x;
        mOffsetY += y;
        // refresh();
        invalidate();
    }

    public void setText(String text, float textSize, int color) {
        if (QLog.isColorLevel()) {
            QLog.d(TAG, QLog.CLR, "setText uin: " + mIdentifier + ", mVideoSrcType: " + mVideoSrcType + ", text: " + text + ", textSize: " + textSize + ", color: " + color);
        }
        if (mStringTexture != null) {
            mStringTexture.recycle();
            mStringTexture = null;
        }
        if (null != text) {
            mStringTexture = StringTexture.newInstance(text, textSize, color);
            invalidate();
        }
    }

    public void enableLoading(boolean loading) {
        if (mLoading != loading) {
            if (QLog.isColorLevel()) {
                QLog.d(TAG, QLog.CLR, "enableLoading uin: " + mIdentifier + ", mVideoSrcType: " + mVideoSrcType + ", loading: " + loading + ", mLoading: " + mLoading);
            }
            mLoading = loading;
            if (loading) {
                if (mLoadingTexture == null) {
                    // mLoadingTexture = new ResourceTexture(mContext, R.drawable.qav_video_loading);
                }
                GLRootView view = getGLRootView();
                if (view != null)
                    view.post(loadingRunnable);
            } else {
                GLRootView view = getGLRootView();
                if (view != null)
                    view.removeCallbacks(loadingRunnable);
            }
        }
    }

    public boolean isLoading() {
        return mLoading;
    }

    public void flush() {
        if (mYuvTexture != null) {
            mYuvTexture.flush(false);
        }
        if (null != mIdentifier) {
            String key = mIdentifier + "_" + mVideoSrcType;
            mGraphicRenderMgr.flushGlRender(key);
        }
    }

    public void setNeedRenderVideo(boolean needRender) {
        if (QLog.isColorLevel()) {
            QLog.d(TAG, QLog.CLR, "setNeedRenderVideo uin: " + mIdentifier + ", mVideoSrcType: " + mVideoSrcType + ", bRender: " + needRender + ", mNeedRenderVideo: " + mNeedRenderVideo);
        }
        mNeedRenderVideo = needRender;
        invalidate();
    }

    private boolean hasBlackBorder(int rotation) {
        if (null != mIdentifier && mIdentifier.equals("")) {
            return false;
        }
        if (mParent != null && getWidth() == mParent.getWidth() && getHeight() == mParent.getHeight()) {
            if (mIsPC) {
                return true;
            } else if (rotation % 2 == 0) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    private Runnable loadingRunnable = new Runnable() {
        @Override
        public void run() {
            GLRootView view = getGLRootView();
            if (view != null) {
                invalidate();
                view.postDelayed(loadingRunnable, LOADING_ELAPSE);
            }
        }
    };
}
