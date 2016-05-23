package ua.kiev.vignatyev.vhome1;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ua.kiev.vignatyev.vhome1.adapters.ShareVcamUsersAdapter;
import ua.kiev.vignatyev.vhome1.ajax.HTTPManager;
import ua.kiev.vignatyev.vhome1.ajax.RequestPackage;
import ua.kiev.vignatyev.vhome1.models.ShareUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShareVcamFragment extends Fragment
    implements ShareVcamUsersAdapter.OnAdapterInteractionListener {

    public static final String VCAM_TOKEN = "vcam_token";
    public static final String USER_TOKEN = "user_token";

    private String mVcamToken;
    private String mUserToken;

    private TextView tvOwnerEmail;
    private ListView lvSharedUsers;

    private Context context;

    private ShareVcamFragment mShareVcamFragment = this;

    private static final SimpleDateFormat mMysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public ShareVcamFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static ShareVcamFragment newInstance(String vcamToken, String userToken) {
        ShareVcamFragment fragment = new ShareVcamFragment();
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

        getVcamShareCustomers(mVcamToken, mUserToken);

        return v;
    }

    /**
     *
     * Adapter interface methods
     */

    @Override
    public void onExpireBtnClick(View view, String type, String name) {
        Log.d("MyApp", "onExpireBtnClick type:" + type + " name:" + name);
    }

    @Override
    public void onScheduleBtnClick(View view, String type, String name) {
        Log.d("MyApp", "onScheduleBtnClick type:" + type + " name:" + name);
    }

    @Override
    public void onDeleteBtnClick(View view, String type, String name) {
        Log.d("MyApp", "onDeleteBtnClick type:" + type + " name:" + name);
    }
    /**
     * REST Request for Hash String
     */
    private void getVcamShareCustomers(String camToken, String userToken) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getVcamShareCustomers");
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
                        tvOwnerEmail.setText(owner.getString("EMAIL"));
                    }
                }
                if(jsonObject.has("share_list")) {
                    Log.d("MyApp", "has share_list");
                    ArrayList<ShareUser> shareUserArrayList = new ArrayList<>();

                    JSONArray shareUserArray = jsonObject.getJSONArray("share_list");
                    for(int i = 0 ; i < shareUserArray.length() ; i++ ) {
                        ShareUser shareUser = new ShareUser();

                        JSONObject shareUserObject = shareUserArray.getJSONObject(i);
                        if(shareUserObject.has("RESTRICTION")) {
                            shareUser.RESTRICTION = shareUserObject.getInt("RESTRICTION");
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
                        shareUserArrayList.add(shareUser);
                    }
                    Log.d("MyApp", "shareUserArrayList size: " + shareUserArrayList.size());
                    ShareVcamUsersAdapter shareVcamUsersAdapter = new ShareVcamUsersAdapter(getActivity(), R.layout.item_share_vcam, shareUserArrayList);
                    shareVcamUsersAdapter.setOnAdapterInteractionListener(mShareVcamFragment);
                    if(lvSharedUsers != null) {
                        lvSharedUsers.setAdapter(shareVcamUsersAdapter);
                    }
                }
            } catch (JSONException | ParseException e) {
                Log.d("MyApp", "Exception: " + e.getMessage());
                e.printStackTrace();
            }


        }
    }
}
