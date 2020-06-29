package com.example.mydiary.MVC.controllers.listeners;

import android.view.View;

public class OnClickRateLimitedDecoratedListener implements View.OnClickListener {

    private final static int CLICK_DELAY_DEFAULT = 300;
    private View.OnClickListener onClickListener;
    private int mClickDelay;

    public OnClickRateLimitedDecoratedListener(View.OnClickListener onClickListener) {
        this(onClickListener, CLICK_DELAY_DEFAULT);
    }

    public OnClickRateLimitedDecoratedListener(View.OnClickListener onClickListener, int delay) {
        this.onClickListener = onClickListener;
        mClickDelay = delay;
    }

    @Override
    public void onClick(final View view) {
        view.setClickable(false);
        onClickListener.onClick(view);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setClickable(true);
            }
        }, mClickDelay);
    }
}
