package ua.com.vi_port.vhome;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



public class NoNetworkFragment extends Fragment {

    private static MainActivity mMainActivity = null;

    public  NoNetworkFragment(){}

    public static NoNetworkFragment createInstance(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        return new NoNetworkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_no_network, container, false);

        Button btCheckConnection = (Button) view.findViewById(R.id.btCheckConnection);

        btCheckConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMainActivity != null) {
                    mMainActivity.onNavigationDrawerItemSelected(0);
                }
            }
        });
        return view;
    }
}
