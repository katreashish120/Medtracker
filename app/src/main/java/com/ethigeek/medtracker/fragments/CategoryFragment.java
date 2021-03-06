package com.ethigeek.medtracker.fragments;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ethigeek.medtracker.managers.CategoryManager;
import com.ethigeek.medtracker.R;
import com.ethigeek.medtracker.activities.AddOrUpdateCategoryActivity;
import com.ethigeek.medtracker.activities.MainActivity;
import com.ethigeek.medtracker.adapters.CategoryListAdapter;

import static com.ethigeek.medtracker.utils.Constants.*;

/**
 * A placeholder fragment containing a simple view.
 *
 * @author Ethiraj Srinivasan
 */
public class CategoryFragment extends Fragment {

    private CategoryListAdapter mAdapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.category_fragment, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        FloatingActionButton fabSOS = (FloatingActionButton) getActivity().findViewById(R.id.fabSOS);
        TextView tvSOS = (TextView) getActivity().findViewById(R.id.tv_sos);
        fabSOS.setVisibility(View.GONE);
        tvSOS.setVisibility(View.GONE);

        ((MainActivity) getActivity()).setFloatingActionButtonAction(AddOrUpdateCategoryActivity.class);
        RecyclerView categoryRecyclerView;
        context = getActivity().getApplicationContext();

        //hide the share button
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();

        categoryRecyclerView = (RecyclerView) view.findViewById(R.id.all_categories_list_view);
        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Get all guest info from the database and save in a cursor
        Cursor cursor = CategoryManager.findAll(context);

        // Create an adapter for that cursor to display the data
        mAdapter = new CategoryListAdapter(context, cursor);

        // Link the adapter to the RecyclerView
        categoryRecyclerView.setAdapter(mAdapter);

        getActivity().setTitle(CATEGORIES);

        return view;

    }

}
