package com.example.swift.businessLayer.dataClasses

class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long, val offer: Boolean) {
    constructor() : this("", "", "", "", -1, false)
}