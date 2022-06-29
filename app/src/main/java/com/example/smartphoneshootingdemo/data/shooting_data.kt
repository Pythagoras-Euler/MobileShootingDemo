package com.example.smartphoneshootingdemo.data

object shooting_data{
    var current_score = 0
    var current_wrong_time = -1
    var current_num = 0

    fun getter_score(): Int {
        return current_score
    }

    public fun setter_score(change_score: Int = 0, target_score : Int = -1):Boolean
    {
        if (target_score == -1) {
            current_score += change_score
            return true
        }
        else
        {
            current_score = target_score
            return true
        }
    }

    fun getter_wrong():Int
    {
        return current_wrong_time

    }


    public fun setter_wrong(change_score: Int = 0):Boolean
    {
            current_wrong_time += change_score
            return true
    }

    public fun reset_wrong() : Boolean
    {
        current_wrong_time = -1
        return true
    }

    public fun getter_num () :Int{
        return current_num
    }

    public fun setter_num(change_num:Int)
    {
        current_num += change_num
    }

    public fun reset_num()
    {
        current_num = 0
    }


}