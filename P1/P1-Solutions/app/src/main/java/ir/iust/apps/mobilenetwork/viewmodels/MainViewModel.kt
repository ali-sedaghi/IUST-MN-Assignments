package ir.iust.apps.mobilenetwork.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ir.iust.apps.mobilenetwork.repository.MainRepository
import ir.iust.apps.mobilenetwork.Pages

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val mainRepository: MainRepository = MainRepository.instance

    var pageState: MutableLiveData<Pages> = mainRepository.pageState
}