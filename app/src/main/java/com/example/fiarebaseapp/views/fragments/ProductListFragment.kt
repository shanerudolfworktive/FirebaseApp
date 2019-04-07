package com.example.fiarebaseapp.views.fragments


import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fiarebaseapp.R
import com.example.fiarebaseapp.models.ProductModel
import com.example.fiarebaseapp.models.State
import com.example.fiarebaseapp.viewmodels.ProductListViewModel
import com.example.fiarebaseapp.views.customViews.OnItemClickListener
import com.example.fiarebaseapp.views.customViews.ProductListAdapter
import kotlinx.android.synthetic.main.fragment_product_list.*

class ProductListFragment : Fragment() {
    lateinit var viewmodel: ProductListViewModel
    private val adapter = ProductListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewmodel = ViewModelProviders.of(activity!!).get(ProductListViewModel::class.java)

        viewmodel.productModels.observe(this, Observer {
            adapter.submitList(it)
        })

        viewmodel.networkState.observe(this, Observer {
            var toastMessage = ""
            if(it.state == State.ERROR) {
                toastMessage += getString(R.string.networkError, it.errorMessage?: "")
            }else if (it.state == State.SUCCESS) {
                toastMessage += getString(R.string.fetchPageSuccess, it.successMessage?: "")
            }
            Toast.makeText(activity, toastMessage, Toast.LENGTH_SHORT).show()
        })

        adapter.onItemClickListener = object : OnItemClickListener {
            override fun onClickListener(productModel: ProductModel?, rootView: View) {
                val action = ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(productModel?.productId)
                findNavController().navigate(action)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar as Toolbar?)
        setHasOptionsMenu(true)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.product_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteAllProducts -> {
                viewmodel.deleteAllProducts()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
