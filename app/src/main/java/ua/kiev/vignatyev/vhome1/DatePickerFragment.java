package ua.kiev.vignatyev.vhome1;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import ua.kiev.vignatyev.vhome1.models.ShareVcamUser;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static String TIME_PARAM = "time";
    private OnPickerInteractionListener mListener;
    private TextView mTextView;

    private ShareVcamUser mShareVcamUser = null;


    public void setTextView (TextView textView) {
        mTextView = textView;
    }

    public void setListener(OnPickerInteractionListener listener){
        mListener = listener;
    }

    //public DatePickerFragment setDate

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        long time = 0;

        if (getArguments() != null) {
            time = getArguments().getLong(TIME_PARAM, 0);
        }
        if (time != 0) {
            c.setTime(new Date(time));
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(mShareVcamUser != null) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            mShareVcamUser.EXPIRATION = c.getTime();
        }
        if((mListener != null)) {
            mListener.onDateSet( view, year, month, day);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d("MyApp", "DatePickerFragment cancel.");
        if(mShareVcamUser != null) {
            mShareVcamUser.EXPIRATION = null;
        }
        if((mListener != null)) {
            mListener.onDateSet( null, 0, 0, 0);
        }
    }

    public void setShareVcamUser(final ShareVcamUser shareVcamUser) {
        mShareVcamUser = shareVcamUser;
    }

     public interface OnPickerInteractionListener {
        void onDateSet(DatePicker view, int year, int month, int day);
     }
}