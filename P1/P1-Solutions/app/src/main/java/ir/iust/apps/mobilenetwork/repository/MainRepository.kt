package ir.iust.apps.mobilenetwork.repository

import androidx.lifecycle.MutableLiveData
import ir.iust.apps.mobilenetwork.Pages

class MainRepository {

    var pageState: MutableLiveData<Pages> = MutableLiveData(Pages.LOGIN)

    companion object {
        val instance = MainRepository()
    }
}