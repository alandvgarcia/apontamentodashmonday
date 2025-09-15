package com.solinftec.apontamentosmondaydash

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform