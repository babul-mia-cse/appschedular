package com.meldcx.appschedule.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.meldcx.appschedule.views.AppScheduleAdapter
import com.meldcx.appschedule.R
import com.meldcx.appschedule.constant.Constant
import com.meldcx.appschedule.database.AppSchedule
import com.meldcx.appschedule.dateutils.DateTimeUtils
import com.meldcx.appschedule.worker.ScheduledWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class ScheduleDialog(
    private val appList: List<ApplicationInfo>,
    private val initialSchedule: AppSchedule? = null,
    private val onSave: (AppSchedule) -> Unit
) : DialogFragment() {

    private lateinit var appSpinner: Spinner
    private lateinit var timeEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    var mDate: Date? = null

    private val mSimpleDateFormat = SimpleDateFormat(Constant.DATE_FORMAT_REMINDER, Locale.ENGLISH)
    var date: Calendar = Calendar.getInstance()
    lateinit var etDateTime: TextInputEditText
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_schedule_app, null)
        val tvInputDateTime = view.findViewById<TextInputLayout>(R.id.text_input_date)
        etDateTime = view.findViewById<TextInputEditText>(R.id.text_input_edit_text_date)
        val imgCalender = view.findViewById<ImageView>(R.id.img_calender)

        appSpinner = view.findViewById(R.id.spinnerApp)
        saveButton = view.findViewById(R.id.btnSave)
        cancelButton = view.findViewById(R.id.btnCancel)

        // Setup spinner adapter for app list
        val adapter = AppScheduleAdapter(requireContext(), appList)
        appSpinner.adapter = adapter

        // If editing existing schedule, populate fields
        initialSchedule?.let { schedule ->

            etDateTime.setText(
                DateTimeUtils.convertMillisToDateTime(schedule.launchTime)
            )
        }


        tvInputDateTime.setOnClickListener { showDateTimePicker() }
        imgCalender.setOnClickListener { showDateTimePicker() }


        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        saveButton.setOnClickListener {
            val uuID = UUID.randomUUID()
            val selectedApp =
                getAppLabel(requireContext(), appSpinner.selectedItem as ApplicationInfo)
            val packageName = (appSpinner.selectedItem as ApplicationInfo).packageName




            val schedule = initialSchedule?.copy(
                appName = selectedApp,
                launchTime = date.time.time,
                packageName = packageName
            )
                ?: AppSchedule(
                    launchTime = date.time.time,
                    uuid = uuID,
                    appName = selectedApp,
                    executed = false,
                    appLabel = selectedApp,
                    packageName = packageName
                ) // id=0 for new schedule
            scheduleReminderAt(requireContext(), uuID, packageName, date.time.time)

            onSave(schedule)
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    fun getAppLabel(context: Context, appInfo: ApplicationInfo): String {
        return context.packageManager.getApplicationLabel(appInfo).toString()
    }

    private fun showDateTimePicker() {
        val currentDate = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.datepicker,
            { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                date.set(year, monthOfYear, dayOfMonth)
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    R.style.datepicker,
                    { view1: TimePicker?, hourOfDay: Int, minute: Int ->
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        date.set(Calendar.MINUTE, minute)
                        Log.d("The chosen one ", date.time.toString())
                        mDate = date.time
                        etDateTime.setText(
                            mSimpleDateFormat.format(date.time)
                        )
                    }, 15,
                    0,
                    false
                )
                timePickerDialog.show()
            }, currentDate[Calendar.YEAR], currentDate[Calendar.MONTH],
            currentDate[Calendar.DATE]
        )
        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.show()
    }

    private fun scheduleReminderAt(
        context: Context,
        id: UUID,
        packageName: String,
        targetTimeInMillis: Long
    ) {
        val currentTimeInMillis = System.currentTimeMillis()
        val delay = targetTimeInMillis - currentTimeInMillis

        if (delay > 0) {
            val inputData = workDataOf(
                Constant.KEY_REMINDER_ID to id.toString(),
                Constant.KEY_REMINDER_TRIGGER_TIME to targetTimeInMillis,
                Constant.KEY_PACKAGE_NAME to packageName
            )
            cancelSchedule(context, packageName)

            val workRequest = OneTimeWorkRequestBuilder<ScheduledWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork("schedule_$packageName", ExistingWorkPolicy.REPLACE, workRequest)
        } else {
            Log.d("Target time is in the past.", "delay <==0")
        }
    }

    fun cancelSchedule(context: Context, packageName: String) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("schedule_$packageName")
    }


}