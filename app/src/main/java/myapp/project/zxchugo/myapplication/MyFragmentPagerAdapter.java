package myapp.project.zxchugo.myapplication;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by ZxcHugo on 2017/2/19.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentsList;
    private ArrayList<String> TitleLists;

    public MyFragmentPagerAdapter(FragmentManager fragmentManager, ArrayList<Fragment> fragments,
                                  ArrayList<String> titleLists){
        super(fragmentManager);
        this.fragmentsList = fragments;
        this.TitleLists = titleLists;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    // 此方法用来显示tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {

        return TitleLists.get(position % TitleLists.size());
    }
}
