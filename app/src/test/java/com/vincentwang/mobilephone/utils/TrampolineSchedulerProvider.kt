package com.vincentwang.mobilephone.utils

import com.vincentwang.mobilephone.utils.SchedulerProvider
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class TrampolineSchedulerProvider:SchedulerProvider {
    override fun io() = Schedulers.trampoline()

    override fun ui() = Schedulers.trampoline()

    override fun computation() = Schedulers.computation()
}