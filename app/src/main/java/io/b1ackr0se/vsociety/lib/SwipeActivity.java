package io.b1ackr0se.vsociety.lib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;

public class SwipeActivity extends AppCompatActivity implements SwipeBackActivityBase {

    private SwipeHelper swipeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swipeHelper = new SwipeHelper(this);
        swipeHelper.onActivityCreate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        swipeHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && swipeHelper != null)
            return swipeHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return swipeHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    protected void setViratorEnable(boolean enable){
        swipeHelper.setVibratorEnabled(enable);
    }

    public void setSwipeListener(SwipeBackLayout.SwipeListener l){
        swipeHelper.setSwipeListener(l);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
