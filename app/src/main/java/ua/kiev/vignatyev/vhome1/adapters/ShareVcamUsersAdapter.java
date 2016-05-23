package ua.kiev.vignatyev.vhome1.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import ua.kiev.vignatyev.vhome1.R;
import ua.kiev.vignatyev.vhome1.models.ShareUser;

/**
 * Created by vignatyev on 22.05.2016.
 */

public class ShareVcamUsersAdapter extends ArrayAdapter <ShareUser> {

    private Context context;
    private List<ShareUser> mShareUser;

    private static final SimpleDateFormat mDisplayDate = new SimpleDateFormat("HH:mm dd/MM/yy");

    private OnAdapterInteractionListener mListener;

    public ShareVcamUsersAdapter(Context context, int resource, List<ShareUser> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mShareUser = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_share_vcam, parent, false);
        }
        final ShareUser shareUser = mShareUser.get(position);


        TextView tvUserEmail = (TextView) convertView.findViewById(R.id.tvUserEmail);
        tvUserEmail.setText(shareUser.NAME);

        TextView tvType = (TextView) convertView.findViewById(R.id.tvType);
        tvType.setText( shareUser.TYPE.equalsIgnoreCase("customer") ? "Пользователь" : "Группа");

        TextView tvExpirationDate = (TextView) convertView.findViewById(R.id.tvExpirationDate);
        tvExpirationDate.setText((shareUser.EXPIRATION==null)?"не ограничен":mDisplayDate.format(shareUser.EXPIRATION));

        CheckBox cbOnline = (CheckBox) convertView.findViewById(R.id.cbOnline);
        CheckBox cbArchive = (CheckBox) convertView.findViewById(R.id.cbArchive);
        CheckBox cbAlerts = (CheckBox) convertView.findViewById(R.id.cbAlerts);
        cbOnline.setChecked((shareUser.RESTRICTION & 1) != 0);
        cbArchive.setChecked((shareUser.RESTRICTION & 2) != 0);
        cbAlerts.setChecked((shareUser.RESTRICTION & 4) != 0);

        LinearLayout llImageButton = (LinearLayout) convertView.findViewById(R.id.llImageButton);
        llImageButton.setTag(shareUser.NAME);

        ImageButton ibExpiration = (ImageButton) convertView.findViewById(R.id.ibExpiration);
        ImageButton ibSchedule = (ImageButton) convertView.findViewById(R.id.ibSchedule);
        ImageButton ibDelete = (ImageButton) convertView.findViewById(R.id.ibDelete);

        ibExpiration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null)
                    mListener.onExpireBtnClick(v, shareUser.TYPE, shareUser.NAME);
            }
        });
        ibSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                if(mListener != null)
                    mListener.onScheduleBtnClick(v, shareUser.TYPE, shareUser.NAME);
            }
        });
        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null)
                    mListener.onDeleteBtnClick(v, shareUser.TYPE, shareUser.NAME);
            }
        });

        return convertView;
    }

    public interface OnAdapterInteractionListener {
        void onExpireBtnClick(View view, String type, String name);
        void onScheduleBtnClick(View view, String type, String name);
        void onDeleteBtnClick(View view, String type, String name);
    }
    public void setOnAdapterInteractionListener(OnAdapterInteractionListener listener) {
        mListener = listener;
    }

}