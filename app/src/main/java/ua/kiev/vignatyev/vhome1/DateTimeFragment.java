package ua.kiev.vignatyev.vhome1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateTimeFragment extends Fragment {

    private static final String DATE_TIME_PARAM = "param1";
    private static final SimpleDateFormat mMysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // TODO: Rename and change types of parameters
    private Date mDateTime;

    private OnFragmentInteractionListener mListener;

    public DateTimeFragment() {
    }

    public static DateTimeFragment newInstance(String dateTimeString) {
        DateTimeFragment fragment = new DateTimeFragment();
        Bundle args = new Bundle();
        args.putString(DATE_TIME_PARAM, dateTimeString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                mDateTime = mMysqlDateFormat.parse( getArguments().getString(DATE_TIME_PARAM) );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_date_time, container, false);

        DatePicker datePicker = (DatePicker)v.findViewById(R.id.datePicker);
        TimePicker timePicker = (TimePicker) v.findViewById(R.id.timePicker);



        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
