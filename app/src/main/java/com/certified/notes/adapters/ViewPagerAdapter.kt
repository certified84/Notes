package com.certified.notes.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.RenderMode
import com.certified.notes.R
import com.certified.notes.model.SliderItem
import java.util.concurrent.Executors

class ViewPagerAdapter(private val sliderItem: List<SliderItem>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val animationView: LottieAnimationView = itemView.findViewById(R.id.animation_view)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_description)

        fun setItems(sliderItem: SliderItem) {
            val executorService = Executors.newSingleThreadExecutor()
            executorService.execute {
                animationView.setRenderMode(RenderMode.SOFTWARE)
                animationView.setAnimation(sliderItem.animation)
                Log.d("SliderItem", "setItems: " + Thread.currentThread().id)
            }
            animationView.enableMergePathsForKitKatAndAbove(true)
            tvTitle.text = sliderItem.title
            tvDescription.text = sliderItem.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setItems(sliderItem[position])
    }

    override fun getItemCount(): Int {
        return sliderItem.size
    }
}