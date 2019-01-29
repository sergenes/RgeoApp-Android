package com.nes.rgeo.ui.main

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nes.rgeo.dao.FavoritesRepository
import com.nes.rgeo.model.Favorite

class FavoritesViewModel:ViewModel() {
    val observableFavorites: MutableLiveData<ArrayList<Favorite>> = MediatorLiveData<ArrayList<Favorite>>()
    val dataSource = FavoritesRepository()

    init {
        observableFavorites.value = dataSource.getFavorites()
    }

    fun loadData(){
        observableFavorites.value = dataSource.getFavorites()
    }

    fun addPlace(){
        dataSource.addPlace()
        observableFavorites.postValue(dataSource.getFavorites())
    }

}