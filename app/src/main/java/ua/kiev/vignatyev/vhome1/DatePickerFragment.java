package ua.kiev.vignatyev.vhome1;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private OnPickerInteractionListener mListener;
    private TextView mTextView;

    public void setTextView (TextView textView) {
        mTextView = textView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(mTextView != null) {
            mTextView.setText(Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(day) + " 23:59:59");
        }
        if(mListener != null) {
            mListener.onDateSet(view,  year,  month,  day);
        }
    }

    public void setListener(OnPickerInteractionListener listener){
        mListener = listener;
    }

    interface OnPickerInteractionListener {
        String onDateSet(DatePicker view, int year, int month, int day);
    }
}