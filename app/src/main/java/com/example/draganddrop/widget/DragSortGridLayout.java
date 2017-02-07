package com.example.draganddrop.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.draganddrop.R;

import java.util.ArrayList;
import java.util.List;
/**
 *
 *  作用：其实就是一个业务逻辑类 ：点击、长按、拖拽
 *      1、加载布局
 *      2、给外界提供方法，把要显示和隐藏的文本传进来  list<String>
 *      3、对外提供方法，把更新之后的要显示的文本告诉外界
 */
public class DragSortGridLayout extends LinearLayout {

    private GridLayout mShowGridLayout;
    private GridLayout mHideGridLayout;
    private int mWidthPixels;
    private int mDpToPx5;

    public DragSortGridLayout(Context context) {
        this(context, null);
    }

    public DragSortGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragSortGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //加载布局
        View.inflate(context, R.layout.dsgl_layout,this);
        //找到要显示和隐藏的gridlayout控件
        mShowGridLayout = (GridLayout) findViewById(R.id.showGridLayout);
        mHideGridLayout = (GridLayout) findViewById(R.id.hideGridLayout);
        //获取屏幕的宽度
        mWidthPixels = getResources().getDisplayMetrics().widthPixels;
        //间距dp===>px
        mDpToPx5 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        //让mShowGridLayout接收拖拽监听
        mShowGridLayout.setOnDragListener(mOnDragListener);
    }
    // 3、对外提供方法，把更新之后的要显示的文本告诉外界
    public List<String> getUpdatedShowItems(){
        List<String> showItems=new ArrayList<String>();
        int childCount = mShowGridLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            //找到每一个孩子
            TextView child = (TextView) mShowGridLayout.getChildAt(i);
            showItems.add(child.getText().toString());
        }
        return showItems;
    }
    //2、给外界提供方法，把要显示和隐藏的文本传进来  list<String>
    public void setShowAndHideItems(List<String> showItems,List<String> hideItems){
        //添加要显示的条目
        for (String showItem : showItems) {
            addItem(showItem,mShowGridLayout);
        }
        //添加要隐藏的条目
        for (String hideItes : hideItems) {
            addItem(hideItes,mHideGridLayout);
        }
    }
    //点击添加View
    public void addItem(String text,GridLayout gridLayout) {
        //创建一个TextView
        TextView textView=createView(text);
        //添加到mGridLayout里面
//        mGridLayout.addView(textView);
        gridLayout.addView(textView);
        //条目单击事件
        textView.setOnClickListener(mOnClickListener);
        //3.1、给TextView设置长按监听
        textView.setOnLongClickListener(mOnLongClickListener);
    }
    //条目单击事件
    private OnClickListener mOnClickListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getParent()==mShowGridLayout){
               if (mShowGridLayout.getChildCount()>1){
                   //单击的是上面的textview
                   mShowGridLayout.removeView(v);
                   addItem(((TextView) v).getText().toString(),mHideGridLayout);
               }
            }else {
                mHideGridLayout.removeView(v);
                addItem(((TextView) v).getText().toString(), mShowGridLayout);
            }
        }
    };
    private View mDragView;
    private OnLongClickListener mOnLongClickListener=new OnLongClickListener() {
        /**
         *
         * @param v  就是textview
         * @return  返回值决定了v的单击事件是否被执行；true表示不会
         */
        @Override
        public boolean onLongClick(View v) {
            if (v.getParent()==mShowGridLayout){
                mDragView=v;
//            Log.d("MainActivity", "onLongClick");
                // 3.2、在长按监听里面发送一个拖拽事件 手指在阴影的中间，阴影类似于照相
                DragShadowBuilder dragShadowBuilder=new DragShadowBuilder(v);
                v.startDrag(null, dragShadowBuilder,null,0);
                //让原来的textview变成虚线边框
                v.setBackgroundResource(R.drawable.dsgl_textview_disable_shape);
            }
            return true;
        }
    };
    private OnDragListener mOnDragListener=new OnDragListener() {
        /**
         *
         * @param v  DridLayout
         * @param event  拖拽事件
         * @return   表示是否要消费这个拖拽事件  true 表示持续接收后面的事件
         */
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.i("test", "start");//开始
                    //初始化每一个条目的位置，并保存起来
                    initRects();
                    break;
                case DragEvent.ACTION_DRAG_ENTERED://进入
                    Log.i("test","enter");
                    break;
                case DragEvent.ACTION_DRAG_LOCATION://移动
                    //查找要移动的新的位置
                    int index=findIndex((int)event.getX(),(int)event.getY());
                    if (index>-1){
                        Log.i("test","location");
                        //找到了新的位置
                        mShowGridLayout.removeView(mDragView);
                        mShowGridLayout.addView(mDragView,index);
                    }

                    break;
                case DragEvent.ACTION_DRAG_EXITED://超出边界
                    Log.i("test","exit");
                    break;
                case DragEvent.ACTION_DROP://落下
                    Log.i("test", "drop");
                    break;
                case DragEvent.ACTION_DRAG_ENDED://结束
                    mDragView.setBackgroundResource(R.drawable.dsgl_textview_enable_shape);
                    Log.i("test","end");
                    break;
            }
            return true;
        }
    };
    //查找要移动的新的位置
    private Rect preRect=new Rect();
    private int findIndex(int x, int y) {
        //如果是在同一个位置移动，就直接返回-1
        if (!preRect.contains(x,y)){
            for (int i = 0; i < mRects.length; i++) {
                if (mRects[i].contains(x,y)){
                    //保存新的位置
                    preRect=mRects[i];
                    return i;
                }
            }
        }
        return -1;
    }

    // //初始化每一个条目的位置，并保存到Rect数组
    private Rect[] mRects=null;
    private void initRects() {
        //获取gridlayout里面所有孩子的数量
        int childCount = mShowGridLayout.getChildCount();
        //初始化数组
        mRects=new Rect[childCount];
        //保存每一个孩子的位置
        for (int i = 0; i < childCount; i++) {
            //获取孩子
            View child = mShowGridLayout.getChildAt(i);
            //保存位置
            mRects[i]=new Rect(child.getLeft(),child.getTop(),child.getRight(),child.getBottom());
        }
    }
    private TextView createView(String text) {
        TextView textView=new TextView(getContext());
        //创建布局参数
        GridLayout.LayoutParams params=new GridLayout.LayoutParams();
        //设置宽高
        params.width=mWidthPixels/4-mDpToPx5*2;
        params.height=GridLayout.LayoutParams.WRAP_CONTENT;
        //设置间距
        params.setMargins(mDpToPx5, mDpToPx5, mDpToPx5, mDpToPx5);
        //把参数设置给TextView
        textView.setLayoutParams(params);
        //设置文字居中
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        //设置背景的shape图形
        textView.setBackgroundResource(R.drawable.dsgl_textview_enable_shape);
        //设置文本
        textView.setText(text);
        return textView;
    }
}
