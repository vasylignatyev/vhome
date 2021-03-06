package ua.com.vi_port.vhome;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import ua.com.vi_port.vhome.adapters.ShareVcamUsersAdapter;
import ua.com.vi_port.vhome.ajax.HTTPManager;
import ua.com.vi_port.vhome.ajax.RequestPackage;
import ua.com.vi_port.vhome.models.ShareVcamUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShareVcamUserFragment extends Fragment
        implements AddShareVcamUserDialog.OnInteraction {

    public static final String VCAM_TOKEN = "vcam_token";
    public static final String USER_TOKEN = "user_token";

    private String mVcamToken;
    private String mUserToken;

    //********* owner info
    private int mICustomer;
    private String mEMail;
    //********* VCam info
    private int mICustomerVcam;
    private String mVcamName;
    //*** VIEWS
    private TextView tvOwnerEmail;
    private ListView lvSharedUsers;

    private Context context;
    private ShareVcamUserActivity mShareVcamUserActivity;

    private ArrayList<ShareVcamUser> mShareVcamUserArrayList = new ArrayList<>();
    private ArrayList<ShareVcamUser> mDeletedShareVcamUserArrayList = new ArrayList<>();

    private ShareVcamUserFragment mShareVcamUserFragment = this;

    private static final SimpleDateFormat mMysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

     public ShareVcamUserFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ShareVcamUserActivity) {
            mShareVcamUserActivity = (ShareVcamUserActivity) context;
        }
        this.context = context;
    }

    public static ShareVcamUserFragment newInstance(String vcamToken, String userToken) {
        ShareVcamUserFragment fragment = new ShareVcamUserFragment();
        Bundle args = new Bundle();
        args.putString( VCAM_TOKEN, vcamToken);
        args.putString( USER_TOKEN, userToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_share_vcam, container, false);

        if (getArguments() != null) {
            mVcamToken = getArguments().getString(VCAM_TOKEN, null);
            mUserToken = getArguments().getString(USER_TOKEN, null);
        }

        tvOwnerEmail = (TextView) v.findViewById(R.id.tvOwnerEmail);
        lvSharedUsers = (ListView) v.findViewById(R.id.lvSharedUsers);

        Button btSaveShareUser = (Button) v.findViewById(R.id.btSaveShareUser);
        btSaveShareUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccess();
            }
        });
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        Button btAddShareUser = (Button) v.findViewById(R.id.btAddShareUser);
        btAddShareUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.FragmentManager fm = getFragmentManager();
                AddShareVcamUserDialog addShareVcamUserDialog = AddShareVcamUserDialog.newInstance( mICustomerVcam, mUserToken);
                addShareVcamUserDialog.setListener( ShareVcamUserFragment.this );
                addShareVcamUserDialog.show(fm, "TEST");

            }
        });
        getVcamShareCustomers(mVcamToken, mUserToken);

        return v;
    }

    private void  saveAccess(){
        ShareVcamUser problemUser = null;
        for (ShareVcamUser shareVcamUser: mShareVcamUserArrayList) {
            if( shareVcamUser.RESTRICTION == 0 ) {
                problemUser = shareVcamUser;
                break;
            }
        }
        if(problemUser == null ) {
            for (ShareVcamUser shareVcamUser : mShareVcamUserArrayList) {
                addAccess(shareVcamUser);
            }
            for (ShareVcamUser shareVcamUser : mDeletedShareVcamUserArrayList) {
                delAccess(shareVcamUser);
            }
            getActivity().finish();
        } else {
            String msg = "Пользователь: \"" + problemUser.NAME + "\" не имеет прав доступа. Добавте права";
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void setShareVcamUser(ShareVcamUser shareVcamUser) {
        mShareVcamUserArrayList.add(shareVcamUser);
        lvSharedUsers.deferNotifyDataSetChanged();
    }

     /**
     * REST Request for Hash String
     */
private void addAccess(ShareVcamUser shareVcamUser){
    RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
    rp.setMethod("GET");
    rp.setParam("functionName", "addAccess");
    rp.setParam("cam_token", mVcamToken);
    rp.setParam("user_token", mUserToken);
    rp.setParam("access_name", shareVcamUser.NAME);
    rp.setParam("type", "insert");
    rp.setParam("restriction", Integer.toString(shareVcamUser.RESTRICTION));
    if(shareVcamUser.EXPIRATION != null) {
        rp.setParam("expiration", mMysqlDateFormat.format(shareVcamUser.EXPIRATION));
    }
    if(shareVcamUser.SCHEDULE != null) {
        rp.setParam("schedule", shareVcamUser.SCHEDULE);
    }
    addAccessAsyncTask task = new addAccessAsyncTask();
    task.execute(rp);
}

    public class addAccessAsyncTask extends AsyncTask<RequestPackage, Void, String> {

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "addAccessAsyncTask:" + s);

        }
    }
    //******************************************
     private void delAccess(ShareVcamUser shareVcamUser){
         RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
         rp.setMethod("GET");
         rp.setParam("functionName", "deleteCustomerAccess");
         rp.setParam("cam_token", mVcamToken);
         rp.setParam("user_token", mUserToken);
         rp.setParam("access_name", shareVcamUser.NAME);
         //rp.setParam("restriction", Integer.toString(shareVcamUser.RESTRICTION));
         new delAccessAsyncTask().execute(rp);

     }
    public class delAccessAsyncTask extends AsyncTask<RequestPackage, Void, String> {

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "delAccessAsyncTask:" + s);
        }
    }
    //********************************************
    private void getVcamShareCustomers(String camToken, String userToken) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getShareVcamCustomers");
        rp.setParam("cam_token", camToken);
        rp.setParam("user_token", userToken);

        getVcamShareCustomersAsyncTask task = new getVcamShareCustomersAsyncTask(camToken);
        task.execute(rp);
    }


    public class getVcamShareCustomersAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        private String camToken;

        public getVcamShareCustomersAsyncTask(String camToken) {
            this.camToken = camToken;
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getVcamShareCustomers:" + s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.has("owner")){
                    JSONObject owner = jsonObject.getJSONObject("owner");
                    if(owner.has("EMAIL")) {
                        mEMail = owner.getString("EMAIL");
                        tvOwnerEmail.setText(mEMail);
                    }
                    if(owner.has("NAME")) {
                        mVcamName = owner.getString("NAME");
                    }
                    if(owner.has("I_CUSTOMER")) {
                        mICustomer = owner.getInt("I_CUSTOMER");
                    }
                    if(owner.has("I_CUSTOMER_VCAM")) {
                        mICustomerVcam = owner.getInt("I_CUSTOMER_VCAM");
                    }
                }
                if(jsonObject.has("share_list")) {
                    Log.d("MyApp", "has share_list");

                    mShareVcamUserArrayList = new ArrayList<>();

                    JSONArray shareUserArray = jsonObject.getJSONArray("share_list");
                    for(int i = 0 ; i < shareUserArray.length() ; i++ ) {
                        ShareVcamUser shareUser = new ShareVcamUser();

                        JSONObject shareUserObject = shareUserArray.getJSONObject(i);
                        if(shareUserObject.has("RESTRICTION")) {
                            shareUser.RESTRICTION = shareUserObject.getInt("RESTRICTION");
                        }
                        if(shareUserObject.has("SCHEDULE")) {
                            String str = shareUserObject.getString("SCHEDULE");
                            if((str != null) && (!str.equals("null"))) {
                                shareUser.SCHEDULE = str;
                            }
                        }
                        if(shareUserObject.has("EXPIRATION")) {
                            String dateStr = shareUserObject.getString("EXPIRATION");
                            if((dateStr != null) && (!dateStr.equals("null"))) {
                                shareUser.EXPIRATION = mMysqlDateFormat.parse(dateStr);
                            }
                        }
                        if(shareUserObject.has("ISSUE_DATE")) {
                            String dateStr = shareUserObject.getString("ISSUE_DATE");
                            if((dateStr != null) && (!dateStr.equals("null"))) {
                                shareUser.ISSUE_DATE = mMysqlDateFormat.parse(dateStr);
                            }
                        }
                        if(shareUserObject.has("NAME")) {
                            shareUser.NAME = shareUserObject.getString("NAME");
                        }
                        if(shareUserObject.has("TYPE")) {
                            shareUser.TYPE = shareUserObject.getString("TYPE");
                        }
                        mShareVcamUserArrayList.add(shareUser);
                    }
                    Log.d("MyApp", "mShareVcamUserArrayList size: " + Arrays.asList(mShareVcamUserArrayList.toArray()));
                    ShareVcamUsersAdapter shareVcamUsersAdapter =
                            new ShareVcamUsersAdapter(getActivity(), R.layout.item_share_vcam, mShareVcamUserArrayList, mDeletedShareVcamUserArrayList);

                    lvSharedUsers.setAdapter(shareVcamUsersAdapter);
                }
            } catch (JSONException | ParseException e) {
                Log.d("MyApp", "Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
