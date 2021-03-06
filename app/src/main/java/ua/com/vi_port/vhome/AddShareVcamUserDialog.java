package ua.com.vi_port.vhome;


import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ua.com.vi_port.vhome.ajax.HTTPManager;
import ua.com.vi_port.vhome.ajax.RequestPackage;
import ua.com.vi_port.vhome.models.ShareVcamUser;


public class AddShareVcamUserDialog extends DialogFragment {

    public static final String I_CUSTOMER_VCAM = "i_customer_vcam";
    public static final String USER_TOKEN = "user_token";

    private final ShareVcamUser mShareVcamUser = new ShareVcamUser();

    private AutoCompleteTextView autoCompleteTextView;

    private int mICustomerVcam;
    private String mUserToken;

    private boolean mCloseDialog = false;

    private AddShareVcamUserDialog.OnInteraction mListener = null;

    public void setListener(AddShareVcamUserDialog.OnInteraction listener) {
        mListener = listener;
    }


    public interface OnInteraction {
        void setShareVcamUser(ShareVcamUser shareVcamUser);
    }

    public AddShareVcamUserDialog() {
    }

    public static AddShareVcamUserDialog newInstance(int mICustomerVcam, String userToken) {
        AddShareVcamUserDialog fragment = new AddShareVcamUserDialog();
        Bundle args = new Bundle();
        args.putInt( I_CUSTOMER_VCAM, mICustomerVcam);
        args.putString( USER_TOKEN, userToken);
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

        View v = inflater.inflate(R.layout.fragment_add_share_vcam_user_dialog, container, false);

        getDialog().setTitle("Добавить пользователя");

        if (getArguments() != null) {
            mICustomerVcam = getArguments().getInt(I_CUSTOMER_VCAM, 0);
            mUserToken = getArguments().getString(USER_TOKEN, null);
        }

        autoCompleteTextView = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("MyApp", "setOnItemClickListener: " + ((TextView) view).getText().toString() );
                if(mListener != null) {
                    mListener.setShareVcamUser(mShareVcamUser);
                }
                mCloseDialog = true;

            }
        });
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyApp", "setOnClickListener: " + ((AutoCompleteTextView) v).getText().toString() );
            }
        });
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("MyApp", "AutoCompleteTextView: " + s);
                if(s.length() > 1) {
                    findShareMember(s.toString());
                }
            }
        });
        return v;
    }

    /**
     * REST Request for Hash String
     */
    private void findShareMember(String searchStr) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "findShareMember");
        rp.setParam("search_str", searchStr);
        rp.setParam("user_token", mUserToken);
        rp.setParam("i_customer_vcam", Integer.toString(mICustomerVcam));

        findShareMemberAsyncTask task = new findShareMemberAsyncTask();
        task.execute(rp);
    }

    private class findShareMemberAsyncTask extends AsyncTask<RequestPackage, Void, String> {

        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "findShareMember:" + s);
            try {
                ArrayList<String> userList = new ArrayList<>();

                JSONArray jsonArray = new JSONArray(s);
                for(int i = 0 ; i < jsonArray.length() ; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(jsonObject.has("NAME")) {
                        mShareVcamUser.NAME = jsonObject.getString("NAME");
                        userList.add(mShareVcamUser.NAME);
                    }
                    if(jsonObject.has("TYPE")) {
                        mShareVcamUser.TYPE = jsonObject.getString("TYPE");
                    }
                }
                if(userList.size() > 0 ) {
                    ArrayAdapter<String> adapter;
                    adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, userList);
                    autoCompleteTextView.setAdapter(adapter);
                    autoCompleteTextView.setDropDownHeight(200);
                    autoCompleteTextView.showDropDown();
                }
                if(mCloseDialog) {
                    AddShareVcamUserDialog.this.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
