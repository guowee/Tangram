package com.uowee.tangram.finder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.uowee.tangram.helper.LayoutHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by GuoWee on 2018/1/13.
 */

public class RangeLayoutHelperFinder extends LayoutHelperFinder {

    @NonNull
    private List<LayoutHelperItem> mLayoutHelperItems = new LinkedList<>();

    @NonNull
    private List<LayoutHelper> mLayoutHelpers = new LinkedList<>();

    @NonNull
    private Comparator<LayoutHelperItem> mLayoutHelperItemComparator = new Comparator<LayoutHelperItem>() {
        @Override
        public int compare(LayoutHelperItem lhs, LayoutHelperItem rhs) {
            return lhs.getStartPosition() - rhs.getStartPosition();
        }
    };

    @Override
    public Iterator<LayoutHelper> iterator() {
        return Collections.unmodifiableList(mLayoutHelpers).iterator();
    }

    @Override
    public Iterable<LayoutHelper> reverse() {
        final ListIterator<LayoutHelper> i = mLayoutHelpers.listIterator(mLayoutHelpers.size());
        return new Iterable<LayoutHelper>() {
            @Override
            public Iterator<LayoutHelper> iterator() {
                return new Iterator<LayoutHelper>() {
                    public boolean hasNext() {
                        return i.hasPrevious();
                    }

                    public LayoutHelper next() {
                        return i.previous();
                    }

                    public void remove() {
                        i.remove();
                    }
                };
            }
        };
    }

    /**
     * @param layouts layoutHelpers that handled
     */
    @Override
    public void setLayouts(@Nullable List<LayoutHelper> layouts) {
        mLayoutHelpers.clear();
        mLayoutHelperItems.clear();
        if (layouts != null) {
            for (LayoutHelper helper : layouts) {
                mLayoutHelpers.add(helper);
                mLayoutHelperItems.add(new LayoutHelperItem(helper));
            }

            Collections.sort(mLayoutHelperItems, mLayoutHelperItemComparator);
        }
    }

    @NonNull
    @Override
    public List<LayoutHelper> getLayoutHelpers() {
        return Collections.unmodifiableList(mLayoutHelpers);
    }

    @Nullable
    @Override
    public LayoutHelper getLayoutHelper(int position) {
        final int count = mLayoutHelperItems.size();
        if (count == 0) {
            return null;
        }

        int s = 0, e = count - 1, m;
        LayoutHelperItem rs = null;

        // binary search range
        while (s <= e) {
            m = (s + e) / 2;
            rs = mLayoutHelperItems.get(m);
            if (rs.getStartPosition() > position) {
                e = m - 1;
            } else if (rs.getEndPosition() < position) {
                s = m + 1;
            } else if (rs.getStartPosition() <= position && rs.getEndPosition() >= position)
                break;

            rs = null;
        }

        return rs == null ? null : rs.layoutHelper;
    }


    static class LayoutHelperItem {

        LayoutHelperItem(LayoutHelper helper) {
            this.layoutHelper = helper;
        }

        LayoutHelper layoutHelper;

        public int getStartPosition() {
            return layoutHelper.getRange().getLower();
        }

        public int getEndPosition() {
            return layoutHelper.getRange().getUpper();
        }

    }
}
