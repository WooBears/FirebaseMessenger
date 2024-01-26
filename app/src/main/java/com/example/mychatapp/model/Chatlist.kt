package com.example.mychatapp.model

class Chatlist {

    private var id: String = ""

    constructor()

    constructor(id: String){
        this.id = id
    }

    fun getId(): String? {
        return id
    }

    fun setSender(sender: String?)
    {
        this.id = id
    }
}