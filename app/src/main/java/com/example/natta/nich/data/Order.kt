package com.example.natta.nich.data

class Order constructor(var orderNumber: Int? = null,
                        var total: Double? = null,
                        var status: String? = null,
                        var date: Any? = null,
                        var restaurantID: String? = null,
                        var paymentType: String? = null,
                        var restaurantName: String? = null,
                        var currentStatus: Int? = null,
                        var numStatus: Int? = null,
                        var fromRestaurant: Boolean? = null,
                        var finish: Boolean? = null,
                        var totalMenu: Int? = null,
                        var customerID: String? = null,
                        var table: String? = null
) {
}