package com.lib.eyes.application

import android.app.Application
import com.example.wireframe.application.ApplicationDelegation
import com.libeye.admob.AdmobApplicationDelegate

fun createDelegation(application: Application, listTestDevice: List<String> = listOf()): ApplicationDelegation = AdmobApplicationDelegate(application, listTestDevice)

open class AdmobApplication : Application()