package com.ethigeek.medtracker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ethigeek.medtracker.R;

import static com.ethigeek.medtracker.utils.Constants.REPORT;


/**
 * Class for Report fragment operations
 * Created by Abinaya on 24/03/17.
 */
public class ReportFragment extends Fragment {

    private Context context;
    private TabLayout tabs;

    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.report_fragment, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        FloatingActionButton fabSOS = (FloatingActionButton) getActivity().findViewById(R.id.fabSOS);
        TextView tvSOS = (TextView) getActivity().findViewById(R.id.tv_sos);
        fabSOS.setVisibility(View.GONE);
        tvSOS.setVisibility(View.GONE);

        getActivity().setTitle(REPORT);
        context = getActivity().getApplicationContext();

        // Setting ViewPager for each Tabs
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);


        // Set Tabs inside Toolbar
        tabs = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabs.setVisibility(View.VISIBLE);
        tabs.addTab(tabs.newTab());
        tabs.addTab(tabs.newTab());
        ReportFragment.Adapter adapter = new ReportFragment.Adapter(getChildFragmentManager(), tabs.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabs.post(new Runnable() {
            @Override
            public void run() {
                tabs.setupWithViewPager(viewPager);
            }
        });

        return view;
    }


    static class Adapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        String[] tabTitles = {"Measurements", "Consumptions"};

        public Adapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        /**
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        /**
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    MeasurementReportTab measurements = new MeasurementReportTab();
                    return measurements;
                case 1:
                    ConsumptionReportTab consumptions = new ConsumptionReportTab();
                    return consumptions;
                default:
                    return null;
            }
        }

        /**
         * @return
         */
        @Override
        public int getCount() {
            return mNumOfTabs;
        }

    }
}
