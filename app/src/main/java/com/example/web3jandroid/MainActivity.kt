package com.example.web3jandroid

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.web3j.crypto.Credentials
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private val credentials: PublishSubject<File> = PublishSubject.create()
    @Inject
    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Web3jApplication.applicationComponent.inject(this)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val walletPath = filesDir.absolutePath
        val walletDir = File(walletPath)
        createWallet(walletDir).observe(this, Observer {
            loadCredentials(walletDir, it)
                .observe(this, Observer { it ->
                    Timber.d(it.address)
                })
        })
        request_ether.setOnClickListener {
            mainViewModel.sendEther()
        }
    }

    private fun createWallet(walletDir: File): LiveData<String> {
        return mainViewModel
            .getWallet(walletDir)
    }

    private fun loadCredentials(walletDir: File, path: String): MutableLiveData<Credentials> {
        return mainViewModel
            .loadCredentials(walletDir, path)
    }

}
