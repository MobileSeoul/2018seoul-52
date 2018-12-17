package com.project.androidtest.util;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.project.androidtest.R;

public class DataBindingUtils {
    @BindingAdapter({"imageUrl"})
    public static void loadImage(SimpleDraweeView simpleDraweeView, String url) {
        simpleDraweeView.setImageURI(url);
        simpleDraweeView.getHierarchy().setFailureImage(R.drawable.img_empty);
    }

    @BindingAdapter({"dateStr"})
    public static void setDate(TextView textView, String str) {
        String convertStr = str.substring(0, 10);
        textView.setText(convertStr);
    }
}
