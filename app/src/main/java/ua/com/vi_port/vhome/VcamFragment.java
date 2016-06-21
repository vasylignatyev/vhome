package ua.com.vi_port.vhome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ua.com.vi_port.vhome.adapters.VcamArrayAdapter;
import ua.com.vi_port.vhome.ajax.HTTPManager;
import ua.com.vi_port.vhome.ajax.RequestPackage;
import ua.com.vi_port.vhome.models.Vcam;
import ua.com.vi_port.vhome.parsers.VcamListParser;

public class VcamFragment extends Fragment
        implements VcamArrayAdapter.OnAdapterInteractionListener {
        //implements AbsListView.OnItemClickListener, VcamArrayAdapter.OnAdapterInteractionListener {
    /**
     * STATIC VAR
     */
    private static final String USER_TOKEN = "user_token";
    private static final String TAG = "VcamFragment";
    /**
     * VARS
     */
    private ProgressDialog pd;
    private String mUserToken;
    private ListView mListView = null;
    private String mStreamURL;
    private int mVcamPosition;
    private List<Vcam> mVcamList;
    private MainActivity mMainActivity = null;

    ua.com.vi_port.vhome.AlertDialog mAlertDialog;

    /**
     *
     */
    public VcamFragment() {
    }

    public static VcamFragment newInstance(String userToken) {
        VcamFragment fragment = new VcamFragment();
        Bundle args = new Bundle();
        args.putString(USER_TOKEN, userToken);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * LIFE CICLE
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if( context instanceof MainActivity) {
            mMainActivity = (MainActivity) context;
            mMainActivity.getActionBar().setTitle(getResources().getString(R.string.my_cameras));
        }

        pd = new ProgressDialog(context);
        pd.setTitle("Подключение к серверу");
        pd.setMessage("Ожидайте");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserToken = getArguments().getString(USER_TOKEN, null);
        }

        mAlertDialog = new ua.com.vi_port.vhome.AlertDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vcam_list, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        //mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState == null) {
            //getCustomerVCamList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MyApp", "VcamFragment::onResume");
        getCustomerVCamList();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MyApp", "VcamFragment::onPause");
    }

    /**
     * SERVICE FUNCTION
     */
    public void updateDisplay(){
        VcamArrayAdapter vcamArrayAdapter = new VcamArrayAdapter( mMainActivity, R.layout.item_vcam, mVcamList);
        vcamArrayAdapter.setOnAdapterInteractionListener(this);
        if (mListView != null) {
            mListView.setAdapter(vcamArrayAdapter);
        }
    }

    /**
     * IMPLEMENTED

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mVcamPosition = position;
        if(mVcamList != null && (mVcamList.size() > position) ) {
            Vcam vcam = mVcamList.get(position);
            mStreamURL = vcam.getVcamURL();
            getHashString(vcam.getTOKEN());
        }
    }
     */
    //video Archive Button Click
    @Override
    public void onArchButtonClick(View v) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment newFragment = VarchPlayerFragment.newInstance(mUserToken, v.getTag().toString());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(TAG);
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    public void onConfigButtonClick(View view) {
        String vcamToken = view.getTag().toString();
        Intent intent = new Intent(mMainActivity, VcamSetupActivity.class);
        intent.putExtra(VcamSetupActivity.VCAM_TOKEN, vcamToken);
        intent.putExtra(VcamSetupActivity.USER_TOKEN, mUserToken);
        startActivity(intent);
    }

    @Override
    public void onScheduleButtonClick(View view) {
        String vcamToken = view.getTag().toString();
        Intent intent = new Intent(mMainActivity, ScheduleActivity.class);
        intent.putExtra(ScheduleActivity.VCAM_TOKEN, vcamToken);
        intent.putExtra(ScheduleActivity.USER_TOKEN, mUserToken);
        startActivity(intent);
    }

    @Override
    public void onShareButtonClick(View view) {
        String vcamToken = view.getTag().toString();
        Intent intent = new Intent(mMainActivity, ShareVcamUserActivity.class);
        intent.putExtra(ShareVcamUserActivity.VCAM_TOKEN, vcamToken);
        intent.putExtra(ShareVcamUserActivity.USER_TOKEN, mUserToken);
        startActivity(intent);
    }

    @Override
    public void onRecordButtonClick(int result) {

        if( result == 1) {
            Toast.makeText(mMainActivity,
                    "Запись по требованию включена. Длительность записи 60 минут",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mMainActivity,
                    "Запись по требованию отключена.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteButtonClick(View view) {
        /*
        mAlertDialog.show(getFragmentManager(), "Alert");
        */
    }



    @Override
    public void onPlayClick(View v, int pos) {
        mVcamPosition = pos;

        if(mVcamList != null && (mVcamList.size() > pos) ) {
            Vcam vcam = mVcamList.get(pos);
            mStreamURL = vcam.getVcamURL();
            getHashString(vcam.getTOKEN());
        }
    }

    /**
     * REST Request for Vcam List
     */
    private void getCustomerVCamList() {

        pd.show();
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getCustomerVcamList");
        rp.setParam("customer_token", mUserToken);

        new VcamFragment.getCustomerVCamListAsyncTask().execute(rp);
    }
    private class getCustomerVCamListAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            if(s == null) {
                mMainActivity.shitHappens();
                return;
            }
            Log.d("MyApp", "getCustomerVCamList: " + s);
            mVcamList = VcamListParser.parseFeed(s);

            if(mVcamList != null ) {
                updateDisplay();
            }
            pd.hide();
        }
    }

    private void getHashString(String camToken) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "get_hash_string");
        rp.setParam("token", mUserToken);
        rp.setParam("cam_token", camToken);

        getHashStringAsyncTask task = new getHashStringAsyncTask(camToken);
        task.execute(rp);

    }
    private class getHashStringAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        private String camToken;

        public getHashStringAsyncTask(String camToken) {
            this.camToken = camToken;
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getHashString:" + s);

            if((s == null))
                return;

            JSONObject obj;
            String hash_string;
            try {
                obj = new JSONObject(s);
                if(obj.has("hash_string")) {
                    hash_string = obj.getString("hash_string");
                    mStreamURL += "?" + hash_string;
                }

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                Fragment newFragment = VcamPlayerFragment.newInstance(mStreamURL, mVcamPosition,camToken);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                transaction.replace(R.id.container, newFragment, VcamPlayerFragment.TAG);
                transaction.addToBackStack(null);
                transaction.commit();
                fragmentManager.executePendingTransactions();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
