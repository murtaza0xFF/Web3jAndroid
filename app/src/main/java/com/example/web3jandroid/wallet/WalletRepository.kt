package com.example.web3jandroid.wallet

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import timber.log.Timber
import java.io.File
import java.math.BigDecimal

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

    fun sendEther(web3j: Web3j, credentials: Credentials): Flowable<TransactionReceipt> {
        return Transfer.sendFunds(
            web3j, credentials,
            "0x45083547dfA72404452EBE4AFa4A11878406F2e0",
            BigDecimal.ONE, Convert.Unit.WEI
        ).flowable()
    }
}