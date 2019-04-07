package com.example.fiarebaseapp.models

enum class State{
    NONE, LOADING, SUCCESS, ERROR
}

class NetworkState (
    val state: State = State.NONE,
    val errorMessage: String? = "",
    val successMessage: String? = ""
)