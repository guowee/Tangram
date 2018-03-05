package com.muse.tangram;

/**
 * Created by GuoWee on 2018/1/13.
 */

public class LayoutChunkResult {
    public int mConsumed;
    public boolean mFinished;
    public boolean mIgnoreConsumed;
    public boolean mFocusable;

    public void resetInternal() {
        mConsumed = 0;
        mFinished = false;
        mIgnoreConsumed = false;
        mFocusable = false;
    }
}
