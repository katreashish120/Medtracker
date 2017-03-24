package sg.edu.nus.iss.se.ft05.medipal.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sg.edu.nus.iss.se.ft05.medipal.Util.NotificationUtils;
import sg.edu.nus.iss.se.ft05.medipal.Util.ReminderTasks;
import sg.edu.nus.iss.se.ft05.medipal.Util.ReminderUtils;
import sg.edu.nus.iss.se.ft05.medipal.constants.Constants;
import sg.edu.nus.iss.se.ft05.medipal.dao.DBHelper;
import sg.edu.nus.iss.se.ft05.medipal.model.Appointment;
import sg.edu.nus.iss.se.ft05.medipal.model.Category;
import sg.edu.nus.iss.se.ft05.medipal.model.Consumption;
import sg.edu.nus.iss.se.ft05.medipal.R;
import sg.edu.nus.iss.se.ft05.medipal.fragments.ConsumptionFragment;
import sg.edu.nus.iss.se.ft05.medipal.model.Medicine;

import static sg.edu.nus.iss.se.ft05.medipal.constants.Constants.*;

public class AddOrUpdateConsumption extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button saveButton;
    private  EditText quantity,date,time;
    private Spinner medicine;

    private Context context;
    DatePickerDialog datePickerDialog;
    Calendar dateCalendar;
    private TimePickerDialog timePickerDialog;
    private Consumption consumption;
    private List<String> medicineList;
    private Map<String, Integer> medicinesMap;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            DATE_FORMAT, Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_consumption);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        context = getApplicationContext();
        findViewsById();
        setListeners();
        populateDropDownList();
        Bundle b = getIntent().getExtras();
        if (b != null && b.getString(ACTION).equalsIgnoreCase(EDIT)) {
            updateSaveButton();
            updateConsumptionValues(b.getInt(ID));
            setTitle(EDIT_CONSUMPTION);
        } else {
            setTitle(NEW_CONSUMPTION);
        }

    }

    private void populateDropDownList() {
        Cursor mCursor = Medicine.fetchAllMedicinesWithId(context);
        medicineList = new ArrayList<>();
        medicinesMap = new HashMap<>();
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            int id = mCursor.getInt(mCursor.getColumnIndex(DBHelper.MEDICINE_KEY_ID));
            String categoryName = mCursor.getString(mCursor.getColumnIndex(DBHelper.MEDICINE_KEY_MEDICINE));
            medicineList.add(categoryName); //add the item
            medicinesMap.put(categoryName, id);
        }

        ArrayAdapter<String> medicineDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, medicineList);

        // Drop down layout style - list view with radio button
        medicineDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        medicine.setAdapter(medicineDataAdapter);

    }

    private void updateConsumptionValues(int id) {
        consumption = Consumption.findById(context, id);
        medicine.setSelection(medicineList.indexOf(Medicine.findById(context, consumption.getMedicineId()).getName()));
        quantity.setText(String.valueOf(consumption.getQuantity()));
        date.setText(consumption.getDate());
        time.setText(consumption.getTime());
    }

    private void updateSaveButton() {
        saveButton.setTag(UPDATE);
        saveButton.setText(UPDATE);
    }

    private void setListeners() {
        date.setOnClickListener(this);
        time.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                time.setText(hourOfDay + COLON + minute);
            }
        },
                newCalendar.get(Calendar.HOUR_OF_DAY),
                newCalendar.get(Calendar.MINUTE), true);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateCalendar = Calendar.getInstance();
                dateCalendar.set(year, monthOfYear, dayOfMonth);
                date.setText(formatter.format(dateCalendar.getTime()));
            }
        },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        saveButton.setOnClickListener(this);
        medicine.setOnItemSelectedListener(this);
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()!=0)
                    date.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()!=0)
                    time.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void findViewsById() {
        medicine = (Spinner) findViewById(R.id.consumptionMedicine);
        quantity = (EditText) findViewById(R.id.consumptionQuantity);
        date = (EditText) findViewById(R.id.consumptionDate);
        time = (EditText) findViewById(R.id.consumptionTime);
        saveButton = (Button) findViewById(R.id.saveConsumption);
        saveButton.setTag(NEW);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.consumptionDate:
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;
            case R.id.consumptionTime:
                timePickerDialog.show();
                break;
            case R.id.saveConsumption:
                saveOrUpdateConsumption();
                break;

        }
    }

    public void saveOrUpdateConsumption() {
        boolean isValidFormat = checkFormat();
        if (!isValidFormat) {
            return;
        }
        int consumptionMedicine = medicinesMap.get(medicine.getSelectedItem());
        int consumptionQuantity = Integer.parseInt(quantity.getText().toString());
        String consumptionDate = date.getText().toString();
        String consumptionTime = time.getText().toString();
        if (saveButton.getTag().toString().equalsIgnoreCase(NEW)) {
            consumption = new Consumption(consumptionMedicine,consumptionQuantity,consumptionDate,consumptionTime);
            if (isValid()) {
                if (consumption.save(context) == -1) {
                    Toast.makeText(context, CONSUMPTION_NOT_SAVED, Toast.LENGTH_SHORT).show();
                } else {
                    checkAndTriggerReplenishReminder();
                    navigateToMainAcitivity();

                }
            }

        } else {
            consumption.setMedicineId(consumptionMedicine);
            consumption.setQuantity(consumptionQuantity);
            consumption.setDate(consumptionDate);
            consumption.setTime(consumptionTime);
            if (isValid()) {
                if (consumption.update(context) == -1) {
                    Toast.makeText(context, CONSUMPTION_NOT_UPDATED, Toast.LENGTH_SHORT).show();
                } else {
                    checkAndTriggerReplenishReminder();
                    navigateToMainAcitivity();
                }
            }

        }

    }

    public void checkAndTriggerReplenishReminder(){
        int totalQuantity = Consumption.totalQuantityConsumed(context,consumption.getMedicineId());
        Medicine medicine = consumption.getMedicine(context);
        if (totalQuantity >= (medicine.getQuantity() - medicine.getThreshold()) ){
            NotificationUtils.replenishReminder(context,medicine.getName());
        }
    }

    private boolean checkFormat() {
        boolean isValid = true;
        if (quantity.getText().toString().isEmpty()) {
            quantity.setError(CONSUMPTION_QUANTITY_ERROR_MESSAGE);
            quantity.requestFocus();
            isValid = false;
        } else if (date.getText().toString().isEmpty()) {
            date.setError(CONSUMPTION_DATE_ERROR_MESSAGE);
            date.requestFocus();
            isValid = false;
        } else if (time.getText().toString().isEmpty()) {
            time.setError(CONSUMPTION_TIME_ERROR_MESSAGE);
            time.requestFocus();
            isValid = false;
        }
        return isValid;
    }

    public void navigateToMainAcitivity() {
        Intent intent = new Intent(context, MainActivity.class);
        MainActivity.currentFragment = ConsumptionFragment.class.getName();
        startActivity(intent);
    }


    private boolean isValid() {
        boolean isValid = true;
        Medicine consumptionMedicine = consumption.getMedicine(context);
        int consumeQuantity = consumptionMedicine.getConsumeQuantity();
        int frequency = consumptionMedicine.getReminder(context).getFrequency();
        if (consumption.getQuantity() > consumeQuantity) {
            quantity.setError(CONSUMPTION_QUANTITY_MORE_THAN_ERROR_MESSAGE + consumeQuantity);
            quantity.requestFocus();
            isValid = false;
        }
        else {

            List<Consumption> consumptions = Consumption.findByDate(context, consumption.getDate());
            if(consumptions.size() >= frequency){
                AlertDialog.Builder warningDialog = new AlertDialog.Builder(this);
                warningDialog.setTitle(Constants.TITLE_WARNING);
                warningDialog.setMessage(CONSUMPTION_FREQUENCY_NOT_MORE_THAN_ERROR_MESSAGE + frequency + CONSUMPTION_TIMES);
                warningDialog.setPositiveButton(Constants.OK_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface alert, int button) {
                        alert.dismiss();
                    }
                });
                warningDialog.show();
                isValid = false;
            }
            else {
               try {
                   if ( formatter.parse(consumption.getDate()).before(formatter.parse(consumptionMedicine.getDateIssued()))) {
                       AlertDialog.Builder warningDialog = new AlertDialog.Builder(this);
                       warningDialog.setTitle(Constants.TITLE_WARNING);
                       warningDialog.setMessage(CONSUMPTION_NOT_BEFORE_ERROR_MESSAGE);
                       warningDialog.setPositiveButton(Constants.OK_BUTTON, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface alert, int button) {
                               alert.dismiss();
                           }
                       });
                       warningDialog.show();
                       isValid = false;
                   }

               } catch (Exception e){

               }
            }
        }
        return isValid;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}