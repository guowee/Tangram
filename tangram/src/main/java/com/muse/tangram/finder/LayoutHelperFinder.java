package com.muse.tangram.finder;

import com.muse.tangram.helper.LayoutHelper;

import java.util.List;

/**
 * Created by GuoWee on 2018/1/12.
 */

public abstract class LayoutHelperFinder implements Iterable<LayoutHelper> {

    /**
     * set layouts into the finder
     *
     * @param layouts
     */
    public abstract void setLayouts(List<LayoutHelper> layouts);

    /**
     * get layoutHelper at given position
     *
     * @return
     */
    public abstract LayoutHelper getLayoutHelper(int position);

    /**
     * get all layoutHelpers
     *
     * @return
     */
    public abstract List<LayoutHelper> getLayoutHelpers();

    /**
     * get iterator that in reverse order
     *
     * @return
     */
    public abstract Iterable<LayoutHelper> reverse();
}
