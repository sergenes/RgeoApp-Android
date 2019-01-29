package com.nes.rgeo.dao

import com.nes.rgeo.model.Favorite

class FavoritesRepository {
    val dataSource:ArrayList<Favorite> = ArrayList()

    init {
        dataSource.add(Favorite("line 1", "address 1"))
        dataSource.add(Favorite("line 2", "address 2"))
        dataSource.add(Favorite("line 3", "address 3"))
    }


    fun getFavorites(): ArrayList<Favorite> {
        return dataSource
    }

    fun addPlace(){
        dataSource.add(Favorite("name", "address"))
    }
}