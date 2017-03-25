package sg.edu.nus.iss.se.ft05.medipal.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sg.edu.nus.iss.se.ft05.medipal.R;
import sg.edu.nus.iss.se.ft05.medipal.adapters.AppointmentListAdapter;
import sg.edu.nus.iss.se.ft05.medipal.model.Appointment;
import sg.edu.nus.iss.se.ft05.medipal.model.Consumption;


public class AppointmentTab extends Fragment {
    private Context context;
    private TextView textView;
    private RecyclerView recyclerView;
    private AppointmentListAdapter mAdapter;
    private String date;
    private List<Appointment> appointments;
    private static final String APPOINTMENTS="Today's Appointments";
    private static final String DATE_FORMAT="yyyy-MM-dd";
    private String content="";


    public AppointmentTab() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_appointment_tab,container,false);
        context=getActivity().getApplicationContext();

        Calendar calendar=Calendar.getInstance();
        Date d=calendar.getTime();
        date=new SimpleDateFormat(DATE_FORMAT).format(d);
        context=getActivity().getApplicationContext();
        recyclerView=(RecyclerView) view.findViewById(R.id.all_appointments_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        Cursor cursor = Appointment.filterDate(context,date);
        mAdapter = new AppointmentListAdapter(context,cursor);
        recyclerView.setAdapter(mAdapter);



        return view;
    }


}