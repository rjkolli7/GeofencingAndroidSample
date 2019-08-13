package com.rjkolli.gfsample.bind

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("onClickListener")
fun bindClickListener(view: View, clickListener: View.OnClickListener) {
    view.setOnClickListener(clickListener)
}