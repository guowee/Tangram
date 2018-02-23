package com.uowee.tangram.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.uowee.tangram.VirtualLayoutManager;
import com.uowee.tangram.helper.LayoutHelper;

import java.util.List;

/**
 * Created by GuoWee on 2018/1/13.
 */

public abstract class VirtualLayoutAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected VirtualLayoutManager mLayoutManager;

    public VirtualLayoutAdapter(VirtualLayoutManager manager) {
        this.mLayoutManager = manager;
    }

    /**
     * 设置整个页面所需要的一系列LayoutHelper
     */
    public void setLayoutHelpers(List<LayoutHelper> helpers) {
        mLayoutManager.setLayoutHelpers(helpers);
    }

    @NonNull
    public List<LayoutHelper> getLayoutHelpers() {
        return this.mLayoutManager.getLayoutHelpers();
    }


}
