package com.example.fiarebaseapp


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.fiarebaseapp.viewmodels.ProductDetailViewModel
import com.google.gson.Gson


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
            Log.e("testing", "product=" + Gson().toJson(it))
        })

        viewmodel.productById(args.productId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.e("testing", "id=" + args.productId)
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }


}
