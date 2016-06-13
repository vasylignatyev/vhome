package ua.com.vi_port.vhome;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ua.com.vi_port.vhome.ajax.HTTPManager;
import ua.com.vi_port.vhome.ajax.RequestPackage;

/**
 * A placeholder fragment containing a simple view.
 */
public class VarchPlayerFragment extends Fragment
        implements ScrollBarView.ScrollBarViewInterface, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, OnTouchListener , View.OnClickListener {

    public static final String ARG_VCAM_TOKEN = "vcam_token";
    public static final String ARG_USER_TOKEN = "user_token";
    public static final String ARG_I_MOTION_DETECT = "i_motion_detect";
    public static final String ARG_ISSUE_DATE = "issue_date";

    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yy");
    private static final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss");


    private String mVcamToken;
    private String mUserToken;
    private int mIMotionDetect;
    private static int mICustomerVcam = 143;
    private Date mIssueDate;

    private int mOldOptions;

    private static final SimpleDateFormat mMysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ScrollBarView mScrollBarView;
    private VideoView mVideoView = null;
    private ImageButton mIbPlay;
    private TextView mTvDate, mTvTime;

    private Boolean mTimerStarted = false;

    private String mCurrentVarchName;
    private Date mVarchIssueDate;
    private int mVarchOffset; //msec

    Timer t;

    private final Date mMotionDetectEnd = new Date();

    private GestureDetectorCompat mGestureDetector;

    private boolean mShowMotionDetect = false;

    /**
     * Getter & Setter
     */
    public static int getICustomerVcam(){
        return mICustomerVcam;
    }
    public String getVcamToken() {
        return mVcamToken;
    }

    /**
     * CONSTRUCTORS
     */
    public static VarchPlayerFragment newInstance(String userToken, String vcamToken) {
        VarchPlayerFragment fragment = new VarchPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_TOKEN, userToken);
        args.putString(ARG_VCAM_TOKEN, vcamToken);
        fragment.setArguments(args);
        return fragment;
    }
    public static VarchPlayerFragment newInstance(Bundle args ) {
        VarchPlayerFragment fragment = new VarchPlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public VarchPlayerFragment() {
    }


    /**
     * LIFE CYCLE
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mGestureDetector = new GestureDetectorCompat( context, new GestureListener());
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        if (arg != null) {
            mVcamToken = arg.getString(ARG_VCAM_TOKEN, null);
            mUserToken = arg.getString(ARG_USER_TOKEN, null);
            mIMotionDetect = arg.getInt(ARG_I_MOTION_DETECT, 0);
            final String issue_date = arg.getString(ARG_ISSUE_DATE, null);
            try {
                if( issue_date != null )
                    mIssueDate = mMysqlDateFormat.parse(issue_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//**********************************************************
        android.app.ActionBar actionBar = getActivity().getActionBar();
        mOldOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        int newOptions = mOldOptions;
        newOptions &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
        newOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        newOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getActivity().getWindow().getDecorView().setSystemUiVisibility(newOptions);
        actionBar.hide();
//***********************************************

        View view = inflater.inflate(R.layout.fragment_varch_player, container, false);

        mScrollBarView = (ScrollBarView) view.findViewById(R.id.scrollBarView);
        mIbPlay = (ImageButton) view.findViewById(R.id.ibPlay);
        mTvDate =(TextView) view.findViewById(R.id.tvDate);
        mTvTime =(TextView) view.findViewById(R.id.tvTime);

        mIbPlay.setOnClickListener(this);

        mScrollBarView.setScrollListener(this);

        mVideoView = (VideoView) view.findViewById(R.id.videoView);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnTouchListener(this);

        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (mTimerStarted & mVideoView.isPlaying() & (mVarchIssueDate != null)) {
                                final Date currentPosition = new Date();
                                //calculate current player position
                                currentPosition.setTime(mVarchIssueDate.getTime() + mVideoView.getCurrentPosition());

                                if (mShowMotionDetect & (currentPosition.getTime() > mMotionDetectEnd.getTime())) {
                                    mVideoView.stopPlayback();
                                    startPlay(mScrollBarView.getNextMD());
                                } else {
                                    mScrollBarView.setCurrentDate(currentPosition);
                                    mTvDate.setText(mDateFormat.format(currentPosition));
                                    mTvTime.setText(mTimeFormat.format(currentPosition));
                                }
                            }
                        }
                    }
                );
            }
        }, 0, 1000);

        if( mIssueDate != null ) {
            Log.d("MyApp", "Set Issue Date -> " + mIssueDate);
            Date tmpDate = new Date(mIssueDate.getTime() - 15000);
            mScrollBarView.setCurrentDate(tmpDate);
            getVarchByDate();
        } else {
            Log.d("MyApp", "Set Issue Date -> is NULL");
        }
        return view;
        //****** GESTURE
    }

    @Override
    public void onDestroyView() {
        android.app.ActionBar actionBar = getActivity().getActionBar();
        getActivity().getWindow().getDecorView().setSystemUiVisibility(mOldOptions);
        actionBar.show();

        t.cancel();

        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * IMPLEMENTED
     */
    @Override
    public void startPlay(Date stop) {
        if( stop != null) {
            mMotionDetectEnd.setTime(stop.getTime() + 15000);
        }
        getVarchByDate();
    }

    @Override
    public void onChange() {
        mTimerStarted = false;
    }

    @Override
    public Boolean showMotionDetect() {
        return mShowMotionDetect;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        getVarchNext();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.seekTo(mVarchOffset);
        mVarchOffset = 0;
        mIbPlay.setImageResource(R.drawable.ic_play_button);
        mp.start();
    }
    //*********************************************
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.ibPlay:
                playButtonClick(v);
                break;
        }
    }
    private void playButtonClick(View v){
        if(mVideoView.isPlaying()) {
            mVideoView.pause();
            mIbPlay.setImageResource(R.drawable.pause);
        } else {
            mVideoView.start();
            mIbPlay.setImageResource(R.drawable.ic_play_button);
        }
    }

    //*********************************************
    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            onVideoViewDoubleTap();
            return super.onDoubleTap(e);
        }
    }

    private void onVideoViewDoubleTap() {
        Log.d("MyApp", "knock knock");
        //taggle mShowMotionDetect
        mShowMotionDetect = !mShowMotionDetect;
        
        if(mShowMotionDetect){
            Log.d("MyApp", "mShowMotionDetect true");
            startPlay(mScrollBarView.getNextMD());
        } else {
            Log.d("MyApp", "mShowMotionDetect false");
        }

    }

    /**
     * REST Request for getVarchByDate
     */
    private void getVarchByDate() {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getVarchByDate");
        rp.setParam("vcam_token", mVcamToken);
        rp.setParam("varch_date", mMysqlDateFormat.format(mScrollBarView.getCurrentDate()));
        GetVarchByDateAsyncTask task = new GetVarchByDateAsyncTask();
        task.execute(rp);
    }
    private class GetVarchByDateAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getVarchByDate: " + s);

            try {
                JSONObject obj = new JSONObject(s);

                if(obj.has("varch_name")) {
                    mCurrentVarchName = obj.getString("varch_name");
                    varchURL(mCurrentVarchName);
                }
                if(obj.has("varch_offset")){
                    mVarchOffset = obj.getInt("varch_offset") * 1000;
                }
            } catch (JSONException e ) {
                e.printStackTrace();
            }

        }
    }
    /**
     * REST Request for Varch URL
     */
    public void varchURL(String varchName) {
        Log.d("MyApp", "vArchURL name : " + varchName);
        //pd.show();
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "varchURL");
        rp.setParam("user_token", mUserToken);
        rp.setParam("vcam_token", mVcamToken);
        rp.setParam("varch_name", varchName);

        GetVarchURLAsyncTask task = new GetVarchURLAsyncTask();
        task.execute(rp);
    }
    public class GetVarchURLAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            if(s == null)
                return;
            JSONObject linkName;
            try {
                linkName = new JSONObject(s);
                if( linkName.has("url") ) {
                    String mVarchLinkName = linkName.getString("url");
                    Log.d(" MyApp", "GetVarchURLAsyncTask Replay: " + mVarchLinkName);
                    mVideoView.setVideoPath(MainActivity.SERVER_URL + mVarchLinkName);
                    mTimerStarted = true;
                }
                if(linkName.has("issue_date")) {
                    mVarchIssueDate = mMysqlDateFormat.parse(linkName.getString("issue_date"));
                    mScrollBarView.setCurrentDate(mVarchIssueDate.getTime() + mVarchOffset);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * REST Request for getVarchNext
     */
    public void getVarchNext() {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getVarchNext");
        rp.setParam("vcam_token", mVcamToken);
        rp.setParam("varch_name", mCurrentVarchName);

        GetVarchNextAsyncTask task = new GetVarchNextAsyncTask();
        task.execute(rp);
    }
    public class GetVarchNextAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject obj = new JSONObject(s);
                if(obj.has("varch_name")) {
                    mCurrentVarchName = obj.getString("varch_name");
                    mVarchOffset = 0;
                    varchURL(mCurrentVarchName);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            mScrollBarView.invalidate();
        }
    }
}
