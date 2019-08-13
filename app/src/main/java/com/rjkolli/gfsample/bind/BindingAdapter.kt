package com.rjkolli.gfsample.bind

import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import androidx.databinding.BindingAdapter

@BindingAdapter("onClickListener")
fun bindClickListener(view: View, clickListener: View.OnClickListener) {
    view.setOnClickListener(clickListener)
}

@BindingAdapter("onSeekBarListener")
fun bindSeekBarListener(seekBar: SeekBar, listenr: SeekBar.OnSeekBarChangeListener) {
    seekBar.setOnSeekBarChangeListener(listenr)
}

@BindingAdapter("textChangedListener")
fun bindTextWatcher(editText: EditText, textWatcher: TextWatcher) {
    editText.addTextChangedListener(textWatcher)
}