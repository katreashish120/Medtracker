package com.ethigeek.medtracker.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ethigeek.medtracker.managers.ConsumptionManager;
import com.ethigeek.medtracker.utils.Constants;
import com.ethigeek.medtracker.domain.Medicine;
import com.ethigeek.medtracker.domain.Reminder;
import com.ethigeek.medtracker.managers.CategoryManager;
import com.ethigeek.medtracker.managers.MedicineManager;
import com.ethigeek.medtracker.R;
import com.ethigeek.medtracker.managers.ReminderManager;
import com.ethigeek.medtracker.utils.ReminderUtils;
import com.ethigeek.medtracker.daoutils.DBHelper;
import com.ethigeek.medtracker.fragments.MedicineFragment;

import static com.ethigeek.medtracker.utils.Constants.*;

/**
 * Class for addition and update of Medicine
 * @author Ethiraj Srinivasan
 */
public class AddOrUpdateMedicineActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private EditText name, description, quantity, consumeQuantity, threshold, expirefactor, dateIssued, frequency, startTime, interval;
    private CheckBox reminder;
    private Spinner dosage, category;
    DatePickerDialog datePickerDialog;
    Calendar dateCalendar;
    private MedicineManager medicineManager;
    private Button saveButton;
    private Context context;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            DATE_FORMAT, Locale.ENGLISH);

    private static final Map<String, Integer> DOSAGE_HASH_MAP = createDosageHashMap();
    static final Map<Integer, String> DOSAGE_REVERSE_HASH_MAP = createDosageReverseHashMap();
    private TimePickerDialog timePickerDialog;
    private ReminderManager reminderManagerMedicine;
    private Map<String, Integer> categoriesMap;
    private String medicineName,medicineDateIssued;

    private static Map<Integer, String> createDosageReverseHashMap() {
        Map<Integer, String> result = new HashMap();
        for (Map.Entry<String, Integer> entry : DOSAGE_HASH_MAP.entrySet()) {
            result.put(entry.getValue(), entry.getKey());
        }
        return result;
    }

    private List<String> categoryList, dosageList;

    private static Map<String, Integer> createDosageHashMap() {
        Map<String, Integer> result = new HashMap();
        result.put("pills", 1);
        result.put("cc", 2);
        result.put("ml", 3);
        result.put("gr", 4);
        result.put("mg", 5);
        result.put("drops", 6);
        result.put("pieces", 7);
        result.put("puffs", 8);
        result.put("units", 9);
        result.put("teaspoon", 10);
        result.put("tablespoon", 11);
        result.put("patch", 12);
        result.put("mcg", 13);
        result.put("I", 14);
        result.put("meq", 15);
        result.put("spray", 16);
        return result;
    }

    /**
     * Method to run while creating UI for addition/edit
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        context = getApplicationContext();
        findViewsById();
        setListeners();
        populateDropDownList();
        Bundle b = getIntent().getExtras();
        if (b != null && b.getString(ACTION).equalsIgnoreCase(EDIT)) {
            updateSaveButton();
            updateMedicineValues(b.getInt(ID));
            setTitle(EDIT_MEDICINE);
        } else {
            setTitle(NEW_MEDICINE);
        }

    }

    private void populateDropDownList() {
        Cursor mCursor = CategoryManager.fetchAllCategoriesWithId(context);
        categoryList = new ArrayList<>();
        categoriesMap = new HashMap<>();
        while (mCursor.moveToNext()) {
            int id = mCursor.getInt(mCursor.getColumnIndex(DBHelper.CATEGORY_KEY_ID));
            String categoryName = mCursor.getString(mCursor.getColumnIndex(DBHelper.CATEGORY_KEY_CATEGORY));
            categoryList.add(categoryName); //add the item
            categoriesMap.put(categoryName, id);
        }

        ArrayAdapter<String> categoryDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);

        // Drop down layout style - list view with radio button
        categoryDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        category.setAdapter(categoryDataAdapter);

        dosageList = new ArrayList<>(DOSAGE_HASH_MAP.keySet());
        ArrayAdapter<String> dosageDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dosageList);
        // Drop down layout style - list view with radio button
        dosageDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        dosage.setAdapter(dosageDataAdapter);
    }

    /**
     * update the medicine values
     * @param id
     */
    private void updateMedicineValues(int id) {

        medicineManager = new MedicineManager();
        Medicine medicine = medicineManager.findById(context, id);

        reminderManagerMedicine = new ReminderManager();
        Reminder reminderDomain = reminderManagerMedicine.findById(context, medicine.getReminderId());
        name.setText(medicine.getName());
        description.setText(medicine.getDescription());
        category.setSelection(categoryList.indexOf(new CategoryManager().findById(context, medicine.getCategoryId()).getCategoryName()));
        reminder.setChecked(medicine.getRemind());
        quantity.setText(String.valueOf(medicine.getQuantity()));
        dosage.setSelection(dosageList.indexOf(DOSAGE_REVERSE_HASH_MAP.get(medicine.getDosage())));
        consumeQuantity.setText(String.valueOf(medicine.getConsumeQuantity()));
        threshold.setText(String.valueOf(medicine.getThreshold()));
        dateIssued.setText(medicine.getDateIssued());
        expirefactor.setText(String.valueOf(medicine.getExpireFactor()));
        frequency.setText(String.valueOf(reminderDomain.getFrequency()));
        startTime.setText(reminderDomain.getStartTime());
        interval.setText(String.valueOf(reminderDomain.getInterval()));
        name.setTag(id);
    }

    private void updateSaveButton() {
        saveButton.setTag(UPDATE);
        saveButton.setText(UPDATE);
    }


    private void findViewsById() {
        name = (EditText) findViewById(R.id.medicineName);
        description = (EditText) findViewById(R.id.medicineDescription);
        category = (Spinner) findViewById(R.id.medicineCategory);
        reminder = (CheckBox) findViewById(R.id.medicineReminder);
        quantity = (EditText) findViewById(R.id.medicineQuantity);
        dosage = (Spinner) findViewById(R.id.medicineDosage);
        consumeQuantity = (EditText) findViewById(R.id.medicineConsumeQuantity);
        threshold = (EditText) findViewById(R.id.medicineThreshold);
        dateIssued = (EditText) findViewById(R.id.medicineDateIssued);
        expirefactor = (EditText) findViewById(R.id.medicineExpireFactor);
        frequency = (EditText) findViewById(R.id.reminderFrequency);
        startTime = (EditText) findViewById(R.id.reminderStartTime);
        interval = (EditText) findViewById(R.id.reminderInterval);
        saveButton = (Button) findViewById(R.id.saveMedicine);
        saveButton.setTag(NEW);
    }

    private void setListeners() {
        dateIssued.setOnClickListener(this);
        startTime.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String hour, minutes;
                if (hourOfDay < 10)
                    hour = "0" + hourOfDay;
                else
                    hour = "" + hourOfDay;
                if (minute < 10)
                    minutes = "0" + minute;
                else
                    minutes = "" + minute;
                startTime.setText(hour + COLON + minutes);
            }
        },
                newCalendar.get(Calendar.HOUR),
                newCalendar.get(Calendar.MINUTE), false);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateCalendar = Calendar.getInstance();
                dateCalendar.set(year, monthOfYear, dayOfMonth);
                dateIssued.setText(formatter.format(dateCalendar.getTime()));
            }
        },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        saveButton.setOnClickListener(this);
        category.setOnItemSelectedListener(this);
        dateIssued.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0)
                    dateIssued.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        startTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0)
                    startTime.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * View
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.medicineDateIssued:
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
                break;
            case R.id.reminderStartTime:
                timePickerDialog.show();
                break;
            case R.id.saveMedicine:
                saveMedicine();
                break;
        }
    }

    private void saveMedicine() {
        boolean isValidFormat = checkFormat();
        if (!isValidFormat) {
            return;
        }
        medicineName = name.getText().toString();
        String medicineDescription = description.getText().toString();
        int medicineCategory = categoriesMap.get(category.getSelectedItem());
        Boolean medicineRemind = reminder.isChecked();
        int medicineQuantity = Integer.parseInt(quantity.getText().toString());
        int medicineDosage = DOSAGE_HASH_MAP.get(dosage.getSelectedItem());
        int medicineConsumeQuantity = Integer.parseInt(consumeQuantity.getText().toString());
        int medicineThreshold = Integer.parseInt(threshold.getText().toString());
        medicineDateIssued = dateIssued.getText().toString();
        int medicinceExpireFactor = Integer.parseInt(expirefactor.getText().toString());
        int reminderFrequency = Integer.parseInt(frequency.getText().toString());
        String reminderStartTime = startTime.getText().toString();
        int reminderInterval = Integer.parseInt(interval.getText().toString());

        if (saveButton.getTag().toString().equalsIgnoreCase(NEW)) {
            reminderManagerMedicine = new ReminderManager(reminderFrequency, reminderStartTime, reminderInterval);
            int medicineReminderId = (int) reminderManagerMedicine.save(context);
            medicineManager = new MedicineManager(medicineName, medicineDescription, medicineCategory, medicineReminderId, medicineRemind, medicineQuantity, medicineDosage, medicineConsumeQuantity, medicineThreshold, medicineDateIssued, medicinceExpireFactor);
            if (isValid()) {
                new SaveMedicine().execute();
            }
        } else {
            medicineManager.getMedicine().setName(medicineName);
            medicineManager.getMedicine().setDescription(medicineDescription);
            medicineManager.getMedicine().setCategoryId(medicineCategory);
            medicineManager.getMedicine().setRemind(medicineRemind);
            medicineManager.getMedicine().setQuantity(medicineQuantity);
            medicineManager.getMedicine().setDosage(medicineDosage);
            medicineManager.getMedicine().setConsumeQuantity(medicineConsumeQuantity);
            medicineManager.getMedicine().setThreshold(medicineThreshold);
            medicineManager.getMedicine().setDateIssued(medicineDateIssued);
            medicineManager.getMedicine().setExpireFactor(medicinceExpireFactor);
            reminderManagerMedicine.getReminder().setFrequency(reminderFrequency);
            reminderManagerMedicine.getReminder().setStartTime(reminderStartTime);
            reminderManagerMedicine.getReminder().setInterval(reminderInterval);
            reminderManagerMedicine.update(context);
            if (isValid()) {
                new UpdateMedicine().execute();
            }
        }
    }

    private class UpdateMedicine extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return medicineManager.update(context)==-1;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                Toast.makeText(context, MEDICINE_NOT_UPDATED, Toast.LENGTH_SHORT).show();
            } else {
                ReminderUtils.syncMedicineReminder(context);
                navigateToMainAcitivity();
            }
        }
    }

    private class SaveMedicine extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return medicineManager.save(context)==-1;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                Toast.makeText(context, MEDICINE_NOT_SAVED, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Medicine medicine = medicineManager.fetchMedicineByNameandDateIssued(context,medicineName,medicineDateIssued);
                    Date dateIssued = formatter.parse(medicine.getDateIssued());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateIssued);
                    Date currentDate = new Date();
                    while(currentDate.after(dateIssued)){
                        List<String> medicineTimeList = medicineManager.findConsumptionTime(context, medicine.getId());
                        for (String time : medicineTimeList) {
                            String dateTime = formatter.format(dateIssued.getTime());
                            if (!ConsumptionManager.exists(context, medicine.getId(), dateTime, time) && !dateTime.equalsIgnoreCase(formatter.format(currentDate))) {
                                ConsumptionManager consumptionManager = new ConsumptionManager(medicine.getId(), 0, dateTime, time);
                                consumptionManager.save(context);
                            }
                        }
                        calendar.add(Calendar.DATE,1);
                        dateIssued = calendar.getTime();
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ReminderUtils.syncMedicineReminder(context);
                navigateToMainAcitivity();
            }
        }
    }

    /**
     * Validate fields and return
     * @return
     */
    private boolean checkFormat() {

        boolean isValid = true;
        if (name.getText().toString().isEmpty()) {
            name.setError(MEDICINE_NAME_ERROR_MESSAGE);
            name.requestFocus();
            isValid = false;
        } else if (description.getText().toString().isEmpty()) {
            description.setError(MEDICINE_DESCRIPTION_ERROR_MESSAGE);
            description.requestFocus();
            isValid = false;
        } else if (quantity.getText().toString().isEmpty()) {
            quantity.setError(MEDICINE_QUANTITY_ERROR_MESSAGE);
            quantity.requestFocus();
            isValid = false;
        } else if (consumeQuantity.getText().toString().isEmpty()) {
            consumeQuantity.setError(MEDICINE_CONSUME_QUANTITY_ERROR_MESSAGE);
            consumeQuantity.requestFocus();
            isValid = false;
        } else if (threshold.getText().toString().isEmpty()) {
            threshold.setError(MEDICINE_THRESHOLD_ERROR_MESSAGE);
            threshold.requestFocus();
            isValid = false;
        } else if (dateIssued.getText().toString().isEmpty()) {
            dateIssued.setError(MEDICINE_DATE_ISSUED_ERROR_MESSAGE);
            dateIssued.requestFocus();
            isValid = false;
        } else if (expirefactor.getText().toString().isEmpty()) {
            expirefactor.setError(MEDICINE_EXPIRE_FACTOR_ERROR_MESSAGE);
            expirefactor.requestFocus();
            isValid = false;
        } else if (frequency.getText().toString().isEmpty()) {
            frequency.setError(MEDICINE_FREQUENCY_ERROR_MESSAGE);
            frequency.requestFocus();
            isValid = false;
        } else if (startTime.getText().toString().isEmpty()) {
            startTime.setError(MEDICINE_START_TIME_ERROR_MESSAGE);
            startTime.requestFocus();
            isValid = false;
        } else if (interval.getText().toString().isEmpty()) {
            interval.setError(MEDICINE_INTERVAL_ERROR_MESSAGE);
            interval.requestFocus();
            isValid = false;
        }
        return isValid;
    }

    /**
     * Validate fields and return
     * @return
     */
    private boolean isValid() {
        boolean isValid = true;
        if (medicineManager.getMedicine().getConsumeQuantity() > medicineManager.getMedicine().getQuantity()) {
            consumeQuantity.setError(MEDICINE_CONSUME_QUALITY_LESS_THAN_QUANTITY);
            consumeQuantity.requestFocus();
            isValid = false;
        } else if (medicineManager.getMedicine().getThreshold() > medicineManager.getMedicine().getQuantity()) {
            threshold.setError(MEDICINE_THRESHOLD_LESS_THAN_QUANTITY);
            threshold.requestFocus();
            isValid = false;
        } else if (medicineManager.getMedicine().getExpireFactor() > 24) {
            expirefactor.setError(MEDICINE_EXPIRE_FACTOR_LESS_THAN_24);
            expirefactor.requestFocus();
            isValid = false;
        } else if (medicineManager.getCategory(context).getRemind() == true && medicineManager.getMedicine().getRemind() == false) {
            AlertDialog.Builder warningDialog = new AlertDialog.Builder(this);
            warningDialog.setTitle(Constants.TITLE_WARNING);
            warningDialog.setMessage(MEDICINE_REMINDER_CANNOT_TURN_OFF_CATEGORY);
            warningDialog.setPositiveButton(Constants.BUTTON_OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface alert, int button) {
                    alert.dismiss();
                }
            });
            warningDialog.show();
            isValid = false;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            long fullDayTime = calendar.getTimeInMillis();
            String time[] = startTime.getText().toString().split(":");
            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            int frequencyReminder = Integer.parseInt(frequency.getText().toString());
            int intervalReminder = Integer.parseInt(interval.getText().toString());
            long maxTimeForReminder = calendar.getTimeInMillis() + (MINUTE * frequencyReminder * intervalReminder);
            if (maxTimeForReminder > fullDayTime) {
                frequency.setError(MEDICINE_PROPER_COMBINATION_OF_FREQUENCY_TIME_INTERVAL);
                frequency.requestFocus();
                isValid = false;
            }

        }
        return isValid;
    }

    /**
     * Navigation to Main Activity
     */
    public void navigateToMainAcitivity() {
        Intent intent = new Intent(context, MainActivity.class);
        MainActivity.currentFragment = MedicineFragment.class.getName();
        startActivity(intent);
        finish();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
