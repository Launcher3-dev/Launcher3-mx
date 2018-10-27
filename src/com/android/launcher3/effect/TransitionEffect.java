package com.android.launcher3.effect;

import android.animation.TimeInterpolator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.PagedView;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.folder.Folder;

import static android.view.View.VISIBLE;

/**
 * 滑动特效
 */
public class TransitionEffect {

    public static final int TRANSITION_EFFECT_NONE = 0;
    public static final int TRANSITION_EFFECT_ZOOM_IN = 1;
    public static final int TRANSITION_EFFECT_ZOOM_OUT = 2;
    public static final int TRANSITION_EFFECT_ROTATE_UP = 3;
    public static final int TRANSITION_EFFECT_ROTATE_DOWN = 4;
    public static final int TRANSITION_EFFECT_CUBE_IN = 5;
    public static final int TRANSITION_EFFECT_CUBE_OUT = 6;
    public static final int TRANSITION_EFFECT_STACK = 7;
    public static final int TRANSITION_EFFECT_ACCORDION = 8;
    public static final int TRANSITION_EFFECT_FLIP = 9;
    public static final int TRANSITION_EFFECT_CYLINDER_IN = 10;
    public static final int TRANSITION_EFFECT_CYLINDER_OUT = 11;
    public static final int TRANSITION_EFFECT_CROSS_FADE = 12;
    public static final int TRANSITION_EFFECT_OVERVIEW = 13;
    public static final int TRANSITION_EFFECT_OVERVIEW_SCALE = 14;
    public static final int TRANSITION_EFFECT_PAGE = 15;
    public static final int TRANSITION_EFFECT_WINDMILL_UP = 16;
    public static final int TRANSITION_EFFECT_WINDMILL_DOWN = 17;


    private static final float TRANSITION_SCREEN_ROTATION = 35f;
    private static final float TRANSITION_SCREEN_WINDMILL = 90f;
    private static final float CAMERA_DISTANCE = 6500;
    private static final float TRANSITION_SCALE_FACTOR = 0.5f;


    // If true, modify alpha of neighboring pages as user scrolls left/right
    private boolean mFadeInAdjacentScreens = true;

    private Launcher mLauncher;
    private AccelerateDecelerateInterpolator mScaleInterpolator;
    private ZInterpolator mZInterpolator;
    private DecelerateInterpolator mLeftScreenAlphaInterpolator;
    private AccelerateInterpolator mAlphaInterpolator;
    private float mCameraDistance;
    private float mOverviewModeShrinkFactor;
    private Workspace mWorkspace;

    public TransitionEffect(Launcher launcher) {
        this.mLauncher = launcher;
        mCameraDistance = PagedView.mDensity * CAMERA_DISTANCE;
        mOverviewModeShrinkFactor = launcher.getResources().getInteger(R.integer.config_workspaceOverviewShrinkPercentage) / 100f;
    }

    public void setWorkspace(Workspace workspace) {
        this.mWorkspace = workspace;
    }

    public void screenScrollByTransitionEffect(int screenScroll, int screenEffectNum) {

        switch (screenEffectNum) {
            case TRANSITION_EFFECT_NONE:// 0
                screenScrollByTransitionEffectStandard(screenScroll);
                break;
            case TRANSITION_EFFECT_ZOOM_IN:// 1
                screenScrollByTransitionEffectZoom(true, screenScroll);
                break;
            case TRANSITION_EFFECT_ZOOM_OUT:// 2
                screenScrollByTransitionEffectZoom(false, screenScroll);
                break;
            case TRANSITION_EFFECT_ROTATE_UP:// 3
                screenScrollByTransitionEffectRotate(true, screenScroll);
                break;
            case TRANSITION_EFFECT_ROTATE_DOWN:// 4
                screenScrollByTransitionEffectRotate(false, screenScroll);
                break;
            case TRANSITION_EFFECT_CUBE_IN:// 5
                screenScrollByTransitionEffectCube(true, screenScroll);
                break;
            case TRANSITION_EFFECT_CUBE_OUT:// 6
                screenScrollByTransitionEffectCube(false, screenScroll);
                break;
            case TRANSITION_EFFECT_STACK:// 7
                screenScrollByTransitionEffectStack(screenScroll);
                break;
            case TRANSITION_EFFECT_ACCORDION:// 8
                screenScrollByTransitionEffectAccordion(screenScroll);
                break;
            case TRANSITION_EFFECT_FLIP:// 9----有问题
                screenScrollByTransitionEffectFlip(screenScroll);
                break;
            case TRANSITION_EFFECT_CYLINDER_IN:// 10
                screenScrollByTransitionEffectCylinder(true, screenScroll);
                break;
            case TRANSITION_EFFECT_CYLINDER_OUT:// 11
                screenScrollByTransitionEffectCylinder(false, screenScroll);
                break;
            case TRANSITION_EFFECT_CROSS_FADE:// 12
                screenScrollByTransitionEffectCrossFade(screenScroll);
                break;
            case TRANSITION_EFFECT_OVERVIEW:// 13----效果不好
                screenScrollByTransitionEffectCrossOverview(screenScroll);
                break;
            case TRANSITION_EFFECT_OVERVIEW_SCALE:// 14
                screenScrollByTransitionEffectOverviewScale(screenScroll);
                break;
            case TRANSITION_EFFECT_PAGE:// 15
                screenScrollByTransitionEffectPage(screenScroll);
                break;
            case TRANSITION_EFFECT_WINDMILL_UP:// 16
                screenScrollByTransitionEffectWindMill(true, screenScroll);
                break;
            case TRANSITION_EFFECT_WINDMILL_DOWN:// 17
                screenScrollByTransitionEffectWindMill(false, screenScroll);
                break;
            default:
                screenScrollByTransitionEffectStandard(screenScroll);
                break;
        }
    }

    //矫正旋转效果的角度问题
    public void clearRotation() {
        final Workspace workspace = mLauncher.getWorkspace();
        int N = workspace.getChildCount();
        for (int i = 0; i < N; i++) {
            CellLayout v = (CellLayout) workspace.getPageAt(i);
            if (v != null) {
                float rotation = v.getRotation();
                if (0 != rotation) {
                    v.setRotation(0);
                }
                v.setPivotX(v.getMeasuredWidth() * 0.5f);
                v.setPivotY(v.getMeasuredHeight() * 0.5f);
            }
        }
    }

    public void clearTranslationX() {
        final Workspace workspace = mLauncher.getWorkspace();
        int N = workspace.getChildCount();
        for (int i = 0; i < N; i++) {
            CellLayout v = (CellLayout) workspace.getPageAt(i);
            if (v != null) {
                v.setTranslationX(0.f);
                v.setPivotX(v.getMeasuredWidth() * 0.5f);
            }
        }
    }

    public void clearTranslation() {
        final Workspace workspace = mLauncher.getWorkspace();
        int N = workspace.getChildCount();
        for (int i = 0; i < N; i++) {
            CellLayout v = (CellLayout) workspace.getPageAt(i);
            if (v != null) {
                v.setTranslationX(0.f);
                v.setTranslationY(0.f);
                v.setPivotX(v.getMeasuredWidth() * 0.5f);
                v.setPivotY(v.getMeasuredHeight() * 0.5f);
            }
        }
    }

    public void clearTransitionEffect() {
        final Workspace workspace = mLauncher.getWorkspace();
        final int N = workspace.getChildCount();
        float scale = mLauncher.getStateManager().getState() == LauncherState.NORMAL ? 1f : mOverviewModeShrinkFactor;
        for (int i = 0; i < N; i++) {
            View v = workspace.getPageAt(i);
            if (v != null) {
                v.setPivotX(v.getMeasuredWidth() * 0.5f);
                v.setPivotY(v.getMeasuredHeight() * 0.5f);
                v.setRotation(0);
                v.setRotationX(0);
                v.setRotationY(0);
                v.setScaleX(scale);
                v.setScaleY(scale);
                v.setTranslationX(0f);
                v.setTranslationY(0f);
                v.setVisibility(View.VISIBLE);
                v.setAlpha(1.0f);
                ((CellLayout) v).getShortcutsAndWidgets().setAlpha(1.0f);
            }
        }
    }

    private float resetValueByMode(float value) {
        if (mLauncher.getStateManager().getState() != LauncherState.NORMAL) {
            value *= mOverviewModeShrinkFactor;
        }
        return value;
    }

    private void screenScrollByTransitionEffectStandard(int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            if (mFadeInAdjacentScreens) {
                float alpha = 1 - Math.abs(scrollProgress);
                ((CellLayout) v).getShortcutsAndWidgets().setAlpha(alpha);
            }
        }
    }

    private void screenScrollByTransitionEffectZoom(boolean in, int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            float scale = 1.0f + (in ? -0.8f : 0.4f) * Math.abs(scrollProgress);
            scale = resetValueByMode(scale);
            // Extra translation to account for the increase in size
            if (!in) {
                float translationX = v.getMeasuredWidth() * 0.2f * (-scrollProgress);
                v.setTranslationX(translationX);
            }

            v.setScaleX(scale);
            v.setScaleY(scale);
        }
    }

    private void screenScrollByTransitionEffectRotate(boolean up, int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            float rotation =
                    (up ? TRANSITION_SCREEN_ROTATION : -TRANSITION_SCREEN_ROTATION) * scrollProgress;

            float translationX = v.getMeasuredWidth() * scrollProgress;

            float rotatePoint =
                    (v.getMeasuredWidth() * 0.5f) /
                            (float) Math.tan(Math.toRadians((double) (TRANSITION_SCREEN_ROTATION * 0.5f)));

            v.setPivotX(v.getMeasuredWidth() * 0.5f);
            if (up) {
                v.setPivotY(-rotatePoint);
            } else {
                v.setPivotY(v.getMeasuredHeight() + rotatePoint);
            }
            v.setRotation(rotation);
            v.setTranslationX(translationX);
        }
    }

    private void screenScrollByTransitionEffectCube(boolean in, int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            float rotation = (in ? 90.0f : -90.0f) * scrollProgress;
            v.setCameraDistance(mCameraDistance);
            v.setPivotX(scrollProgress < 0 ? 0 : v.getMeasuredWidth());
            v.setPivotY(v.getMeasuredHeight() * 0.5f);
            v.setRotationY(rotation);
            float offset = v.getMeasuredWidth() * (1.f - mOverviewModeShrinkFactor) / 2.0f;
            v.setTranslationX(isLauncherNormal() ? 0 : (scrollProgress < 0 ? offset : -offset));
        }
    }

    private void screenScrollByTransitionEffectStack(int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            if (mZInterpolator == null) {
                mZInterpolator = new ZInterpolator(0.5f);
            }
            if (mLeftScreenAlphaInterpolator == null) {
                mLeftScreenAlphaInterpolator = new DecelerateInterpolator(4);
            }
            if (mAlphaInterpolator == null) {
                mAlphaInterpolator = new AccelerateInterpolator(0.9f);
            }
            final boolean isRtl = Utilities.isRtl(v.getResources());
            float interpolatedProgress;
            float translationX;
            float maxScrollProgress = Math.max(0, scrollProgress);
            float minScrollProgress = Math.min(0, scrollProgress);

            if (isRtl) {
                translationX = maxScrollProgress * v.getMeasuredWidth();
                interpolatedProgress = mZInterpolator.getInterpolation(Math.abs(maxScrollProgress));
            } else {
                translationX = minScrollProgress * v.getMeasuredWidth();
                interpolatedProgress = mZInterpolator.getInterpolation(Math.abs(minScrollProgress));
            }
            float scale = (1 - interpolatedProgress) +
                    interpolatedProgress * TRANSITION_SCALE_FACTOR;

            // 缩小状态
            scale = resetValueByMode(scale);

            float alpha;
            if (isRtl && (scrollProgress > 0)) {
                alpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(maxScrollProgress));
            } else if (!isRtl && (scrollProgress < 0)) {
                alpha = mAlphaInterpolator.getInterpolation(1 - Math.abs(scrollProgress));
            } else {
                //  On large screens we need to fade the effect_page as it nears its leftmost position
                alpha = mLeftScreenAlphaInterpolator.getInterpolation(1 - scrollProgress);
            }

            v.setTranslationX(translationX);
            v.setScaleX(scale);
            v.setScaleY(scale);
            if (v instanceof CellLayout) {
                ((CellLayout) v).getShortcutsAndWidgets().setAlpha(alpha);
            } else {
                v.setAlpha(alpha);
            }

            // If the view has 0 alpha, we set it to be invisible so as to prevent
            // it from accepting touches
            if (alpha == 0) {
                v.setVisibility(View.INVISIBLE);
            } else if (v.getVisibility() != VISIBLE) {
                v.setVisibility(VISIBLE);
            }
        }

    }

    private void screenScrollByTransitionEffectAccordion(int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            float scale = 1.0f - Math.abs(scrollProgress);
            v.setScaleX(scale);
            v.setPivotX(scrollProgress < 0 ? 0 : v.getMeasuredWidth());
            v.setPivotY(v.getMeasuredHeight() / 2f);
        }
    }

    private void screenScrollByTransitionEffectFlip(int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
//            float rotation = -180.0f * scrollProgress;
//            if (scrollProgress != 0) {
//                mWorkspace.mLauncher.changeBackgroundAlpha(1.0f);
//            }
//
//            if (scrollProgress >= -0.5f && scrollProgress <= 0.5f) {
//                v.setCameraDistance(mCameraDistance);
//                v.setTranslationX(v.getMeasuredWidth() * scrollProgress);
//                v.setPivotX(v.getMeasuredWidth() * 0.5f);
//                v.setRotationY(rotation);
//                if (v.getVisibility() != VISIBLE) {
//                    v.setVisibility(VISIBLE);
//                }
//            } else {
//                v.setVisibility(INVISIBLE);
//            }

            float rotation = -180.0f * scrollProgress;
            if (scrollProgress == 0) {
                //Flip特效反向切换桌面时出现蒙版
                Folder openFolder = mLauncher.getWorkspace().getOpenFolder();
                if (openFolder == null) {
                    mLauncher.changeBackgroundAlpha(1.0f);
                }
            }
            if (scrollProgress >= -0.5f && scrollProgress <= 0.5f) {
                v.setCameraDistance(PagedView.mDensity * mCameraDistance);
                v.setTranslationX(v.getMeasuredWidth() * scrollProgress);
                v.setPivotX(v.getMeasuredWidth() * 0.5f);
                v.setRotationY(rotation);
                if (v.getVisibility() != VISIBLE) {
                    v.setVisibility(VISIBLE);
                }
            } else {
                v.setVisibility(View.INVISIBLE);
            }


//            float rotation = -180.0f * Math.max(-1f, Math.min(1f, scrollProgress));
//
//            v.setCameraDistance(mWorkspace.mDensity * Workspace.CAMERA_DISTANCE);
//            v.setPivotX(v.getMeasuredWidth() * 0.5f);
//            v.setPivotY(v.getMeasuredHeight() * 0.5f);
//            v.setRotationY(rotation);
//
//            if (scrollProgress >= -0.5f && scrollProgress <= 0.5f) {
//                v.setTranslationX(v.getMeasuredWidth() * scrollProgress);
//            } else {
//                v.setTranslationX(0f);
//            }
        }
    }

    /**
     * 柱面切换效果，不是立方切换效果
     *
     * @param in             向内或者向外
     * @param screenScroll 滑动进度
     */
    private void screenScrollByTransitionEffectCylinder(boolean in, int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            float rotation = (in ? TRANSITION_SCREEN_ROTATION : -TRANSITION_SCREEN_ROTATION) * scrollProgress;
            v.setCameraDistance(v.getMeasuredWidth() * 4);
            v.setPivotX((scrollProgress + 1) * v.getMeasuredWidth() * 0.5f);
            v.setPivotY(v.getMeasuredHeight() * 0.5f);
            v.setRotationY(rotation);
        }
    }

    private void screenScrollByTransitionEffectCrossFade(int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            float alpha = 1 - Math.abs(scrollProgress);
            v.setPivotX(v.getMeasuredWidth() * 0.5f);
            v.setPivotY(v.getMeasuredHeight() * 0.5f);
            v.setAlpha(alpha);
        }
    }

    private void screenScrollByTransitionEffectCrossOverview(int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            if (mScaleInterpolator == null) {
                mScaleInterpolator = new AccelerateDecelerateInterpolator();
            }
            float scale = 1.0f - 0.1f *
                    mScaleInterpolator.getInterpolation(Math.min(0.3f, Math.abs(scrollProgress)) / 0.3f);
            v.setPivotX(scrollProgress < 0 ? 0 : v.getMeasuredWidth());
            v.setPivotY(v.getMeasuredHeight() * 0.5f);
            v.setScaleX(scale);
            v.setScaleY(scale);
            v.setAlpha(scale);
        }
    }

    private void screenScrollByTransitionEffectOverviewScale(int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            float scale = (scrollProgress >= 0 ? 1 - scrollProgress : 1 + scrollProgress);
            scale = resetValueByMode(scale);
            v.setCameraDistance(mCameraDistance);
            v.setPivotX(v.getMeasuredWidth() * 0.5f);
            v.setPivotY(v.getMeasuredHeight() * 0.5f);

            v.setScaleX(scale);
            v.setScaleY(scale);
            v.setTranslationX(v.getMeasuredWidth() * scrollProgress);

            if (scale == 0.0f) {
                v.setVisibility(View.INVISIBLE);
            } else if (v.getVisibility() == View.INVISIBLE) {
                v.setVisibility(VISIBLE);
            }
            if (mFadeInAdjacentScreens) {
                setCellLayoutFadeAdjacent(v,scrollProgress);
            }
        }
    }

    // 翻页
    private void screenScrollByTransitionEffectPage(int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            float translationX = v.getMeasuredWidth() * scrollProgress;
            translationX = resetValueByMode(translationX);
            float rotation = scrollProgress > 0 ? scrollProgress * (-120.f) : scrollProgress * 120.0f;
            v.setCameraDistance(mCameraDistance);

            float offset = isLauncherNormal() ? 0 : v.getMeasuredWidth() * (1.f - mOverviewModeShrinkFactor) / 2.f;

            if (scrollProgress > 0 && scrollProgress < 1) {
                v.setPivotX(0);
                v.setTranslationX(offset);
            } else if (scrollProgress > -1 && scrollProgress < 0) {
                v.setPivotX(v.getMeasuredWidth());
                v.setTranslationX(-offset);
            }
            v.setPivotY(v.getMeasuredHeight() >> 1);

            v.setRotationY(rotation);
            v.setTranslationX(scrollProgress > 0 ? offset + translationX : (-offset) + translationX);

            if (scrollProgress <= -1.0 || scrollProgress >= 1.0) {
                v.setVisibility(View.INVISIBLE);
                v.setTranslationX(0);
            } else {
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    // 大风车
    private void screenScrollByTransitionEffectWindMill(boolean up, int screenScroll) {
        final int N = mWorkspace.getChildCount();
        for (int i = 0; i < N; i++) {
            View v = mWorkspace.getPageAt(i);
            float scrollProgress = mWorkspace.getScrollProgress(screenScroll, v, i);
            float rotation =
                    (up ? TRANSITION_SCREEN_WINDMILL : -TRANSITION_SCREEN_WINDMILL) * scrollProgress;

            float translationX = v.getMeasuredWidth() * scrollProgress;
            translationX = resetValueByMode(translationX);

            float rotatePoint =
                    (v.getMeasuredWidth() * 0.5f) /
                            (float) Math.tan(Math.toRadians((double) (TRANSITION_SCREEN_WINDMILL * 0.5f)));

            v.setPivotX(v.getMeasuredWidth() * 0.5f);
            if (up) {
                v.setPivotY(-rotatePoint);
            } else {
                v.setPivotY(v.getMeasuredHeight() + rotatePoint);
                // 由于Y方向旋转轴偏移，需要矫正Y方向上的位置
                v.setTranslationY(isLauncherNormal() ? 0 :
                        -(rotatePoint + v.getMeasuredHeight() / 2.f) * (1.f - mOverviewModeShrinkFactor)
                                + mLauncher.getWorkspace().getOverviewModeTranslationY());
            }
            v.setRotation(rotation);
            v.setTranslationX(translationX);
            if (scrollProgress == 1.0f || scrollProgress == -1.0f) {
                v.setTranslationX(0);
                v.setRotation(0);
                v.setPivotX(v.getMeasuredHeight() * 0.5f);
                v.setPivotY(v.getMeasuredHeight() * 0.5f);
            }
        }
    }

    private void setCellLayoutFadeAdjacent(View child, float scrollProgress) {
        float alpha = 1 - Math.abs(scrollProgress);
        ((CellLayout) child).getShortcutsAndWidgets().setAlpha(alpha);
    }

    private boolean isLauncherNormal() {
        return mLauncher.getStateManager().getState() == LauncherState.NORMAL;
    }
}

class ZInterpolator implements TimeInterpolator {
    private float focalLength;

    ZInterpolator(float foc) {
        focalLength = foc;
    }

    public float getInterpolation(float input) {
        return (1.0f - focalLength / (focalLength + input)) /
                (1.0f - focalLength / (focalLength + 1.0f));
    }
}
