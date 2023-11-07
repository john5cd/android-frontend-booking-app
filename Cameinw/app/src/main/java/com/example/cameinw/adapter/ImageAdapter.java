package com.example.cameinw.adapter;

import android.content.Context;

import androidx.viewpager.widget.PagerAdapter;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.List;

public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    private List<Bitmap> bitmapImages;
    private ImageView imageView;

    public ImageAdapter(Context context, List<Bitmap> images) {
        this.bitmapImages = images;
        mContext = context;
    }

    @Override
    public int getCount() {return bitmapImages.size();}

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageView.setImageBitmap(bitmapImages.get(position));
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}
