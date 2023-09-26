package com.example.ecrtest

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class MyRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private var items: MutableList<Message> = mutableListOf()

    inner class IncomingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jsonTextView: TextView = itemView.findViewById(R.id.jsonTextView)
    }

    inner class OutgoingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jsonTextView: TextView = itemView.findViewById(R.id.jsonTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_INCOMING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_line_item, parent, false)
                IncomingViewHolder(view)
            }
            VIEW_TYPE_OUTGOING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_line_item_out, parent, false)
                OutgoingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val prettyPrintedJson = gson.toJson(item)

        when (holder) {
            is MyRecyclerViewAdapter.IncomingViewHolder -> {
                holder.jsonTextView.text = prettyPrintedJson
                // Handle incoming item specific logic
            }
            is MyRecyclerViewAdapter.OutgoingViewHolder -> {
                holder.jsonTextView.text = prettyPrintedJson
                // Handle outgoing item specific logic
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        val messageType = items[position].messageType
        return when (messageType) {
            MessageType.INCOMING -> VIEW_TYPE_INCOMING
            MessageType.OUTGOING -> VIEW_TYPE_OUTGOING
        }
    }

    // Method to add a single item to the adapter
    fun addItem(item: Message) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    // Method to clear all items from the adapter
    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_INCOMING = 0
        private const val VIEW_TYPE_OUTGOING = 1
    }
}


class ItemDecorator(private val spaceHeight: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (position != 0) {
            outRect.top = spaceHeight
        }
    }
}

data class Message(
    val content: String,
    val messageType: MessageType // Add this property
)

enum class MessageType {
    INCOMING,
    OUTGOING
}
