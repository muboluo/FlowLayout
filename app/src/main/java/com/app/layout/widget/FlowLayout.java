package com.app.layout.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

    private static final int mHorizontalSpacing = dp2px(10); // item 之间的 横向间隙
    private static final int mVerticalSpacing = dp2px(8); // item 之间的 纵向间隙

    private List<List<View>> childListInEachLineList = new ArrayList<>(); // 存储 每一行的child view list
    private List<Integer> lineHeightList = new ArrayList<>(); // 每一行的行高 list


    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 每次重绘都会走该方法，所以需要重置 行高、子viewlist
        childListInEachLineList.clear();
        lineHeightList.clear();
        // child line list
        List<View> childLineList = new ArrayList<>(); //每行 child list


        // get padding
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        //  根据计算得出的 view的高度和宽度，并不是最终结果。而是如果我们希望展示子view，
        //  需要这样的宽度和高度。
        int parentNeedWidth = 0;
        int parentNeedHeight = 0;

        // 自身的宽度和高度。
        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec);

        // get width used
        int widthUsedInLine = 0;
        // 当前行的最大的子view的高度，我们只需要该高度，作为行高。
        int currentLineHeight = 0;

        for (int i = 0; i < getChildCount(); ++i) {

            // 测量 每个子view  宽高
            View childView = getChildAt(i);

            final LayoutParams childLayoutParams = childView.getLayoutParams();

            final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight,
                    childLayoutParams.width);
            final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom,
                    childLayoutParams.height);

            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            final int childViewMeasuredWidth = childView.getMeasuredWidth();
            final int childViewMeasuredHeight = childView.getMeasuredHeight();

            // 判断是否需要换行，若需要换行，则将该行子view list 添加入 childListInEachLineList 中。并记录行高。
            if (selfWidth < childViewMeasuredWidth + widthUsedInLine + mHorizontalSpacing) {

                childListInEachLineList.add(childLineList);
                lineHeightList.add(currentLineHeight);

                // 得到当前 flowLayout 需要的最大宽度
                parentNeedWidth = Math.max(parentNeedWidth, widthUsedInLine + mHorizontalSpacing);
                // 得到当前 flowLayout 的总高度
                parentNeedHeight += currentLineHeight + mVerticalSpacing;

                // 重置高度和已使用的宽度。
                childLineList = new ArrayList<>();
                currentLineHeight = 0;
                widthUsedInLine = 0;

            }

            childLineList.add(childView);

            // 获取已经使用的行宽
            widthUsedInLine += childViewMeasuredWidth + mHorizontalSpacing;
            // 当前高度最大的子view，作为行高
            currentLineHeight = Math.max(currentLineHeight, childViewMeasuredHeight);

            // 如果是最后一个子view，将该view添加入 lineList 中，并记录该行的行高。
            if (i == getChildCount() - 1) {
                childListInEachLineList.add(childLineList);
                lineHeightList.add(currentLineHeight);

                parentNeedWidth = Math.max(parentNeedWidth, widthUsedInLine + mHorizontalSpacing);
                parentNeedHeight += currentLineHeight + mVerticalSpacing;

            }

        }

        // 测量  flow layout 的 宽高。
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int exactWidth = widthMode == MeasureSpec.EXACTLY ? selfWidth : parentNeedWidth;
        int exactHeight = heightMode == MeasureSpec.EXACTLY ? selfHeight : parentNeedHeight;

        setMeasuredDimension(exactWidth, exactHeight);

    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

        int startPosition = getPaddingLeft();
        int topPosition = getPaddingTop();

        for (int j = 0; j < childListInEachLineList.size(); ++j) {

            List<View> childLineList = childListInEachLineList.get(j);
            int lineHeight = lineHeightList.get(j);

            for (int k = 0; k < childLineList.size(); ++k) {

                //为每一个view 设置 layout
                View childView = childLineList.get(k);
                int width = childView.getMeasuredWidth();

                int left = startPosition;
                int top = topPosition;
                int right = startPosition + width;
                int bottom = topPosition + lineHeight;

                childView.layout(left, top, right, bottom);

                startPosition += width + mHorizontalSpacing;
            }

            startPosition = getPaddingLeft();
            topPosition += mVerticalSpacing + lineHeight;
        }

    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

}
