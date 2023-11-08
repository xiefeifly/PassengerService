package com.hst.base

import androidx.recyclerview.widget.RecyclerView
import android.util.SparseArray
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import android.widget.TextView
import androidx.annotation.DrawableRes
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView

class BaseRecycleViewHolder(itemView: View?) : RecyclerView.ViewHolder(
    itemView!!
) {
    private val mViews = SparseArray<View?>()
    val itemView: View
        get() = itemView

    fun <T : View?> getView(@IdRes viewId: Int): T? {
        var view = mViews[viewId]
        if (view == null) {
            view = itemView.findViewById(viewId)
            mViews.put(viewId, view)
        }
        return view as T?
    }

    fun setText(@IdRes viewId: Int, @StringRes resId: Int) {
        val view = getView<View>(viewId)!!
        (view as TextView).text = view.getContext().getString(resId)
    }

    fun setText(@IdRes viewId: Int, text: CharSequence?) {
        val view = getView<View>(viewId)!!
        (view as TextView).text = text
    }

    fun setGravity(@IdRes viewId: Int, gravity: Int) {
        val view = getView<View>(viewId)!!
        (view as TextView).gravity = gravity
    }

    fun setText(view: TextView, @StringRes resId: Int) {
        view.text = view.context.getString(resId)
    }

    fun setText(view: TextView, text: CharSequence?) {
        view.text = text
    }

    fun setTextColor(viewId: Int, color: Int) {
        val view = getView<View>(viewId)!!
        (view as TextView).setTextColor(color)
    }

    fun setImageResource(@IdRes viewId: Int, @DrawableRes resId: Int) {
        val view = getView<View>(viewId)!!
        (view as ImageView).setImageResource(resId)
    }

    fun setImageDrawable(viewId: Int, drawable: Drawable?) {
        val view = getView<View>(viewId)!!
        (view as ImageView).setImageDrawable(drawable)
    }

    fun setVisibility(visibility: Int, vararg viewIds: Int) {
        if (viewIds == null) {
            return
        }
        for (viewId in viewIds) {
            getView<View>(viewId)!!.visibility = visibility
        }
    }
}