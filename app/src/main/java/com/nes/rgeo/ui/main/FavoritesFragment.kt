package com.nes.rgeo.ui.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.nes.rgeo.R
import kotlinx.android.synthetic.main.fragment_favorites.*

/**
 * A simple [Fragment] subclass.
 *
 */
class FavoritesFragment : Fragment() {

    private lateinit var viewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(FavoritesViewModel::class.java)


        this.context?.let { context->
            favoritesRecyclerView.layoutManager = LinearLayoutManager(context)


            viewModel.observableFavorites.observe( this, Observer {
                favoritesRecyclerView.adapter = FavoritesAdapter(it, context)
            })
        }

        addPlaceButton.setOnClickListener {
            viewModel.addPlace()
        }


//        viewModel.loadData()

    }
}
