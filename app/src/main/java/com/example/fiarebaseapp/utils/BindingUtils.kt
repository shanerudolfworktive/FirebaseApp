package com.example.fiarebaseapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.fiarebaseapp.R
import com.example.fiarebaseapp.consts.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_product_detail.*

class BindingUtils {
    companion object {
        @JvmStatic @BindingAdapter("imageUrl")
        fun ImageView.setImageUrl(url: String?) {
            Picasso.get().load(Constants.BASE_URL + url).fit().placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(this)
        }
    }
}