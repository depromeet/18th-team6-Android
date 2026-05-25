package com.obrit.obrit.shared.model.home

data class HomeOverallStatus(
    val replacement: HomeStatusLevel,
    val spare: HomeStatusLevel,
    val overall: HomeOverallLevel,
)
