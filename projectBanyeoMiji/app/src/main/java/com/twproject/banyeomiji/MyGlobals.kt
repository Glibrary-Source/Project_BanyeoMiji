package com.twproject.banyeomiji

class MyGlobals {

    var checkPermissionFirst = true
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