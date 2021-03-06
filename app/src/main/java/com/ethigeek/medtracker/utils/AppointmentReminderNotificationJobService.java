package com.ethigeek.medtracker.utils;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;

import static com.ethigeek.medtracker.utils.Constants.CLINIC;
import static com.ethigeek.medtracker.utils.Constants.ID;



/**
 * Class for appointment reminder notification job service
 * @author Ethiraj Srinivasan
 */
public class AppointmentReminderNotificationJobService extends JobService {
    /**
     *
     * @param jobParameters
     * @return
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        PersistableBundle b = jobParameters.getExtras();
        String clinicName = b.getString(CLINIC);
        int appointmentId = b.getInt(ID);
        NotificationUtils.remindUserForAppointment(AppointmentReminderNotificationJobService.this,clinicName,appointmentId);
        jobFinished(jobParameters, false);
        return true;
    }

    /**
     *
     * @param jobParameters
     * @return
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
