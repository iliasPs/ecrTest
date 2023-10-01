package com.example.ecrtest

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
        val directionView: TextView =itemView.findViewById(R.id.direction)
    }

    inner class OutgoingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jsonTextView: TextView = itemView.findViewById(R.id.jsonTextView)
        val directionView: TextView = itemView.findViewById(R.id.direction)
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
                holder.jsonTextView.text = item.content
                holder.directionView.text = item.messageType.toString()
                // Handle incoming item specific logic
            }
            is MyRecyclerViewAdapter.OutgoingViewHolder -> {
                holder.jsonTextView.text = item.content
                holder.directionView.text = item.messageType.toString()
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

    fun getItems(): MutableList<Message> {
        return items
    }
}


class ItemDecorator(private val dividerHeight: Int) : RecyclerView.ItemDecoration() {

    private val dividerPaint = Paint()

    init {
        dividerPaint.color = Color.BLACK // Set the color of the divider (black in this case)
        dividerPaint.style = Paint.Style.STROKE
        dividerPaint.strokeWidth = dividerHeight.toFloat()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + dividerHeight

            c.drawLine(left.toFloat(), top.toFloat(), right.toFloat(), top.toFloat(), dividerPaint)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = dividerHeight // Add bottom margin to each item to create space for the divider
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
