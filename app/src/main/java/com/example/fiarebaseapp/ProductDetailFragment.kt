package com.example.fiarebaseapp


import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.fiarebaseapp.viewmodels.ProductDetailViewModel
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_product_detail.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProductDetailFragment : Fragment() {
    val args: ProductDetailFragmentArgs by navArgs()

    lateinit var viewmodel: ProductDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProviders.of(activity!!).get(ProductDetailViewModel::class.java)
        viewmodel.productModel.observe(this, Observer {
            Picasso.get().load(url).fit().placeholder(R.drawable.default_image).error(R.drawable.default_image)
                .into(productImageView)
            nameTextView.text = it.productName
            priceTextView.text = it.price
            instoreTextView.text = "in store: " + it.inStock
            ratingTextView.text = "rating: " + it.reviewRating
            ratingCountTextView.text = "review count: " + it.reviewCount
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                descriptionTextView.text = Html.fromHtml(it.longDescription, Html.FROM_HTML_MODE_LEGACY)
            } else {
                descriptionTextView.text = Html.fromHtml(it.longDescription)
            }

        })

        viewmodel.productById(args.productId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Picasso.get().load(R.drawable.default_image).into(productImageView)
    }

}
