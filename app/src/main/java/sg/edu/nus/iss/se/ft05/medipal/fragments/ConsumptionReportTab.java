package sg.edu.nus.iss.se.ft05.medipal.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sg.edu.nus.iss.se.ft05.medipal.R;
import sg.edu.nus.iss.se.ft05.medipal.adapters.ConsumptionListAdapter;
import sg.edu.nus.iss.se.ft05.medipal.constants.Constants;
import sg.edu.nus.iss.se.ft05.medipal.model.Consumption;

import static sg.edu.nus.iss.se.ft05.medipal.constants.Constants.DATE_FORMAT;

/**
 * Created by ethi on 25/03/17.
 */

public class ConsumptionReportTab extends Fragment implements View.OnClickListener{



    private RecyclerView consumptionRecyclerView;
    private Context context;
    private ConsumptionListAdapter mAdapter;
    private View view;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            DATE_FORMAT, Locale.ENGLISH);
    private EditText dateFrom,dateTo;
    private DatePickerDialog datePickerDialogFrom,datePickerDialogTo;
    private String dateFromText,dateToText;
    private Date dateObjFrom,dateObjTo;
    private Calendar dateCalendarFrom,dateCalendarTo;
    private Consumption consumption;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.consumption_report_share, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.consumption_report_tab, container, false);
        consumptionRecyclerView = (RecyclerView) view.findViewById(R.id.all_consumption_list_view);
        context = getActivity().getApplicationContext();
        findViewsById();

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        consumptionRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Get all guest info from the database and save in a cursor
        Cursor cursor = Consumption.findAll(context);

        // Create an adapter for that cursor to display the data
        mAdapter = new ConsumptionListAdapter(context, cursor);

        // Link the adapter to the RecyclerView
        consumptionRecyclerView.setAdapter(mAdapter);
        setListeners();
        setValues();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                //update the list
                mAdapter.swapCursor(Consumption.findAll(context));
                //get the id of the item being swiped
                int id = (int) viewHolder.itemView.getTag();
                //remove from DB
                consumption = Consumption.findById(context, id);
                AlertDialog.Builder warningDialog = new AlertDialog.Builder(getActivity(),R.style.AppTheme_Dialog);
                warningDialog.setTitle(Constants.TITLE_WARNING);
                warningDialog.setMessage(R.string.warning_delete);
                warningDialog.setPositiveButton(Constants.BUTTON_YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface alert, int which) {
                        //remove from DB
                        consumption.delete(context);
                        Toast.makeText(context, R.string.delete_success, Toast.LENGTH_SHORT).show();
                        //update the list
                        mAdapter.swapCursor(Consumption.findAll(context));
                        alert.dismiss();
                    }
                });
                warningDialog.setNegativeButton(Constants.BUTTON_NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface alert, int which) {
                        alert.dismiss();
                    }
                });
                warningDialog.show();
            }

        }).attachToRecyclerView(consumptionRecyclerView);
        return view;
    }

    private void setValues() {
        dateFrom.setText(formatter.format(Calendar.getInstance().getTime()));
        dateTo.setText(formatter.format(Calendar.getInstance().getTime()));
    }

    private void findViewsById() {
        dateFrom = (EditText) view.findViewById(R.id.fromDate);
        dateTo = (EditText) view.findViewById(R.id.toDate);
    }

    private void setListeners(){
        dateFrom.setOnClickListener(this);
        dateTo.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialogFrom = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateCalendarFrom = Calendar.getInstance();
                dateCalendarFrom.set(year, monthOfYear, dayOfMonth);
                dateFrom.setText(formatter.format(dateCalendarFrom.getTime()));
            }
        },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialogTo = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateCalendarTo = Calendar.getInstance();
                dateCalendarTo.set(year, monthOfYear, dayOfMonth);
                dateTo.setText(formatter.format(dateCalendarTo.getTime()));
            }
        },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

        dateFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    dateFromText = dateFrom.getText().toString();

                    dateToText = dateTo.getText().toString();
                    if(dateToText.length() != 0)
                        checkDateAndSwapCursor();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dateTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    dateFromText = dateFrom.getText().toString();
                    dateToText = dateTo.getText().toString();
                    if(dateFromText.length() != 0)
                        checkDateAndSwapCursor();
                }
                

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    
    private void checkDateAndSwapCursor(){
        try {
            dateObjFrom = formatter.parse(dateFromText);
            dateObjTo = formatter.parse(dateToText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(dateObjTo.after(dateObjFrom)){
            triggerFilterForDate();
        }
    }

    private void triggerFilterForDate() {
        Cursor cursor = Consumption.betweenDate(context,dateFromText,dateToText);
        mAdapter.swapCursor(cursor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.share_consumption:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "xcx");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subjct");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fromDate:
                datePickerDialogFrom.show();
                break;
            case R.id.toDate:
                try {
                    dateObjFrom = formatter.parse(dateFromText);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar newCalendar = Calendar.getInstance();
                datePickerDialogTo = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateCalendarTo = Calendar.getInstance();
                        dateCalendarTo.set(year, monthOfYear, dayOfMonth);
                        dateTo.setText(formatter.format(dateCalendarTo.getTime()));
                    }
                },
                        newCalendar.get(Calendar.YEAR),
                        newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialogTo.getDatePicker().setMinDate(dateObjFrom.getTime());
                datePickerDialogTo.show();
                break;
        }
    }
}
