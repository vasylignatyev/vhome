package ua.com.vi_port.vhome;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class PinchActivity extends FragmentActivity implements PinchFragment.OnFragmentInteractionListener {
    /**
     * STATIC VARS
     */
    public static final String ARG_IMAGE_URL = "image_url";

    /**
     * Private VARS
     */
    private String mImageUrl;
    @Override
    public String getImageUrl() {
        return mImageUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mImageUrl = extras.getString(ARG_IMAGE_URL, null);
            Log.d("MyApp", "PinchActivity " + PinchActivity.ARG_IMAGE_URL + ": " + mImageUrl);
        }
        setContentView(R.layout.activity_pinch);
    }

}
