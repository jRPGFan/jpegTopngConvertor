package com.example.jpegtopngconvertor.presenters

import com.example.jpegtopngconvertor.screens.IScreens
import com.example.jpegtopngconvertor.views.MainView
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter

class MainPresenter(private val router: Router, private val screens: IScreens) :
    MvpPresenter<MainView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        router.replaceScreen(screens.showConvertor())
    }

    fun backClicked() {
        router.exit()
    }
}