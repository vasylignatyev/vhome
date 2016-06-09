package ua.com.vi_port.vhome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

import ua.com.vi_port.vhome.ajax.HTTPManager;
import ua.com.vi_port.vhome.ajax.RequestPackage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VcamPlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VcamPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VcamPlayerFragment extends Fragment {

    public static final String TAG = "VcamPlayerFragment";

    private int mVcamPosition;
    private String mVcamToken;
    private String mStreamURL;
    private int mOldOptions;
    private ProgressDialog pd;



    VideoView mVideoPlayerView;
    DisplayMetrics dm;

    private MainActivity mMainActivity;

    Timer myTimer;

    private static final String ARG_VCAM_URL = "vcamUrl";
    private static final String ARG_POSITION = "position";
    private static final String ARG_VCAM_TOKEN = "vcam_token";

    public static VcamPlayerFragment newInstance(String vcamUrl, int position, String vcam_token) {

        VcamPlayerFragment fragment = new VcamPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VCAM_URL, vcamUrl);
        args.putInt(ARG_POSITION, position);
        args.putString(ARG_VCAM_TOKEN, vcam_token);
        fragment.setArguments(args);
        return fragment;
    }

    public void getInit() {
        //set flag that inform player is started

        mVideoPlayerView = (VideoView) mMainActivity.findViewById(R.id.video_player_view);
        dm = new DisplayMetrics();
        mMainActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        if(mVideoPlayerView != null ) {
            mVideoPlayerView.setMinimumWidth(width);
            mVideoPlayerView.setMinimumHeight(height);
            mVideoPlayerView.setVideoPath(mStreamURL);
            mVideoPlayerView.start();
        }
        mVideoPlayerView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pd.hide();
            }
        });
    }

    public VcamPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof MainActivity){
            mMainActivity = (MainActivity) context;
        }
        pd = new ProgressDialog(context);
        pd.setTitle("Подключение к серверу");
        pd.setMessage("Ожидайте");

        mMainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mStreamURL = args.getString(ARG_VCAM_URL, null);
            mVcamPosition = args.getInt(ARG_POSITION, 0);
            mVcamToken = args.getString(ARG_VCAM_TOKEN, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Убираем все ненужное
        android.app.ActionBar actionBar = mMainActivity.getActionBar();
        mOldOptions = mMainActivity.getWindow().getDecorView().getSystemUiVisibility();
        int newOptions = mOldOptions;
        newOptions &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
        newOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        newOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        mMainActivity.getWindow().getDecorView().setSystemUiVisibility(newOptions);
        actionBar.hide();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_palyer, container, false);
        mVideoPlayerView = (VideoView) view.findViewById(R.id.video_player_view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        Log.d("MyApp","onStart");
        super.onStart();

        runStream(mVcamToken);

        myTimer = new Timer(); // Создаем таймер
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                keepAliveStream();
            }
        }, 10000L, 10000L);
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d("MyApp", "onStop");
        myTimer.cancel();
    }

    @Override
    public void onDestroyView() {
        android.app.ActionBar actionBar = mMainActivity.getActionBar();
        mMainActivity.getWindow().getDecorView().setSystemUiVisibility(mOldOptions);
        actionBar.show();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mMainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onDetach();
    }


    public void keepAliveStream() {
        Log.d("MyApp", "keepAliveStream position: " + mVcamPosition);
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/functions/streamControl.php");
        rp.setMethod("GET");
        rp.setParam("token", mVcamToken);
        rp.setParam("appname", "myapp");
        rp.setParam("server_ip", "0.0.0.0");
        rp.setParam("sessname", "sessname");
        rp.setParam("action", "keepAlive");
        rp.setParam("hls", "true");

        KeepAliveStreamAsyncTask task = new KeepAliveStreamAsyncTask();
        task.execute(rp);
    }
    
    /**
     * Async task Start Vcam Streaming
     */
    public class KeepAliveStreamAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp","KeepAliveStreamAsyncTask Replay: " + s);
        }
    }

    /**
     * REST Request for Vcam List
     */
    public void runStream(String token) {
        Log.d("MyApp", "runStream token: " + token);
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/functions/streamControl.php");
        rp.setMethod("GET");
        rp.setParam("token", token);
        rp.setParam("appname", "myapp");
        rp.setParam("server_ip", "0.0.0.0");
        rp.setParam("sessname", "sessname");
        rp.setParam("action", "start");
        rp.setParam("hls", "true");

        RunStreamAsyncTask task = new RunStreamAsyncTask();
        task.execute(rp);
        pd.show();
    }

    /**
     * Async task Start Vcam Streaming
     */
    public class RunStreamAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp","runStreamAsyncTask Replay: " + s);
            showIfStreamExists();
        }
    }

    /**
     * REST if straem Exist
     */
    public void showIfStreamExists() {
        Log.d("MyApp", "showIfStreamExists url: " + mStreamURL);
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "php/functions/showIfStreamExists.php");
        rp.setMethod("POST");
        rp.setParam("url", mStreamURL);

        ShowIfStreamExistsTask task = new ShowIfStreamExistsTask();
        task.execute(rp);
    }
    /**
     * Async task Start Vcam Streaming
     */
    public class ShowIfStreamExistsTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String replay = HTTPManager.getData(params[0]);
            return replay;
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp","ShowIfStreamExistsTask Replay: " + s);
            //if(s.equals("\"200\"")) {
                Log.d("MyApp", "Sream Ready");
                getInit();
            //}
        }
    }

}
