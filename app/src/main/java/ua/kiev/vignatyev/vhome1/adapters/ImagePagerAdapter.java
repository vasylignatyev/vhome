package ua.kiev.vignatyev.vhome1.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.List;

import ua.kiev.vignatyev.vhome1.MainActivity;
import ua.kiev.vignatyev.vhome1.PinchActivity;
import ua.kiev.vignatyev.vhome1.R;
import ua.kiev.vignatyev.vhome1.models.MDImage;

/**
 * Created by vignatyev on 10.09.2015.
 */
public class ImagePagerAdapter extends PagerAdapter {
    /*
     * VARS
     */
    private final int DEFAULT_CACHE_SIZE_PROPORTION = 8;
    private Context mContext;
    private List<String> mImages;
    private LruCache<String, Bitmap> mImageCache;
    private int mIMotionDetect;

    private RequestQueue queue;

    public ImagePagerAdapter(Context context, List<String> images, int iMotionDetect,
                             LruCache<String, Bitmap> imageCache, RequestQueue queue) {
        mContext = context;
        mImages = images;
        mIMotionDetect = iMotionDetect;

        this.mImageCache = imageCache;
        this.queue = queue;
    }

    @Override
    public int getCount() {
        return ((mImages == null) ? 0 : mImages.size());
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d("MyApp", "ImageAdapter::instantiateItem position: " + position);

        final ImageView imageView = new ImageView(mContext);
        //setTAB fpr image for findViewWithTag
        int padding = 2;

        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        imageView.setLayoutParams(params);
        //imageView.setTag("ImageView");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MDImage mdImage = (MDImage) view.getTag();

                Intent intent = new Intent(mContext, PinchActivity.class);
                intent.putExtra(PinchActivity.ARG_IMAGE_URL, mdImage.uri);
                mContext.startActivity(intent);
            }
        });
        container.addView(imageView, 0);

        if(mImages != null) {
            final String uri = mImages.get(position);
            Bitmap bitmap = mImageCache.get(uri);
            Log.d("MyApp", "URI: " + uri);
            MDImage mdImage = new MDImage(mIMotionDetect, uri);
            imageView.setTag(mdImage);
            if(bitmap != null){
                Log.d("MyApp", "Loading image from cahch: " + uri);
                imageView.setImageBitmap(bitmap);
            } else {
                Log.d("MyApp", "Loading image from server: " + MainActivity.SERVER_URL + uri);
                ImageRequest imageRequest = new ImageRequest( MainActivity.SERVER_URL + uri,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                imageView.setImageBitmap(bitmap);
                                //mImageCache.put(uri, bitmap);
                            }
                        },
                        534, 300,
                        Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                                imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_unavailable));
                            }
                        }
                );
                queue.add(imageRequest);
            }
        } else {
            Log.d("MyApp", "mImages is NULL");
        }
        return imageView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //View v = (View) object;
        ImageView imageView = (ImageView) object;
        //Log.d("MyApp", "ImageView.tag : " + imageView.getTag());
        //ImageView imageView = (ImageView) v.findViewWithTag("ImageView");
        //ImageView imageView = (ImageView) container.getChildAt(0);
        if(imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Log.d("MyApp", "Recycling image: " + position);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if(bitmap != null) {
                    bitmap.recycle();
                }
                imageView.setImageBitmap(null);
            }
            container.removeView(imageView);
        }

        object = null;
    }

    /**
     *
     */
    private class UriAndImageView {
        public UriAndImageView(ImageView ivEventThumb, String uri) {
            this.ivEventThumb = ivEventThumb;
            this.uri = uri;
        }
        public String uri;
        public ImageView ivEventThumb;
    }
}
