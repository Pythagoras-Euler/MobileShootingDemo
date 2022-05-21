package com.example.smartphoneshootingdemo.data

object shooting_data{
    var current_score = 0

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


}