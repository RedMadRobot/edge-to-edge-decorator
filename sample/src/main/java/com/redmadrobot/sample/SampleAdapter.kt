package com.redmadrobot.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.redmadrobot.sample.databinding.ItemSampleBinding

class SampleAdapter(private val data: List<SampleData>) : RecyclerView.Adapter<SampleAdapter.SampleViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        val item = ItemSampleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SampleViewHolder(item)
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        with(holder.item) {
            image.setImageResource(data[position].image)

            title.text = data[position].title
            description.text = data[position].description
        }
    }

    class SampleViewHolder(val item: ItemSampleBinding) : RecyclerView.ViewHolder(item.root)
}

data class SampleData(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
)
