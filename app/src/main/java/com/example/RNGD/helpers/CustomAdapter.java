package com.example.RNGD.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.RNGD.lifebook.R;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Created by Ashwin Kumar on 8/6/2014.
 */
public class CustomAdapter extends ArrayAdapter<Data> {
    Context context;
    LinkedList<Data> list;
    Bitmap placerHolder;
    private LruCache<String, Bitmap> cache;
    private final int maxMem;

    public CustomAdapter(Context context, int resource, LinkedList<Data> list) {
        super(context, resource, list);
        this.list = list;
        this.context = context;
        placerHolder = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        maxMem = (int) Runtime.getRuntime().maxMemory() / 1024;
        int capacity = maxMem / 10;
        cache = new LruCache<String, Bitmap>(capacity) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//convertView is not null when the listview is recycling, otherwise it is

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {//this modification changes tonnes of resources. No need to inflate everytime.
            //further enhancement by the ConvertView or ViewHolder Pattern
            convertView = inflater.inflate(R.layout.data_layout, parent, false);

        }
        Data data = list.get(position);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(data.title);
        TextView date = (TextView) convertView.findViewById(R.id.time);
        date.setText(data.date);
        TextView body = (TextView) convertView.findViewById(R.id.text);
        body.setText(data.body);
        ImageView image = (ImageView) convertView.findViewById(R.id.coverImg);
        loadBitmap(data.coverImgUri, image);

        return convertView;
    }


    public void addBitmapToCache(String key, Bitmap val) {
        if (getBitmapFromCache(key) == null) {
            cache.put(key, val);
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        return (cache.get(key));
    }


    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }

    }


    public void loadBitmap(String uri, ImageView imageView) {
        Bitmap bmp = cache.get(uri);
        if (bmp != null) {
            imageView.setImageBitmap(bmp);
            return;
        }

        if (cancelPotentialWork(uri, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(context.getResources(), placerHolder, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(uri);
        }
    }

    public static boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;

            if (bitmapData == null || !bitmapData.equals(data)) {

                bitmapWorkerTask.cancel(true);
            } else {

                return false;
            }
        }

        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        private WeakReference<ImageView> ivRef;
        public String data = "";

        public BitmapWorkerTask(ImageView iv) {
            iv.setImageResource(R.drawable.ic_launcher);
            ivRef = new WeakReference<ImageView>(iv);
        }


        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            Uri uri = Uri.parse(params[0]);
            Bitmap bmp = null;
            try {
                bmp = decodeUri(uri);
            } catch (Exception e) {

            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (!data.equals("") && bitmap != null) {
                cache.put(data, bitmap);
            }
            if (ivRef != null && bitmap != null) {
                ImageView iv = ivRef.get();
                if (iv != null) {
                    iv.setImageBitmap(bitmap);
                }
            }
        }
    }


    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 100;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);

    }


}
