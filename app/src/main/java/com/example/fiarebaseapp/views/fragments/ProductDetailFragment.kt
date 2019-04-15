package com.example.fiarebaseapp.views.fragments


import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.fiarebaseapp.R
import com.example.fiarebaseapp.consts.Constants
import com.example.fiarebaseapp.databinding.FragmentProductDetailBinding
import com.example.fiarebaseapp.viewmodels.ProductDetailViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_product_detail.*

class ProductDetailFragment : Fragment() {
    lateinit var binding: FragmentProductDetailBinding
    val args: ProductDetailFragmentArgs by navArgs()

    lateinit var viewmodel: ProductDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProviders.of(activity!!).get(ProductDetailViewModel::class.java)
        viewmodel.productModel.observe(this, Observer {
            Picasso.get().load(Constants.BASE_URL + it.productImage).fit().placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(productImageView)
            binding.productModel = it
            nameTextView.text = it.productName ?: ""
            priceTextView.text = it.price ?: ""
            instoreTextView.text = getString(R.string.inStore, it.inStock)
            ratingTextView.text = getString(R.string.rating, it.reviewRating)
            ratingCountTextView.text = getString(R.string.reviewCount, it.reviewCount)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                descriptionTextView.text = Html.fromHtml(it.longDescription ?: "", Html.FROM_HTML_MODE_LEGACY)
            } else {
                descriptionTextView.text = Html.fromHtml(it.longDescription ?: "")
            }
        })

        viewmodel.productById(args.productId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Picasso.get().load(R.drawable.default_image).into(productImageView)
    }

}
