package com.example.draganddrop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.draganddrop.widget.DragSortGridLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DragSortGridLayout mDragSortGridLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDragSortGridLayout = ((DragSortGridLayout) findViewById(R.id.dragSortGridLayout));
        //设置数据
        List<String> showItems=new ArrayList<>();
        showItems.add("邓紫棋");
        showItems.add("范冰冰");
        showItems.add("杨幂");
        showItems.add("唐嫣");
        showItems.add("刘诗诗");
        showItems.add("李小璐");
        showItems.add("柳岩");
        showItems.add("赵丽颖");
        showItems.add("张柏芝");
        showItems.add("波多");
        showItems.add("苍井空");
        showItems.add("李宇春");
        List<String> hideItems=new ArrayList<>();
        hideItems.add("杰伦");
        hideItems.add("李易峰");
        hideItems.add("陈冠希");
        hideItems.add("周星驰");
        hideItems.add("赵本山");
        hideItems.add("刘德华");
        hideItems.add("李玉刚");
        mDragSortGridLayout.setShowAndHideItems(showItems,hideItems);
    }
}
