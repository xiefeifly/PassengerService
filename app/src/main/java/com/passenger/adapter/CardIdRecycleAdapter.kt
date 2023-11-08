package com.passenger.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.passenger.R
import com.passenger.bean.DataItem
import com.passenger.bean.PassengerAttachment

class CardIdRecycleAdapter() :
    RecyclerView.Adapter<CardIdRecycleAdapter.ViewHolde>() {
    var data: MutableList<DataItem> = mutableListOf()
    var mOnClickListener: onClickListener? = null
    fun setDatas(datas: MutableList<DataItem>) {
        data = datas
        notifyDataSetChanged()
    }

    fun setData(onClickListener: onClickListener) {
        mOnClickListener = onClickListener
    }

    inner class ViewHolde(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val customCode: TextView = itemView.findViewById(R.id.customCode)
        val appName: TextView = itemView.findViewById(R.id.passengerName)
        val cardid: TextView = itemView.findViewById(R.id.cardid)
        val imageView: ImageView = itemView.findViewById(R.id.deleteImage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolde {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cardid, parent, false)

        return ViewHolde(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolde, position: Int) {
        val dataItem = data[position]
        with(holder) {
            customCode.text = dataItem.Passenger?.CustomCode
            appName.text = dataItem.Passenger?.Name
            cardid.text = dataItem.Passenger?.IDcard
            imageView.setOnClickListener {
                Log.e("TAG", "onCreateViewHolder: ============")
                mOnClickListener?.OnitemListener(dataItem)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    interface onClickListener {

        fun OnitemListener(item: DataItem)

    }
}