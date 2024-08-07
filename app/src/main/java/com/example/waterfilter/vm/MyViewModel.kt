package com.example.waterfilter.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.waterfilter.data.AgentProduct

class MyViewModel : ViewModel(){
    public var productLiveData: MutableLiveData<List<AgentProduct>> = MutableLiveData()

    fun getProductLiveData() : LiveData<List<AgentProduct>> {
        return productLiveData
    }

    public fun setProductLiveData(list: List<AgentProduct>) {
        productLiveData.value = list
    }
}