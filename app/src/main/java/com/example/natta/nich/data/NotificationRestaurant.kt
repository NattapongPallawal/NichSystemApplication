package com.example.natta.nich.data

class NotificationRestaurant constructor(
        var customer: String,
        var date: Any,
        var message: String,
        var order: String,
        var restaurant: String,
        var fromRestaurant : Boolean,
        var read: Boolean = false
) {

}