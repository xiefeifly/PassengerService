package com.hst.base

import android.content.Context
import kotlin.jvm.JvmOverloads
import androidx.recyclerview.widget.RecyclerView
import com.hst.base.BaseRecycleViewHolder
import android.util.SparseBooleanArray
import com.hst.fsp.FspUserInfo
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.collection.ArraySet

abstract class BaseRecyclerAdapter<T> @JvmOverloads constructor(
    protected var mContext: Context,
    protected var mDatas: MutableList<T>?,
    private val mItemLayoutId: Int,
    protected var mCanSelect: Boolean,
    selectedItem: ArraySet<T>? = null
) : RecyclerView.Adapter<com.hst.base.BaseRecycleViewHolder>() {
    private var mCanClear // setData ，第一次数据不被删除，后续查找的数据都是新数据，须被删除
            = false
    protected var isSelected: SparseBooleanArray? = null
        private set
    var selectedItem: ArraySet<T>? = null
        private set

    init {
        if (mCanSelect) initSelect(selectedItem)
    }

    private fun initSelect(selectedItem: ArraySet<T>?) {
        isSelected = SparseBooleanArray()
        if (selectedItem == null) {
            this.selectedItem = ArraySet()
            if (mDatas != null && !mDatas!!.isEmpty()) {
                for (i in mDatas!!.indices) {
                    isSelected!!.put(i, false)
                }
            }
        } else {
            this.selectedItem = selectedItem
            if (mDatas != null && !mDatas!!.isEmpty()) {
                for (i in mDatas!!.indices) {
                    val d = mDatas!![i]
                    if (d is FspUserInfo) {
                        val info = mDatas!![i] as FspUserInfo
                        for (it in this.selectedItem!!) {
                            val item = it as FspUserInfo
                            if (item.userId == info.userId) {
                                isSelected!!.put(i, true)
                                break
                            }
                        }
                    } else {
                        if (selectedItem.contains(d)) {
                            isSelected!!.put(i, true)
                        } else {
                            isSelected!!.put(i, false)
                        }
                    }
                }
            }
        }
    }

    fun setData(d: MutableList<T>?) {
        if (mCanClear && mDatas != null && !mDatas!!.isEmpty()) {
            mDatas!!.clear()
        }
        mDatas = d
        mCanClear = true
        if (mCanSelect) {
            isSelected!!.clear()
            if (mDatas != null && !mDatas!!.isEmpty()) {
                for (i in mDatas!!.indices) {
                    if (selectedItem!!.contains(mDatas!![i])) {
                        isSelected!!.put(i, true)
                    } else {
                        isSelected!!.put(i, false)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.hst.base.BaseRecycleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(mItemLayoutId, parent, false)
        return com.hst.base.BaseRecycleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: com.hst.base.BaseRecycleViewHolder, position: Int) {
        if (haveHeaderItem()) {
            onBindViewData(holder, position, if (position == 0) null else mDatas!![position - 1])
        } else {
            onBindViewData(holder, position, mDatas!![position])
        }
    }

    override fun getItemCount(): Int {
        val nempty = if (haveHeaderItem()) 1 else 0
        return if (mDatas == null) nempty else mDatas!!.size + nempty
    }

    protected abstract fun onBindViewData(holder: com.hst.base.BaseRecycleViewHolder?, position: Int, item: T?)
    protected fun haveHeaderItem(): Boolean {
        return false
    }
}