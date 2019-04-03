package com.example.fiarebaseapp.views.customViews

import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fiarebaseapp.R
import com.example.fiarebaseapp.consts.Constants
import com.example.fiarebaseapp.models.ProductModel
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.product_list_item.view.*

open interface OnItemClickListener{
    fun onClickListener(productModel: ProductModel?, rootView: View)
}

class ProductListAdapter : PagedListAdapter<ProductModel, ProductViewHolder>(DiffCallback()){

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener{
            onItemClickListener?.onClickListener(item, holder.itemView)
        }
        holder.bind(item)
    }
}

class DiffCallback: DiffUtil.ItemCallback<ProductModel>() {
    override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
        return oldItem.productId.equals(newItem.productId)
    }

    override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
        return oldItem.productName == newItem.productName
                && oldItem.shortDescription == newItem.shortDescription
                && oldItem.inStock == newItem.inStock
                && oldItem.price == newItem.price
                && oldItem.productImage == newItem.productImage
                && oldItem.reviewRating == newItem.reviewRating
                && oldItem.longDescription == newItem.longDescription
    }
}

class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val nameTextView = itemView.nameTextView
    val productImageView = itemView.productItemImateView
    val priceTextView = itemView.priceTextView
    val shortDescriptionTextView = itemView.shortDescriptionTextView
    fun bind(item: ProductModel?) {
        shortDescriptionTextView.visibility = View.VISIBLE
        if(item == null) {
            nameTextView.text = "loading"
            Picasso.get().load(R.drawable.default_image)
                .fit().into(productImageView)
            priceTextView.text = "loading"
            shortDescriptionTextView.text = "loading"
        }else {
            nameTextView.text = item.productName?: "product name holder"
            Picasso.get().load(Constants.BASE_URL + item.productImage)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .fit().into(productImageView)
            priceTextView.text = item.price
            if(item.shortDescription == null) {
                shortDescriptionTextView.visibility = View.GONE
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    shortDescriptionTextView.text = Html.fromHtml(item.shortDescription, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    shortDescriptionTextView.text = Html.fromHtml(item.shortDescription)
                }
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): ProductViewHolder{
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_list_item, parent, false)
            return ProductViewHolder(view)
        }
    }
}