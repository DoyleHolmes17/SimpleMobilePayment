package com.ainosi.doyle.ainomobilepayment.helper;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.EditText;

import com.ainosi.doyle.ainomobilepayment.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DatePickers {
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String DATE_SQL_FORMAT = "yyyy-MM-dd";
    public static final String DATE_SQL_FULLFORMAT = "yyyy/MM/dd HH:mm:ss:SSS";
    public static final String DATE_SQL_FORMAT_SLASH = "yyyy/MM/dd";
    public static final String DATE_LOCAL_FORMAT = "dd/MM/yyyy";

    public static DatePickerDialog.OnDateSetListener showDatePickerDialog(final Calendar calendar, final EditText editText) {
        return new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_SQL_FORMAT);

                editText.setText(sdf.format(calendar.getTime()));
            }
        };
    }

    public static DatePickerDialog showDatePickerDialog(Context context, final Calendar calendar, final EditText editText) {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_SQL_FORMAT);

        DatePickerDialog dialog;
        DatePickerDialog.OnDateSetListener dateSetListener;
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                editText.setText(sdf.format(calendar.getTime()));
            }
        };

        dialog = new DatePickerDialog(context, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    editText.setText("");
                }
            }
        });

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.setText(sdf.format(calendar.getTime()));
            }
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        return dialog;
    }
}
