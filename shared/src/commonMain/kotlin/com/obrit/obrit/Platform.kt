package com.obrit.obrit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform