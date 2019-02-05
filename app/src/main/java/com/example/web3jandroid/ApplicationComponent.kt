package com.example.web3jandroid

import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [(ApplicationModule::class)]
)
@Singleton
interface ApplicationComponent {
    fun inject(web3jApplication: Web3jApplication)
    fun inject(mainViewModel: MainViewModel)
    fun inject(mainActivity: MainActivity)
}
