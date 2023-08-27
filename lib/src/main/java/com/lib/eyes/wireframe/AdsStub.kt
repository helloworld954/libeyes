package com.lib.eyes.wireframe

import com.lib.eyes.ShowParam

class AdsStubParam(override val showCallback: ShowCallback?) : ShowParam

class AdsStub : AdsInterface<AdsStubParam> {
    override fun show(param: AdsStubParam) {

    }
}