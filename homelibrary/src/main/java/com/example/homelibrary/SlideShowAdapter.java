package com.example.homelibrary;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SlideShowAdapter extends PagerAdapter {

        private Context context;
        LayoutInflater inflater;
        public ArrayList<String> picturesArray;

        public SlideShowAdapter(Context context, ArrayList<String> picturesArray)
        {
                this.context = context;
                this.picturesArray = picturesArray;

        }


        @Override
        public int getCount() {
                return picturesArray.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

                return (view==(LinearLayout)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.home_slideshow_layout,container,false);

                ImageView imageView  = (ImageView) view.findViewById(R.id.imageView);
                String single_url = picturesArray.get(position);
                Glide.with(context).load(single_url).into(imageView);
                container.addView(view);
                return view;


        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((LinearLayout)object);
        }

}
