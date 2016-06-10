package ua.com.vi_port.vhome.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import java.util.List;

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
    }

    private OnAdapterInteractionListener listener;

    public void setOnAdapterInteractionListener(OnAdapterInteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_vcam, parent, false);
        }
        final Vcam vcam = vcamList.get(position);
        TextView vcamName = (TextView) convertView.findViewById(R.id.vcamName);
        vcamName.setText(vcam.CUSTOMER_VCAM_NAME);
        TextView tvVcamLocation = (TextView) convertView.findViewById(R.id.vcamLocation);
        tvVcamLocation.setText(vcam.VCAM_LOCATION);

        TextView tvOnline = (TextView) convertView.findViewById(R.id.tvOnline);
        if( vcam.ON_AIR == 4 ) {
            tvOnline.setVisibility(View.VISIBLE);
        } else {
            tvOnline.setVisibility(View.GONE);
        }

        TextView tvRecord = (TextView) convertView.findViewById(R.id.tvRecord);
        if( (vcam.ROS != 0) || (vcam.ROD != 0) ) {
            tvRecord.setVisibility(View.VISIBLE);
        } else {
            tvRecord.setVisibility(View.GONE);
        }


        ImageButton ibConfig = (ImageButton) convertView.findViewById(R.id.ibConfig);
        ibConfig.setTag(vcam.TOKEN);
        ibConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfigButtonClick(v);
                }
            }
        });

        ImageButton ibCalendar = (ImageButton) convertView.findViewById(R.id.ibCalendar);
        ibCalendar.setTag(vcam.TOKEN);
        ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onScheduleButtonClick(v);
                }
            }
        });


        ImageButton ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
        ibShare.setTag(vcam.TOKEN);
        ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShareButtonClick(v);
                }
            }
        });
        ImageButton ibRecord = (ImageButton) convertView.findViewById(R.id.ibRecord);
        ibShare.setTag(vcam.TOKEN);
        ibRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordControl( vcam.TOKEN, "session");

            }
        });
        ImageButton ibArchive = (ImageButton) convertView.findViewById(R.id.ibArchive);
        ibArchive.setTag(vcam.TOKEN);
        ibArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onArchButtonClick(v);
                }
            }
        });

        if( vcam.TYPE.equalsIgnoreCase("OWNER")) {
            ibConfig.setVisibility(View.VISIBLE);
            ibCalendar.setVisibility(View.VISIBLE);
            ibShare.setVisibility(View.VISIBLE);
            ibRecord.setVisibility(View.VISIBLE);
            ibArchive.setVisibility(View.VISIBLE);
        }
        if(vcam.THUMBNAIL != null) {
            ImageView ivThumb = (ImageView)  convertView.findViewById(R.id.ivThumb);
            ivThumb.setImageBitmap(vcam.THUMBNAIL);
        } else {
            final VcamAndThumb container = new VcamAndThumb(vcam, convertView);
            ThumbLoader loader = new ThumbLoader();
            loader.execute(container);
        }

        convertView.setTag(position);

        return convertView;
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
            //Log.d("MyApp", "recordControl: " + s);

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
}
