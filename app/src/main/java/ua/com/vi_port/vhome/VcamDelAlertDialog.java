package ua.com.vi_port.vhome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VcamDelAlertDialog extends DialogFragment implements View.OnClickListener {

    public static String VCAM_TOKEN = "vcam_token";

    private AlertDialogInterface listener = null;

    public void setListener(AlertDialogInterface listener) {
        this.listener = listener;
    }

    private String mVcamToken;

    public VcamDelAlertDialog() {
    }

    public static VcamDelAlertDialog newInstance( String vcam_token ) {
        VcamDelAlertDialog f = new VcamDelAlertDialog();
        Bundle args = new Bundle();
        args.putString(VCAM_TOKEN, vcam_token);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView( inflater,  container,  savedInstanceState);

        getDialog().setTitle(R.string.vcam_delete);
        View v = inflater.inflate(R.layout.dialog_alert, null);
        v.findViewById(R.id.btnYes).setOnClickListener(this);
        v.findViewById(R.id.btnNo).setOnClickListener(this);

        Bundle args = getArguments();
        mVcamToken = args.getString(VCAM_TOKEN, null);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnYes :
                onConfirm();
                break;
            case R.id.btnNo :
                onCancel();
                break;
        }
        dismiss();
    }
    private void onConfirm() {
        if(listener != null) {
            listener.onConfirm(mVcamToken);
        }
    }
    private void onCancel() {
        if(listener != null) {
            listener.onCancel(mVcamToken);
        }
    }
    interface AlertDialogInterface {
        void onConfirm(String vcam_token);
        void onCancel(String vcam_token);
    }
}
