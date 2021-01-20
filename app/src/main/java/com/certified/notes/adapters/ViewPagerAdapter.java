package com.certified.notes.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.RenderMode;
import com.certified.notes.R;
import com.certified.notes.model.SliderItem;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder> {

    private static final String TAG = "ViewPagerAdapter";

    private Context mContext;

    private List<SliderItem> mSliderItems;
    private ViewPager2 mViewPager2;

    public ViewPagerAdapter(List<SliderItem> sliderItems, ViewPager2 viewPager2) {
        mSliderItems = sliderItems;
        mViewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_view_pager, parent, false);
        Log.d(TAG, "onCreateViewHolder: Created");

        return new ViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        holder.setItems(mSliderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mSliderItems.size();
    }

    public static class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        private LottieAnimationView animationView;
        private TextView tvTitle, tvDescription;

        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            animationView = itemView.findViewById(R.id.animation_view);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }

        public void setItems(@NonNull SliderItem sliderItem) {
            animationView.setRenderMode(RenderMode.SOFTWARE);
            animationView.setAnimation(sliderItem.getAnimation());
            tvTitle.setText(sliderItem.getTitle());
            tvDescription.setText(sliderItem.getDescription());
        }
    }
}
