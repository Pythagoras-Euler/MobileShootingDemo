package com.example.smartphoneshootingdemo.data
import java.util.ArrayList


object BackgroundData {
//TODO bug:时间计算未清零，导致总用时不准确

        public var distanceMemory:MutableList<Double>  = ArrayList()
        public var timeMemory:MutableList<Double> = ArrayList()

    public fun getDistAverage() : Double{
        return distanceMemory.average()
    }

    public fun getTimeAverage():Double{
//        return timeMemory.average()
        return (timeMemory.max() - timeMemory.min())/1000
    }

    public fun resetDistAvg(){
        distanceMemory = ArrayList()
    }

    public fun resetTimeAvg(){
        timeMemory = ArrayList()
    }

}