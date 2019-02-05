package com.example.web3jandroid

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.web3jandroid.wallet.WalletRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import timber.log.Timber
import java.io.File
import javax.inject.Inject


public class MainViewModel : ViewModel() {

    private var web3j: Web3j
    private val credentials: MutableLiveData<Credentials> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()
    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var walletRepository: WalletRepository
    private val walletName: MutableLiveData<String> = MutableLiveData()
    private val transactionResult: MutableLiveData<TransactionReceipt> = MutableLiveData()

    init {
        Web3jApplication.applicationComponent.inject(this)
        web3j = Web3j.build(
            HttpService(
                "https://ropsten.infura.io/v3/90211e995749445da6934149f7f58031"
            )
        )
    }

    fun getWallet(walletDir: File): LiveData<String> {
        val disposable = Observable.just(preferences.getString("wallet_name", ""))
            .concatMap {
                when {
                    it.isEmpty() -> {
                        walletRepository.getWallet(walletDir)
                            .doOnNext { it1 ->
                                Timber.d("Saving wallet name to prefs")
                                preferences.edit().putString("wallet_name", it1).apply()
                            }
                    }
                    else -> Observable.just(it)
                }
            }
            .subscribe {
                walletName.value = it
            }
        compositeDisposable.add(disposable)
        return walletName
    }

    fun loadCredentials(walletDir: File, fileName: String): LiveData<Credentials> {
        val disposable = walletRepository
            .loadCredentials(walletDir, fileName)
            .subscribe { credentials.value = it }
        compositeDisposable.add(disposable)
        return credentials
    }

    fun sendEther(): MutableLiveData<TransactionReceipt> {
        val disposable = walletRepository
            .sendEther(web3j, credentials.value!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                transactionResult.value = it
            }, {
                Timber.d(it.localizedMessage)
            })
        compositeDisposable.add(disposable)
        return transactionResult
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}