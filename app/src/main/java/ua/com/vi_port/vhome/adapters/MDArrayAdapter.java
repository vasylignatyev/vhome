package ua.com.vi_port.vhome.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

import ua.com.vi_port.vhome.R;
import ua.com.vi_port.vhome.models.MotionDetectNew;

/**
 * Created by vignatyev on 02.09.2015.
 */
public class MDArrayAdapter extends ArrayAdapter<MotionDetectNew> {
    private Context context;
    private List<MotionDetectNew> mMotionDetectList;
    private LruCache< String, Bitmap > imageCache;
    private RequestQueue queue;
    private ImagePagerAdapter imagePagerAdapter;

    private OnMDArrayAdapterListener mListener;

    //private ViewPager viewPager;


    public MDArrayAdapter(Fragment fragment, int resource, List<MotionDetectNew> objects) {
        super( fragment.getContext(), resource, objects);

        Context context = fragment.getContext();

        try {
            mListener = (OnMDArrayAdapterListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString()
                    + " must implement OnMDArrayAdapterListener");
        }


        this.context = context;

        this.mMotionDetectList = objects;

        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        Log.d("MyApp", "MEM -> " + cacheSize);
        imageCache = new LruCache<>(cacheSize);
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewPager viewPager;
        View view;

        if(convertView !=null){
            viewPager = (ViewPager) convertView.findViewById(R.id.view_pager);
            viewPager.setAdapter(null);

            int i = (int) convertView.getTag();
            mMotionDetectList.get(i).viewPager = null;
            view = convertView;
        } else {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_motion_detect, parent, false);
        }
        final MotionDetectNew motionDetect = mMotionDetectList.get(position);

        TextView tvNotificationDate = (TextView) view.findViewById(R.id.tvNotificationDate);
        TextView tvNotificationCamName = (TextView) view.findViewById(R.id.tvNotificationCamName);

        tvNotificationDate.setText(motionDetect.date);
        tvNotificationCamName.setText(motionDetect.cam_name);

        Button btFindInArchive = (Button) view.findViewById(R.id.btFindInArchive);
        btFindInArchive.setTag(motionDetect.iMotionDetect);
        btFindInArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int iMotionDetect = Integer.parseInt(view.getTag().toString());

                Log.d("MyApp", "Find In Archive Click " + iMotionDetect);

                mListener.genMotionDetectPopup(iMotionDetect);

            }
        });

        view.setTag(position);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        imagePagerAdapter = new ImagePagerAdapter(context, motionDetect.images, motionDetect.iMotionDetect, imageCache, queue);
        viewPager.setAdapter(imagePagerAdapter);

        motionDetect.viewPager = viewPager;
        return view;
    }

    @Override
    public void clear() {
        Log.d("MyApp", "MDArrayAdapter::clear");
        ViewPager viewPager;
        for(MotionDetectNew motionDetectNew : mMotionDetectList ) {
            if(motionDetectNew.viewPager != null) {
                //viewPager = (ViewPager) motionDetectNew.view;
                motionDetectNew.viewPager.setAdapter(null);
            }
        }
        super.clear();
    }

    public interface OnMDArrayAdapterListener {
        void genMotionDetectPopup(int iMotionDetect);
    }

}
