package com.mbrite.patrol.app;

import android.app.ListFragment;
import android.graphics.drawable.ColorDrawable;
import android.widget.ListView;

/**
 * Parent class for shared methods.
 */
public class ParentFragment extends ListFragment {
    protected void setDivider() {
        ListView lv = getListView();
        lv.setDivider(new ColorDrawable(this.getResources().getColor(R.color.black)));
        lv.setDividerHeight(1);
    }
}
