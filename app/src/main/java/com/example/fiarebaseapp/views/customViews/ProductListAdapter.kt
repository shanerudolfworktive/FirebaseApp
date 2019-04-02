package com.example.fiarebaseapp.views.customViews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fiarebaseapp.R
import com.example.fiarebaseapp.models.ProductModel
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
//        return oldItem.productName == newItem.productName
//                && oldItem.shortDescription == newItem.shortDescription
//                && oldItem.inStock == newItem.inStock
//                && oldItem.price == newItem.price
//                && oldItem.productImage == newItem.productImage
//                && oldItem.reviewRating == newItem.reviewRating
        return oldItem == newItem
    }
}

class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val nameTextView = itemView.nameTextView

    fun bind(item: ProductModel?) {
        if(item == null) {
            nameTextView.text = "loading"
        }else {
            nameTextView.text = item.productName?: "product name holder"
            Log.e("testing", "insert time=" + item.insertTime)

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