package com.xw.repo;

import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

public class BubbleAccessibilityDelegate extends AccessibilityDelegateCompat {

    private static final int TIMEOUT_SEND_ACCESSIBILITY_EVENT = 200;

    private AccessibilityEventSender mAccessibilityEventSender;

    private BubbleSeekBar bubbleSeekBar;

    BubbleAccessibilityDelegate(BubbleSeekBar seekBar) {
        bubbleSeekBar = seekBar;
    }

    private class AccessibilityEventSender implements Runnable {
        public void run() {
            sendAccessibilityEvent(bubbleSeekBar, AccessibilityEvent.TYPE_VIEW_SELECTED);
        }
    }

    @Override
    public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(host, event);
    }

    @Override
    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(host, event);
        event.setItemCount((int) (bubbleSeekBar.getMax() - bubbleSeekBar.getMin()));
        event.setCurrentItemIndex(bubbleSeekBar.getProgress());
    }

    @Override
    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        switch (action) {
            case AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD:
                if (bubbleSeekBar.getProgress() > bubbleSeekBar.getMin())
                    bubbleSeekBar.setProgress(bubbleSeekBar.getProgress() - 1);

                scheduleAccessibilityEventSender();
                return true;
            case AccessibilityNodeInfo.ACTION_SCROLL_FORWARD:
                if (bubbleSeekBar.getProgress() < bubbleSeekBar.getMax())
                    bubbleSeekBar.setProgress(bubbleSeekBar.getProgress() + 1);

                scheduleAccessibilityEventSender();
                return true;
        }

        return super.performAccessibilityAction(host, action, args);
    }

    private void scheduleAccessibilityEventSender() {
        if (mAccessibilityEventSender == null) {
            mAccessibilityEventSender = new AccessibilityEventSender();
        } else {
            bubbleSeekBar.removeCallbacks(mAccessibilityEventSender);
        }
        bubbleSeekBar.postDelayed(mAccessibilityEventSender, TIMEOUT_SEND_ACCESSIBILITY_EVENT);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        AccessibilityNodeInfoCompat.RangeInfoCompat rangeInfo = AccessibilityNodeInfoCompat.RangeInfoCompat.obtain(
                AccessibilityNodeInfoCompat.RangeInfoCompat.RANGE_TYPE_INT,
                bubbleSeekBar.getMin(), bubbleSeekBar.getMax(), bubbleSeekBar.getProgress()
        );
        info.setRangeInfo(rangeInfo);

        final int progress = bubbleSeekBar.getProgress();
        if (progress > bubbleSeekBar.getMin()) {
            info.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD);
        }
        if (progress < bubbleSeekBar.getMax()) {
            info.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD);
        }
        super.onInitializeAccessibilityNodeInfo(host, info);
    }
}
