package com.meldcx.appschedule.views
//noinspection SuspiciousImport
import android.R
import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class AppScheduleAdapter(context: Context, private val items:  List<ApplicationInfo>)
    : ArrayAdapter<ApplicationInfo>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.simple_spinner_item, parent, false)
        val tv = view.findViewById<TextView>(R.id.text1)
        tv.text = getAppLabel(context, items[position])
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.simple_spinner_dropdown_item, parent, false)
        val tv = view.findViewById<TextView>(R.id.text1)
        tv.text = getAppLabel(context, items[position])
        return view
    }

    fun getAppLabel(context: Context, appInfo: ApplicationInfo): String {
        return context.packageManager.getApplicationLabel(appInfo).toString()
    }
}