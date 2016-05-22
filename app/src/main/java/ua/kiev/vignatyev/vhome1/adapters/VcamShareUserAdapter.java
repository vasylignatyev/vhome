package ua.kiev.vignatyev.vhome1.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ua.kiev.vignatyev.vhome1.R;
import ua.kiev.vignatyev.vhome1.models.ShareUser;
import ua.kiev.vignatyev.vhome1.models.Varch;

/**
 * Created by vignatyev on 22.05.2016.
 */

public class VcamShareUserAdapter extends ArrayAdapter <ShareUser> {

    private Context context;
    private List<ShareUser> mShareUser;

    public VcamShareUserAdapter(Context context, int resource, List<ShareUser> objects) {
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
        ShareUser shareUser = mShareUser.get(position);

        TextView tvUserEmail = (TextView) convertView.findViewById(R.id.tvUserEmail);
        tvUserEmail.setText(shareUser.NAME);

        return super.getView(position, convertView, parent);
    }
 }
