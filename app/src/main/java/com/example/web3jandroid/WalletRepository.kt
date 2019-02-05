package com.example.web3jandroid

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import java.io.File

class WalletRepository {

    fun getWallet(walletDir: File): Observable<String> {
        return Observable
            .just(WalletUtils.generateLightNewWalletFile("walletpassword", walletDir))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun loadCredentials(walletDir: File, fileName: String): Observable<Credentials> {
        return Observable
            .just(WalletUtils.loadCredentials("walletpassword", walletDir.toString() + "/" + fileName))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}