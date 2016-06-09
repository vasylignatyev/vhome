package ua.com.vi_port.vhome.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import ua.com.vi_port.vhome.DatePickerFragment;
import ua.com.vi_port.vhome.R;
import ua.com.vi_port.vhome.ShareScheduleActivity;
import ua.com.vi_port.vhome.models.ShareVcamUser;

public class ShareVcamUsersAdapter extends ArrayAdapter <ShareVcamUser>
        implements DatePickerFragment.OnPickerInteractionListener {

    private static final SimpleDateFormat mDisplayDate = new SimpleDateFormat("dd.MMM.yy");

    private Context mContext;
    private final List<ShareVcamUser> mShareVcamUserList;
    private final List<ShareVcamUser> mDeletedShareVcamUserList;

    public ShareVcamUsersAdapter(Context context, int resource, final List<ShareVcamUser> objects,
                                 final List<ShareVcamUser> deletedObjects) {
        super(context, resource, objects);
        mContext = context;
        mShareVcamUserList = objects;
        mDeletedShareVcamUserList = deletedObjects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_share_vcam, parent, false);
        }
        final ShareVcamUser shareVcamUser = mShareVcamUserList.get(position);


        TextView tvUserEmail = (TextView) convertView.findViewById(R.id.tvUserEmail);
        tvUserEmail.setText(shareVcamUser.NAME);

        TextView tvType = (TextView) convertView.findViewById(R.id.tvType);
        tvType.setText( shareVcamUser.TYPE.equalsIgnoreCase("customer") ? "Пользователь" : "Группа");

        final TextView tvExpirationDate = (TextView) convertView.findViewById(R.id.tvExpirationDate);
        tvExpirationDate.setText((shareVcamUser.EXPIRATION==null)?"не ограничен":mDisplayDate.format(shareVcamUser.EXPIRATION));

        CheckBox cbOnline = (CheckBox) convertView.findViewById(R.id.cbOnline);
        CheckBox cbArchive = (CheckBox) convertView.findViewById(R.id.cbArchive);
        CheckBox cbAlerts = (CheckBox) convertView.findViewById(R.id.cbAlerts);
        cbOnline.setChecked((shareVcamUser.RESTRICTION & 1) != 0);
        cbArchive.setChecked((shareVcamUser.RESTRICTION & 2) != 0);
        cbAlerts.setChecked((shareVcamUser.RESTRICTION & 4) != 0);
        cbOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    shareVcamUser.RESTRICTION |=  1;
                } else {
                    shareVcamUser.RESTRICTION &=  (~1);
                }
            }
        });
        cbArchive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    shareVcamUser.RESTRICTION |=  2;
                } else {
                    shareVcamUser.RESTRICTION &=  (~2);
                }
            }
        });
        cbAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    shareVcamUser.RESTRICTION |=  4;
                } else {
                    shareVcamUser.RESTRICTION &=  (~4);
                }
            }
        });

        LinearLayout llImageButton = (LinearLayout) convertView.findViewById(R.id.llImageButton);
        llImageButton.setTag(shareVcamUser.NAME);

        ImageButton ibExpiration = (ImageButton) convertView.findViewById(R.id.ibExpiration);
        ImageButton ibSchedule = (ImageButton) convertView.findViewById(R.id.ibSchedule);
        ImageButton ibDelete = (ImageButton) convertView.findViewById(R.id.ibDelete);

        ibExpiration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                Bundle arguments = new Bundle();
                final String currentExpireDate = tvExpirationDate.getText().toString();
                if(currentExpireDate.length()>0) {
                    try {
                        arguments.putLong( DatePickerFragment.TIME_PARAM, mDisplayDate.parse(currentExpireDate).getTime());
                    } catch (ParseException e) {
                        arguments.putLong( DatePickerFragment.TIME_PARAM, 0);
                    }
                }
                datePickerFragment.setArguments(arguments);
                datePickerFragment.setListener( ShareVcamUsersAdapter.this );
                datePickerFragment.setShareVcamUser(shareVcamUser);
                android.app.FragmentManager fm = ((Activity) mContext).getFragmentManager();
                datePickerFragment.show( fm, "EXPIRATION_DATE");
            }
        });
        ibSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {

                Intent intent = new Intent(mContext, ShareScheduleActivity.class);
                intent.putExtra(ShareScheduleActivity.SCHEDULE , shareVcamUser.SCHEDULE);
                ((Activity) mContext).startActivity(intent);
                ShareScheduleActivity.setShareVcamUser(shareVcamUser);
           }
        });
        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareVcamUserList.remove(shareVcamUser);
                mDeletedShareVcamUserList.add(shareVcamUser);
                notifyDataSetChanged();
             }
        });

        return convertView;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        notifyDataSetChanged();
        return ;
    }


}
