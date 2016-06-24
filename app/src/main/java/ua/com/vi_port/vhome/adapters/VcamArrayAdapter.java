package ua.com.vi_port.vhome.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import ua.com.vi_port.vhome.MainActivity;
import ua.com.vi_port.vhome.R;
import ua.com.vi_port.vhome.ajax.HTTPManager;
import ua.com.vi_port.vhome.ajax.RequestPackage;
import ua.com.vi_port.vhome.models.Vcam;

/**
 * Created by vignatyev on 01.07.2015.
 */
public class VcamArrayAdapter extends ArrayAdapter<Vcam> {
    private Context context;
    private static String THUMB_NAIL_URL = MainActivity.SERVER_URL + "vcam_thumbnail/";

    private List<Vcam> vcamList;
    private ProgressDialog pd;

    private static class ViewHolder {
        TextView vcamName;
        TextView tvVcamLocation;
        ImageButton ibDelete;
        ImageButton ibConfig;
        ImageButton ibCalendar;
        ImageButton ibShare;
        ImageButton ibRecord;
        ImageButton ibArchive;
        ImageView ivThumb;
        TextView tvOnline;
        TextView tvRecord;
    }

    public VcamArrayAdapter(Context context, int resource, List<Vcam> objects) {
        super(context, resource, objects);
        this.context = context;
        this.vcamList = objects;

        pd = new ProgressDialog(context);
        pd.setTitle("Подключение к серверу");
        pd.setMessage("Ожидайте");
    }

    public interface OnAdapterInteractionListener {
        void onArchButtonClick(View view);
        void onConfigButtonClick(View view);
        void onScheduleButtonClick(View view);
        void onShareButtonClick(View view);
        void onRecordButtonClick(int result);
        void onDeleteButtonClick(View view);
        void onPlayClick(View v, int pos);
        String getCustomerToken();
    }

    private OnAdapterInteractionListener listener;

    public void setOnAdapterInteractionListener(OnAdapterInteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.item_vcam, parent, false);

            viewHolder.vcamName = (TextView) convertView.findViewById(R.id.vcamName);
            viewHolder.tvVcamLocation = (TextView) convertView.findViewById(R.id.vcamLocation);
            viewHolder.ibDelete = (ImageButton) convertView.findViewById(R.id.ibDelete);
            viewHolder.ibConfig = (ImageButton) convertView.findViewById(R.id.ibConfig);
            viewHolder.ibCalendar = (ImageButton) convertView.findViewById(R.id.ibCalendar);
            viewHolder.ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
            viewHolder.ibRecord = (ImageButton) convertView.findViewById(R.id.ibRecord);
            viewHolder.ibArchive = (ImageButton) convertView.findViewById(R.id.ibArchive);
            viewHolder.ivThumb = (ImageView)  convertView.findViewById(R.id.ivThumb);
            viewHolder.tvOnline = (TextView) convertView.findViewById(R.id.tvOnline);
            viewHolder.tvRecord = (TextView) convertView.findViewById(R.id.tvRecord);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Vcam vcam = vcamList.get(position);

        viewHolder.vcamName.setText(vcam.CUSTOMER_VCAM_NAME);
        viewHolder.tvVcamLocation.setText(vcam.VCAM_LOCATION);

        viewHolder.ibDelete.setTag(vcam.TOKEN);
        viewHolder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteButtonClick(v);
                }
            }
        });
        viewHolder.ibConfig.setTag(vcam.TOKEN);
        viewHolder.ibConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfigButtonClick(v);
                }
            }
        });
        viewHolder.ibCalendar.setTag(vcam.TOKEN);
        viewHolder.ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onScheduleButtonClick(v);
                }
            }
        });
        viewHolder.ibShare.setTag(vcam.TOKEN);
        viewHolder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShareButtonClick(v);
                }
            }
        });
        viewHolder.ibShare.setTag(vcam.TOKEN);
        viewHolder.ibRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordControl( vcam.TOKEN, "session");

            }
        });
        viewHolder.ibArchive.setTag(vcam.TOKEN);
        viewHolder.ibArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onArchButtonClick(v);
                }
            }
        });

        if( vcam.TYPE.equalsIgnoreCase("OWNER")) {
            viewHolder.ibConfig.setVisibility(View.VISIBLE);
            viewHolder.ibCalendar.setVisibility(View.VISIBLE);
            viewHolder.ibShare.setVisibility(View.VISIBLE);
            viewHolder.ibRecord.setVisibility(View.VISIBLE);
            viewHolder.ibArchive.setVisibility(View.VISIBLE);
            viewHolder.ibDelete.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ibArchive.setVisibility(View.VISIBLE);
        }

        if(vcam.THUMBNAIL != null) {
            viewHolder.ivThumb.setImageBitmap(vcam.THUMBNAIL);
        } else {
            final VcamAndThumb container = new VcamAndThumb(vcam, convertView);
            ThumbLoader loader = new ThumbLoader();
            loader.execute(container);
        }
        viewHolder.ivThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyApp", "On thumb click: " + position);
                if (listener != null) {
                    listener.onPlayClick(v, position);
                }
            }
        });
        if( vcam.ON_AIR == 4 ) {
            viewHolder.tvOnline.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvOnline.setVisibility(View.GONE);
        }
        if( (vcam.ROS != 0) || (vcam.ROD != 0) ) {
            viewHolder.tvRecord.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvRecord.setVisibility(View.GONE);
        }

        vcam.timer = new Timer();
        vcam.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(listener != null) {
                    getVCamStatus(listener.getCustomerToken(), vcam.TOKEN);
                }
            }
        }, 10000L, 10000L);


        convertView.setTag(position);

        return convertView;
    }

    @Override
    public void clear() {
        Log.d("MyApp", "VcamArrayAdapter::clear");
        for(Vcam vcam : vcamList ) {
            if(vcam.timer != null) {
                vcam.timer.cancel();
                vcam.timer = null;
            }
        }
        super.clear();
    }


    private class VcamAndThumb {
        public VcamAndThumb(Vcam vcam, View view) {
            this.vcam = vcam;
            this.view = view;
        }

        public Vcam vcam;
        public View view;
        public Bitmap thumb;

    }
    private class ThumbLoader extends AsyncTask<VcamAndThumb, Void, VcamAndThumb> {

        @Override
        protected VcamAndThumb doInBackground(VcamAndThumb... params) {

            Log.d("MyApp", "ThumbLoader");

            VcamAndThumb container = params[0];
            Vcam vcam = container.vcam;
            InputStream in;
            try{
                String thumbNailUrl = THUMB_NAIL_URL + vcam.TOKEN + ".jpg";
                Log.d("MyApp", "thumbNailUrl: " +thumbNailUrl);
                in = (InputStream) new URL(thumbNailUrl).getContent();
                vcam.THUMBNAIL = BitmapFactory.decodeStream(in);
                container.thumb = vcam.THUMBNAIL;
                in.close();
                return container;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(VcamAndThumb vcamAndThumb) {
            if( vcamAndThumb!=null ) {
                ImageView ivThumb = (ImageView) vcamAndThumb.view.findViewById(R.id.ivThumb);
                ivThumb.setImageBitmap(vcamAndThumb.thumb);
                vcamAndThumb.vcam.THUMBNAIL = vcamAndThumb.thumb;
            }
        }
    }

    /**
     * REST Request
     */
    private void recordControl(String cam_token, String sessname) {

        pd.show();
        //************************
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "recordControl");
        rp.setParam("cam_token", cam_token);
        rp.setParam( "sessname", sessname);

        new recordControlAsyncTask().execute(rp);
    }
    private class recordControlAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            pd.hide();

            if(s == null) {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.has("result")) {
                    int result = jsonObject.getInt("result");
                    if( listener != null ) {
                        listener.onRecordButtonClick(result);
                    }
                }
            } catch (JSONException e) {
                Log.d("MyApp", "recordControl JSON error: " + e.getMessage());
            }
        }
    }
    private void getVCamStatus(String user_token, String cam_token) {
        RequestPackage rp = new RequestPackage(MainActivity.SERVER_URL + "ajax/ajax.php");
        rp.setMethod("GET");
        rp.setParam("functionName", "getVCamStatus");
        rp.setParam("cam_token", cam_token);
        rp.setParam( "user_token", user_token);

        new getVCamStatusAsyncTask().execute(rp);
    }
    private class getVCamStatusAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getVCamStatus: " + s);
        }
    }
}
