package com.android.launcher3.uninstall;

import android.animation.Animator;
import android.animation.ValueAnimator;

import com.android.launcher3.imp.ImpUninstallIconShowListener;
import com.android.launcher3.setting.MxSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * 卸载按钮显示动画工具
 */
public class UninstallIconAnimUtil {

    private List<ValueAnimator> mList;

    private boolean isStart = false;

    public UninstallIconAnimUtil() {
        this.mList = new ArrayList<>();
    }

    public boolean isStart() {
        if (!MxSettings.sShowUnInstallIcon) {
            cancel();
        }
        return isStart;
    }

    public void animateToIconIndicatorDraw(final ImpUninstallIconShowListener listener) {
        final ValueAnimator animator = ValueAnimator.ofFloat(MxSettings.sShowUnInstallIcon ? 0f : 1f, MxSettings.sShowUnInstallIcon ? 1f : 0f);
        mList.add(animator);
        animator.setDuration(600);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            public void onAnimationUpdate(ValueAnimator animation) {
                if (listener != null) {
                    final float percent = (Float) animation.getAnimatedValue();
                    listener.onUninstallIconChange(percent);
                }
            }
        });
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                isStart = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mList.remove(animator);
                isStart = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mList.remove(animator);
                isStart = false;
            }
        });

    }

    public void cancel() {
        if (mList != null && !mList.isEmpty()) {
            mList.clear();
        }
    }

}
