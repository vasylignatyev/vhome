package ua.com.vi_port.vhome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AlertDialog extends DialogFragment implements View.OnClickListener {

    public AlertDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().setTitle("Title!");
        View v = inflater.inflate(R.layout.dialog_alert, null);
        v.findViewById(R.id.btnYes).setOnClickListener(this);
        v.findViewById(R.id.btnNo).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnYes :
                Log.d("MyApp", "Yes");
                onYes();
                break;

            case R.id.btnNo :
                Log.d("MyApp", "No");
                onCancel();
                break;
        }
        dismiss();
    }
    private void onYes() {

    }
    private void onCancel() {

    }
}
