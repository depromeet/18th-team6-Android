package com.obrit.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class ObritRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "obrit"

    override fun instance(config: Config): RuleSet =
        RuleSet(
            ruleSetId,
            listOf(
                NoDirectRunOnInViewModel(config),
                ScreenContentNoLambdaParams(config),
                ScreenActionContract(config),
                ScreenComposableFileContract(config),
                NoViewModelInScreenContent(config),
                ScreenContentVisibility(config),
                NoTodoCallInMainSource(config),
                NoBangBangInMainSource(config),
                ModuleBuildScriptConvention(config),
                ProjectAccessorsOnly(config),
            ),
        )
}
