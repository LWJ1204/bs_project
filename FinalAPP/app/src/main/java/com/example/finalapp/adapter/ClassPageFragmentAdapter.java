package com.example.finalapp.adapter;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class ClassPageFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;
    private List<String> tittles;

    public ClassPageFragmentAdapter(@NonNull FragmentManager fm, List<Fragment>fms,List<String>tables) {
        super(fm);
        this.mFragmentList=fms;
        this.tittles=tables;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList==null?null:mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList==null?0:mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tittles==null?null:tittles.get(position);
    }
}
