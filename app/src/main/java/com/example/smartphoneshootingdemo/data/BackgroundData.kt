package com.example.smartphoneshootingdemo.data
import java.util.ArrayList


object BackgroundData {


        public val distanceMemory:MutableList<Double>  = ArrayList()
        public var timeMemory:MutableList<Double> = ArrayList()

    public fun getDistAverage() : Double{
        return distanceMemory.average()
    }

    public fun getTimeAverage():Double{
//        return timeMemory.average()
        return (timeMemory.max() - timeMemory.min())/1000
    }

}