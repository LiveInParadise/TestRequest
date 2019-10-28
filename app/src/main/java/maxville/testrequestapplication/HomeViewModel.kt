package maxville.testrequestapplication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import maxville.testrequestapplication.data.common.BaseViewModel
import maxville.testrequestapplication.data.common.SingleLiveEvent
import java.net.URL

class HomeViewModel : BaseViewModel() {

    var text = MutableLiveData<String>()
    var toast = SingleLiveEvent<String>()
    var progressShow = SingleLiveEvent<Boolean>()

    fun executeRequest(address: String) {
        addDisposable(
            Single.just(address)
                .map { url ->
                    URL(url).readText()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressShow.value = true }
                .doAfterTerminate { progressShow.value = false }
                .subscribe({
                    text.value = it
                }, {
                    toast.value = it.message
                    Log.e("error", it.message)
                })
        )
    }
}