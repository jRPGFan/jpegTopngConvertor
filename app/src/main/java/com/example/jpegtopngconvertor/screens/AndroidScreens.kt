package com.example.jpegtopngconvertor.screens

import com.example.jpegtopngconvertor.ui.ConvertorFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

class AndroidScreens : IScreens {
    override fun showConvertor() = FragmentScreen { ConvertorFragment.newInstance() }
}