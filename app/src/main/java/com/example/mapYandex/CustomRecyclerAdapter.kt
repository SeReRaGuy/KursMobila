package com.example.mapYandex

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerAdapter(
    private val action: ActionInterface
) : RecyclerView.Adapter<CustomRecyclerAdapter.TagHolder>() {
    class TagHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnailImage: ImageView = itemView.findViewById(R.id.thumbnail)
        val largeTextView: TextView = itemView.findViewById(R.id.textViewLarge)
        val deleteImage: ImageView = itemView.findViewById(R.id.deleter)
    }

    var tags: List<Tag> = emptyList()
        @SuppressLint("NotifyDataSetChanged") set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        val itemView =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item, parent, false)
        return TagHolder(itemView)
    }

    override fun getItemCount() = tags.size

    override fun onBindViewHolder(holder: TagHolder, position: Int) {
        val tag = tags[position]
        holder.itemView.tag = tag.id
        if (tag.image != null) {
            holder.thumbnailImage.setImageBitmap(tag.image)
        } else {
            holder.thumbnailImage.setImageResource(R.drawable.null_image_wh)
        }
        holder.largeTextView.text = tag.name
//        holder.itemView.setOnClickListener {
//            action.onItemClick(tag.id!!) //Убрать !!
//        }
        holder.deleteImage.setOnClickListener {
            AlertDialog.Builder(holder.deleteImage.context)
                .setIcon(android.R.drawable.ic_menu_delete)
                .setTitle("Вы действительно хотите удалить карточку?").setMessage(
                    "Будет удалена карточка:" + "\n ${tag.name}"
                ).setPositiveButton("Да") { _, _ -> action.onDeleteTag(tag.id!!) } //Убрать !!
                .setNegativeButton("Нет") { _, _ ->
                    Toast.makeText(
                        holder.deleteImage.context, "Удаление отменено", Toast.LENGTH_LONG
                    ).show()
                }.show()
        }
    }
}