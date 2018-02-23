package com.uowee.tangram.helper;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.uowee.tangram.OrientationHelperEx;
import com.uowee.tangram.VirtualLayoutManager.LayoutParams;
import com.uowee.tangram.LayoutChunkResult;
import com.uowee.tangram.LayoutManagerHelper;
import com.uowee.tangram.VirtualLayoutManager;

import static com.uowee.tangram.VirtualLayoutManager.VERTICAL;

/**
 * Created by GuoWee on 2018/1/12.
 */

public class LinearLayoutHelper extends BaseLayoutHelper {
    private static final String TAG = "LinearLayoutHelper";

    private static final boolean DEBUG = false;

    private int mDividerHeight = 0;

    public LinearLayoutHelper() {
        this(0);
    }

    public LinearLayoutHelper(int dividerHeight) {
        // empty range
        this(dividerHeight, 0);
    }


    public LinearLayoutHelper(int dividerHeight, int itemCount) {
        setItemCount(itemCount);
        setDividerHeight(dividerHeight);
    }


    public void setDividerHeight(int dividerHeight) {
        if (dividerHeight < 0) {
            dividerHeight = 0;
        }
        this.mDividerHeight = dividerHeight;
    }

    /**
     * In {@link LinearLayoutHelper}, each iteration only consume one item,
     * so it can let parent LayoutManager to decide whether the next item is in the range of this helper
     */
    @Override
    public void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state,
                            VirtualLayoutManager.LayoutStateWrapper layoutState, LayoutChunkResult result,
                            LayoutManagerHelper helper) {
        // reach the end of this layout
        if (isOutOfRange(layoutState.getCurrentPosition())) {
            return;
        }
        int currentPosition = layoutState.getCurrentPosition();

        // find corresponding layout container
        View view = nextView(recycler, layoutState, helper, result);
        if (view == null) {
            return;
        }
        final boolean isOverLapMargin = helper.isEnableMarginOverLap();

        VirtualLayoutManager.LayoutParams params = (VirtualLayoutManager.LayoutParams) view.getLayoutParams();
        final boolean layoutInVertical = helper.getOrientation() == VERTICAL;

        int startSpace = 0, endSpace = 0, gap = 0;
        boolean isLayoutEnd = layoutState.getLayoutDirection() == VirtualLayoutManager.LayoutStateWrapper.LAYOUT_END;
        boolean isStartLine = isLayoutEnd
                ? currentPosition == getRange().getLower().intValue()
                : currentPosition == getRange().getUpper().intValue();
        boolean isEndLine = isLayoutEnd
                ? currentPosition == getRange().getUpper().intValue()
                : currentPosition == getRange().getLower().intValue();

        if (isStartLine) {
            startSpace = computeStartSpace(helper, layoutInVertical, isLayoutEnd, isOverLapMargin);
        }

        if (isEndLine) {
            endSpace = computeEndSpace(helper, layoutInVertical, isLayoutEnd, isOverLapMargin);
        }

        if (!isStartLine) {
            if (!isOverLapMargin) {
                gap = mDividerHeight;
            } else {
                if (isLayoutEnd) {
                    int marginTop = params.topMargin;
                    View sibling = helper.findViewByPosition(currentPosition - 1);
                    int lastMarginBottom = sibling != null ? ((LayoutParams) sibling.getLayoutParams()).bottomMargin : 0;
                    if (lastMarginBottom >= 0 && marginTop >= 0) {
                        gap = Math.max(lastMarginBottom, marginTop);
                    } else {
                        gap = lastMarginBottom + marginTop;
                    }
                } else {
                    int marginBottom = params.bottomMargin;
                    View sibling = helper.findViewByPosition(currentPosition + 1);
                    int lastMarginTop = sibling != null ? ((LayoutParams) sibling.getLayoutParams()).topMargin : 0;
                    if (marginBottom >= 0 && lastMarginTop >= 0) {
                        gap = Math.max(marginBottom, lastMarginTop);
                    } else {
                        gap = marginBottom + lastMarginTop;
                    }
                }
            }
        }

        final int widthSize = helper.getContentWidth() - helper.getPaddingLeft() - helper
                .getPaddingRight() - getHorizontalMargin() - getHorizontalPadding();
        int widthSpec = helper.getChildMeasureSpec(widthSize, params.width, !layoutInVertical);
        int heightSpec;
        float viewAspectRatio = params.mAspectRatio;
        if (!Float.isNaN(viewAspectRatio) && viewAspectRatio > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec((int) (widthSize / viewAspectRatio + 0.5f),
                    View.MeasureSpec.EXACTLY);
        } else if (!Float.isNaN(mAspectRatio) && mAspectRatio > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec((int) (widthSize / mAspectRatio + 0.5),
                    View.MeasureSpec.EXACTLY);
        } else {
            heightSpec = helper.getChildMeasureSpec(
                    helper.getContentHeight() - helper.getPaddingTop() - helper.getPaddingBottom()
                            - getVerticalMargin() - getVerticalPadding(), params.height,
                    layoutInVertical);
        }

        if (!isOverLapMargin) {
            helper.measureChildWithMargins(view, widthSpec, heightSpec);
        } else {
            helper.measureChild(view, widthSpec, heightSpec);
        }

        OrientationHelperEx orientationHelper = helper.getMainOrientationHelper();
        result.mConsumed = orientationHelper.getDecoratedMeasurement(view) + startSpace + endSpace + gap;
        int left, top, right, bottom;
        if (helper.getOrientation() == VERTICAL) {
            // not support RTL now
            if (helper.isDoLayoutRTL()) {
                right = helper.getContentWidth() - helper.getPaddingRight() - mMarginRight - mPaddingRight;
                left = right - orientationHelper.getDecoratedMeasurementInOther(view);
            } else {
                left = helper.getPaddingLeft() + mMarginLeft + mPaddingLeft;
                right = left + orientationHelper.getDecoratedMeasurementInOther(view);
            }

            // whether this layout pass is layout to start or to end
            if (layoutState.getLayoutDirection() == VirtualLayoutManager.LayoutStateWrapper.LAYOUT_START) {
                // fill start, from bottom to top
                bottom = layoutState.getOffset() - startSpace - (isStartLine ? 0 : gap);
                top = bottom - orientationHelper.getDecoratedMeasurement(view);
            } else {
                // fill end, from top to bottom
                top = layoutState.getOffset() + startSpace + (isStartLine ? 0 : gap);
                bottom = top + orientationHelper.getDecoratedMeasurement(view);
            }
        } else {
            top = helper.getPaddingTop() + mMarginTop + mPaddingTop;
            bottom = top + orientationHelper.getDecoratedMeasurementInOther(view);

            if (layoutState.getLayoutDirection() == VirtualLayoutManager.LayoutStateWrapper.LAYOUT_START) {
                // fill left, from right to left
                right = layoutState.getOffset() - startSpace - (isStartLine ? 0 : gap);
                left = right - orientationHelper.getDecoratedMeasurement(view);
            } else {
                // fill right, from left to right
                left = layoutState.getOffset() + startSpace + (isStartLine ? 0 : gap);
                right = left + orientationHelper.getDecoratedMeasurement(view);
            }
        }
        // We calculate everything with View's bounding box (which includes decor and margins)
        // To calculate correct layout position, we subtract margins.
        layoutChildWithMargin(view, left, top, right, bottom, helper);

        if (DEBUG) {
            Log.d(TAG, "laid out child at position " + helper.getPosition(view) + ", with l:"
                    + (left + params.leftMargin) + ", t:" + (top + params.topMargin) + ", r:"
                    + (right - params.rightMargin) + ", b:" + (bottom - params.bottomMargin));
        }

        handleStateOnResult(result, view);
    }

    @Override
    public int computeAlignOffset(int offset, boolean isLayoutEnd, boolean useAnchor, LayoutManagerHelper helper) {
        final boolean layoutInVertical = helper.getOrientation() == VERTICAL;

        if (isLayoutEnd) {
            if (offset == getItemCount() - 1) {
                return layoutInVertical ? mMarginBottom + mPaddingBottom : mMarginRight + mPaddingRight;
            }
        } else {
            if (offset == 0) {
                return layoutInVertical ? -mMarginTop - mPaddingTop : -mMarginLeft - mPaddingLeft;
            }
        }

        return super.computeAlignOffset(offset, isLayoutEnd, useAnchor, helper);
    }

}