package com.tcs.edureka.ui.activity.media

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tcs.edureka.databinding.MediaItemListBinding
import com.tcs.edureka.model.mediaplayer.MediaModel

class MyMediaAdapter(val onClick: (Int) -> Unit) : RecyclerView.Adapter<MyMediaAdapter.MyMediaViewHolder>() {

    interface OnNextPrevious {
        fun onNext(mediaModel: MediaModel)
        fun onPrevious(mediaModel: MediaModel)
    }

    var selectedPosition = -1

    var onNextPrevious: OnNextPrevious? = null

    private val diffUtil = object : DiffUtil.ItemCallback<MediaModel>() {
        override fun areItemsTheSame(oldItem: MediaModel, newItem: MediaModel): Boolean =
                oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: MediaModel, newItem: MediaModel): Boolean =
                oldItem.id == newItem.id

    }

    val differ = AsyncListDiffer(this, diffUtil)

    class MyMediaViewHolder(private val mediaItemListBinding: MediaItemListBinding) : RecyclerView.ViewHolder(mediaItemListBinding.root) {
        init {
            mediaItemListBinding.root.setPadding(0, 4, 0, 4)
        }

        fun bind(mediaModel: MediaModel) {
            mediaItemListBinding.apply {
                tvTitle.text = mediaModel.title
                tvCount.text = "Played ${mediaModel.payedCount} times"

                Picasso.get()
                        .load(mediaModel.imgId)
                        .into(imageAlbum)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMediaViewHolder {
        val binding = MediaItemListBinding.inflate(LayoutInflater.from(parent.context))
        return MyMediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyMediaViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
        if (position != RecyclerView.NO_POSITION) {
            holder.itemView.setOnClickListener {
                onClick(position)
                selectedPosition = position
                notifyDataSetChanged()
            }
        }

        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#D5BCBC"))
        }
    }


    override fun getItemCount(): Int = differ.currentList.size


}