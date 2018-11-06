package com.example.natta.nich.data



class Feedback constructor(var customer :String?="",
                           var customerObj : Customer?= Customer(),
                           var title : String? = "",
                           var comment : String? = "",
                           var rating : Double? = 0.0,
                           var date: Any? = null){
}