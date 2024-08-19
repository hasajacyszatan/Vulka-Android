package io.github.vulka.impl.vulcan.prometheus.hebece

import io.github.vulka.impl.vulcan.hebe.HebeHttpClient
import io.github.vulka.impl.vulcan.hebe.VulcanHebeApi
import io.github.vulka.impl.vulcan.hebe.VulcanHebeLoginCredentials
import io.github.vulka.impl.vulcan.prometheus.VulcanPrometheusLoginCredentials

open class VulcanHebeCeApi : VulcanHebeApi() {
    protected open fun setup(credentials: VulcanPrometheusLoginCredentials) {
        client = HebeHttpClient(credentials.keystore)
        this.credentials = VulcanHebeLoginCredentials(
            account = credentials.account,
            keystore = credentials.keystore
        )
    }
}