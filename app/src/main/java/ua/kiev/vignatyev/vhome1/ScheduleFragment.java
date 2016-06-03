package ua.kiev.vignatyev.vhome1;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import ua.kiev.vignatyev.vhome1.ajax.HTTPManager;
import ua.kiev.vignatyev.vhome1.ajax.RequestPackage;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScheduleFragment extends Fragment {

    public static final String SCHEDULE = "schedule";

    private String mSchedule;

     private LinearLayout llSchedule;


    public ScheduleFragment() {
    }

    public static ScheduleFragment newInstance(String schedule) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString( SCHEDULE, schedule);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_schedule, container, false);

        if (getArguments() != null) {
            mSchedule = getArguments().getString(SCHEDULE, null);
        }
        llSchedule = (LinearLayout) v.findViewById(R.id.llSchedule);

        Button btnWorkingDays = (Button) v.findViewById(R.id.btnWorkingDays);
        btnWorkingDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWorkingDaysClick(v);
            }
        });
        Button btnWeekend = (Button) v.findViewById(R.id.btnWeekend);
        btnWeekend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWeekendClick(v);
            }
        });
        Button btnClear = (Button) v.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearClick(v);
            }
        });

        if(mSchedule != null) {
            setSchedule(mSchedule);
        }

        return v;
    }

    private void onWorkingDaysClick (View v){
        int rowCount = llSchedule.getChildCount();
        for( int r = 0 ; r <  rowCount; r++) {
            View rowChild = llSchedule.getChildAt(r);
            if (rowChild instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) rowChild;
                int count = ll.getChildCount();
                int j = 0;
                for (int i = 0; i <= count; i++) {
                    View view = ll.getChildAt(i);
                    if (view instanceof CheckBox) {
                        if( j++ < 5 ) {
                            CheckBox cb = (CheckBox)view;
                            cb.setChecked(true);
                        }
                    }
                }
            }
        }
    }
    private void onWeekendClick (View v){
        int rowCount = llSchedule.getChildCount();
        for( int r = 0 ; r <  rowCount; r++) {
            View rowChild = llSchedule.getChildAt(r);
            if (rowChild instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) rowChild;
                int count = ll.getChildCount();
                int j = 0;
                for (int i = 0; i <= count; i++) {
                    View view = ll.getChildAt(i);
                    if (view instanceof CheckBox) {
                        if( j++ > 4 ) {
                            CheckBox cb = (CheckBox)view;
                            cb.setChecked(true);
                        }
                    }
                }
            }
        }
    }
    private void onClearClick (View v){
        int rowCount = llSchedule.getChildCount();
        for( int r = 0 ; r <  rowCount; r++) {
            View rowChild = llSchedule.getChildAt(r);
            if (rowChild instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) rowChild;
                int count = ll.getChildCount();
                for (int i = 0; i <= count; i++) {
                    View view = ll.getChildAt(i);
                    if (view instanceof CheckBox) {
                        CheckBox cb = (CheckBox)view;
                        cb.setChecked(false);
                    }
                }
            }
        }
    }
    private void setSchedule(String schedule) {
        //Log.d("MyApp" , "ScheduleFragment::setSchedule schedule:" + schedule);
        if((schedule == null) || ( schedule.length() < 168)) {
            Log.d("MyApp", "setSchedule param too short: " + schedule.length());
            return;
        }
        int rowCount = llSchedule.getChildCount();
        for( int r = 0 ; r < rowCount ; r++) {
            View rowChild = llSchedule.getChildAt(r);
            if (rowChild instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) rowChild;
                int llCount = ll.getChildCount();
                int j = 0;
                for (int i = 0; i < llCount; i++) {
                    View view = ll.getChildAt(i);
                    if (view instanceof CheckBox) {
                        int strIndex = r + j*24 + 1;
                        Log.d("MyApp", "strIndex: " + strIndex + " - " + schedule.charAt(strIndex));
                        ((CheckBox)view).setChecked(schedule.charAt(strIndex) == '1');
                        j++;
                    }
                }
            }
        }
    }

    public String getSchedule(){
        String schedule = "";

        String[] scheduleArray = null;

        int rowCount = llSchedule.getChildCount();
        for( int r = 0 ; r <  rowCount; r++) {
            View rowChild = llSchedule.getChildAt(r);
            if (rowChild instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) rowChild;
                int count = ll.getChildCount();
                if(scheduleArray == null) {
                    scheduleArray = new String[count-1];
                }
                int j = 0;
                for (int i = 0; i <= count; i++) {
                    View view = ll.getChildAt(i);
                    if (view instanceof CheckBox) {
                        CheckBox cb = (CheckBox)view;
                        if(scheduleArray[j] == null) {
                            scheduleArray[j] = "";
                        }
                        scheduleArray[j] += cb.isChecked() ? "1" : "0";
                        j++;
                    }
                }
            }
        }
        for( int k = 0 ; k < scheduleArray.length ; k++ ){
            if((scheduleArray[k] != null)) {
                schedule += scheduleArray[k];
            }
        }
        return schedule;
    }
}
