package ua.kiev.vignatyev.vhome1;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AddShareVcamUserDialog extends DialogFragment {


    public AddShareVcamUserDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add_share_vcam_user_dialog, container, false);

        return v;
    }

}
