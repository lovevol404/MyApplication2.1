package myapp.project.zxchugo.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by ZxcHugo on 2017/2/19.
 * 此文件为实现日程的Tab滑动菜单，对应布局routine.xml
 * 具体今日和未来日程请分别在todayroutine.xml和futureroutine.xml中实现，或者自行设计
 */

public class FragmentRoutine extends Fragment {

    private ArrayList<Fragment> fragmentsList;
    private ArrayList<String> titles;
    private ViewPager viewPager;
    Fragment todayRoutine;
    Fragment futureRoutine;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    // tab选项卡实现
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.routine, null);
        viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        fragmentsList = new ArrayList<>();
        titles = new ArrayList<>();         // tab标题

        todayRoutine = new TodayRoutine();
        futureRoutine = new FutureRoutine();

        fragmentsList.add(todayRoutine);
        fragmentsList.add(futureRoutine);
        titles.add("今日日程");
        titles.add("未来日程");

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentsList, titles);
        viewPager.setAdapter(adapter);

        // TabLayout
        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}
