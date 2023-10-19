package com.twproject.banyeomiji

class MyGlobals {

    var firstExplain = true
    var fullAdCount = 0
    var userLogin = 0

    companion object {
        @get:Synchronized
        var instance: MyGlobals? = null
            get() {
                if (null == field) {
                    field = MyGlobals()
                }
                return field
            }
            private set
    }
}