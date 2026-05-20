package com.obrit.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtValueArgument

class NoDirectRunOnInViewModel(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Defect,
            description = "ViewModel must use reduceOn instead of calling Orbit runOn directly.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeName() != "runOn") return

        val containingClass = expression.parentOfType<KtClass>() ?: return
        val className = containingClass.name.orEmpty()
        if (!className.endsWith("ViewModel")) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = "Use reduceOn<UiState.Success> { ... } instead of runOn in $className.",
            ),
        )
    }
}

class ScreenContentNoLambdaParams(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.CodeSmell,
            description = "ScreenContent must receive state/action/modifier, not raw lambdas.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (!function.isComposable() || !function.name.orEmpty().endsWith("ScreenContent")) return

        function.valueParameters
            .filter { it.isFunctionType() }
            .forEach { parameter ->
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.from(parameter),
                        message = "${function.name} must not receive lambda parameter '${parameter.name}'. Pass a ScreenAction instead.",
                    ),
                )
            }
    }
}

class ScreenActionContract(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Defect,
            description = "ScreenAction must be internal, live in Screen.kt, and stop at SuccessContent.",
            debt = Debt.TEN_MINS,
        )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val className = klass.name.orEmpty()
        if (!className.endsWith("ScreenAction")) return

        if (!klass.hasModifier(KtTokens.INTERNAL_KEYWORD) || !klass.hasModifier(KtTokens.DATA_KEYWORD)) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(klass),
                    message = "$className must be declared as 'internal data class'.",
                ),
            )
        }

        val expectedFileName = className.removeSuffix("Action") + ".kt"
        val actualFileName = klass.containingKtFile.fileName()
        if (actualFileName != expectedFileName) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(klass),
                    message = "$className must be declared in $expectedFileName, not $actualFileName.",
                ),
            )
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (!function.isComposable()) return

        function.valueParameters
            .filter { it.typeText().endsWith("ScreenAction") }
            .filterNot { function.name.orEmpty().endsWith("ScreenContent") }
            .filterNot { function.name.orEmpty().endsWith("ScreenSuccessContent") }
            .forEach { parameter ->
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.from(parameter),
                        message = "${function.name} must not receive ${parameter.typeText()}. Pass only the lambdas it needs.",
                    ),
                )
            }
    }
}

class ScreenComposableFileContract(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.CodeSmell,
            description = "Screen composables must live in their matching screen files.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val functionName = function.name.orEmpty()
        if (!function.isComposable()) return
        if (!function.containingKtFile.normalizedPath().contains("/screen/")) return
        if (!functionName.isScreenComposableName()) return

        val expectedFileName = "$functionName.kt"
        val actualFileName = function.containingKtFile.fileName()
        if (actualFileName == expectedFileName) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(function),
                message = "$functionName must be declared in $expectedFileName, not $actualFileName.",
            ),
        )
    }
}

class NoViewModelInScreenContent(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Defect,
            description = "Screen content composables must not connect to ViewModels or Orbit collectors.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (!function.isComposable() || !function.name.orEmpty().endsWith("Content")) return

        function.valueParameters
            .filter { it.typeText().endsWith("ViewModel") }
            .forEach { parameter ->
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.from(parameter),
                        message = "${function.name} must not receive ViewModel parameter '${parameter.name}'. Keep ViewModel wiring in Screen.",
                    ),
                )
            }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val containingFunction = expression.parentOfType<KtNamedFunction>() ?: return
        if (!containingFunction.isComposable() || !containingFunction.name.orEmpty().endsWith("Content")) return

        val calleeName = expression.calleeName()
        if (calleeName !in forbiddenContentCalls) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = "${containingFunction.name} must not call $calleeName. Move ViewModel wiring to Screen.",
            ),
        )
    }

    private companion object {
        val forbiddenContentCalls = setOf("koinViewModel", "collectAsState", "collectSideEffect")
    }
}

class ScreenContentVisibility(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.CodeSmell,
            description = "Screen content composables are module-internal implementation details.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (!function.isComposable() || !function.name.orEmpty().endsWith("Content")) return
        if (function.hasModifier(KtTokens.INTERNAL_KEYWORD) || function.hasModifier(KtTokens.PRIVATE_KEYWORD)) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(function),
                message = "${function.name} must be internal unless it is a public screen entry point.",
            ),
        )
    }
}

class RemoteDataSourceImplementationVisibility(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Defect,
            description = "RemoteDataSource implementations must be internal.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val className = klass.name.orEmpty()
        if (!klass.isRemoteDataSourceImplementation()) return
        if (klass.hasModifier(KtTokens.INTERNAL_KEYWORD)) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(klass),
                message = "$className must be declared as 'internal class'.",
            ),
        )
    }
}

class RepositoryImplementationVisibility(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Defect,
            description = "Repository implementations must be internal.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val className = klass.name.orEmpty()
        if (!className.endsWith("RepositoryImpl")) return
        if (klass.hasModifier(KtTokens.INTERNAL_KEYWORD)) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(klass),
                message = "$className must be declared as 'internal class'.",
            ),
        )
    }
}

class RepositoryReturnTypeContract(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Defect,
            description = "Repository functions must return Result or Flow.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val containingClass = function.parentOfType<KtClass>() ?: return
        val className = containingClass.name.orEmpty()
        if (!className.isRepositoryTypeName()) return
        if (!function.isPublicApi()) return

        val returnType = function.returnTypeText()
        if (returnType.isRepositoryReturnType()) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(function),
                message = "$className.${function.name} must explicitly return Result<T> or Flow<T>.",
            ),
        )
    }
}

class RepositoryResultRunCatchingContract(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Defect,
            description = "Repository functions that return Result must call runCatchingWith.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val containingClass = function.parentOfType<KtClass>() ?: return
        val className = containingClass.name.orEmpty()
        if (!className.isRepositoryTypeName()) return
        if (!function.isPublicApi()) return
        if (!function.returnTypeText().isRepositoryResultReturnType()) return

        val bodyExpression = function.bodyExpression ?: return
        if (bodyExpression.containsCallNamed("runCatchingWith")) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(function),
                message = "$className.${function.name} must wrap Result<T> work with runCatchingWith.",
            ),
        )
    }
}

class NoTodoCallInMainSource(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Defect,
            description = "TODO calls must not remain in production source.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeName() != "TODO") return
        if (!expression.containingKtFile.isMainSource()) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = "Replace TODO() with production behavior before committing main source.",
            ),
        )
    }
}

class NoBangBangInMainSource(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Defect,
            description = "The not-null assertion operator is not allowed in production source.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitPostfixExpression(expression: KtPostfixExpression) {
        super.visitPostfixExpression(expression)

        if (expression.operationToken != KtTokens.EXCLEXCL) return
        if (!expression.containingKtFile.isMainSource()) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = "Replace '!!' with explicit null handling.",
            ),
        )
    }
}

class ModuleBuildScriptConvention(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.Defect,
            description = "Gradle modules must use OBRit convention plugins and keep SDK/toolchain config in build-logic.",
            debt = Debt.TEN_MINS,
        )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val file = expression.containingKtFile
        if (!file.isModuleBuildScript()) return

        when (expression.calleeName()) {
            "android" -> reportBuildScript(expression, "Move android { ... } configuration to build-logic and apply an obrit convention plugin.")
            "jvmToolchain",
            "compilerOptions",
            "compileOptions",
            -> reportBuildScript(expression, "Move Java/Kotlin toolchain configuration to build-logic.")
            "alias" -> reportRawPluginAlias(expression)
        }
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        val file = expression.containingKtFile
        if (!file.isModuleBuildScript()) return
        if (expression.operationToken != KtTokens.EQ) return

        val leftText = expression.left?.text.orEmpty()
        if (leftText in forbiddenToolchainAssignments) {
            reportBuildScript(expression, "Move $leftText configuration to build-logic.")
        }
    }

    private fun reportRawPluginAlias(expression: KtCallExpression) {
        val aliasText = expression.valueArguments.firstOrNull()?.argumentText().orEmpty()
        if (rawPluginAliases.none(aliasText::contains)) return

        reportBuildScript(
            expression,
            "Use an obrit convention plugin instead of $aliasText in module build scripts.",
        )
    }

    private fun reportBuildScript(
        element: PsiElement,
        message: String,
    ) {
        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(element),
                message = message,
            ),
        )
    }

    private companion object {
        val rawPluginAliases =
            setOf(
                "libs.plugins.androidApplication",
                "libs.plugins.androidLibrary",
                "libs.plugins.composeCompiler",
                "libs.plugins.kotlinAndroid",
                "libs.plugins.kotlinMultiplatform",
            )

        val forbiddenToolchainAssignments =
            setOf(
                "sourceCompatibility",
                "targetCompatibility",
                "jvmTarget",
                "compileSdk",
                "minSdk",
                "targetSdk",
            )
    }
}

class ProjectAccessorsOnly(config: Config) : Rule(config) {
    override val issue =
        Issue(
            id = javaClass.simpleName,
            severity = Severity.CodeSmell,
            description = "Module dependencies must use type-safe project accessors.",
            debt = Debt.FIVE_MINS,
        )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.containingKtFile.isBuildScript()) return
        if (expression.calleeName() != "project") return

        val firstArgumentText = expression.valueArguments.firstOrNull()?.argumentText().orEmpty()
        if (!firstArgumentText.startsWith("\":")) return

        report(
            CodeSmell(
                issue = issue,
                entity = Entity.from(expression),
                message = "Use type-safe project accessor instead of project($firstArgumentText).",
            ),
        )
    }
}

private fun KtCallExpression.calleeName(): String? = calleeExpression?.text

private fun KtValueArgument.argumentText(): String = getArgumentExpression()?.text.orEmpty()

private fun KtParameter.typeText(): String = typeReference?.text.orEmpty()

private fun KtParameter.isFunctionType(): Boolean = "->" in typeText()

private fun KtNamedFunction.isComposable(): Boolean =
    annotationEntries.any { annotation -> annotation.shortName?.asString() == "Composable" }

private fun KtNamedFunction.isPublicApi(): Boolean =
    !hasModifier(KtTokens.PRIVATE_KEYWORD) &&
        !hasModifier(KtTokens.PROTECTED_KEYWORD) &&
        !hasModifier(KtTokens.INTERNAL_KEYWORD)

private fun KtNamedFunction.returnTypeText(): String =
    typeReference
        ?.text
        .orEmpty()
        .replace(Regex("\\s+"), "")

private fun String.isRepositoryTypeName(): Boolean =
    endsWith("Repository") || endsWith("RepositoryImpl")

private fun String.isRepositoryReturnType(): Boolean =
    isRepositoryResultReturnType() ||
        startsWith("Flow<") ||
        startsWith("kotlinx.coroutines.flow.Flow<")

private fun String.isRepositoryResultReturnType(): Boolean =
    startsWith("Result<") ||
        startsWith("kotlin.Result<")

private fun KtClass.isRemoteDataSourceImplementation(): Boolean {
    val className = name.orEmpty()

    return className.endsWith("RemoteDataSourceImpl") ||
        superTypeListEntries.any { superType -> superType.text.endsWith("RemoteDataSource") }
}

private fun PsiElement.containsCallNamed(name: String): Boolean {
    val stack = ArrayDeque<PsiElement>()
    stack.add(this)

    while (stack.isNotEmpty()) {
        val element = stack.removeFirst()
        if (element is KtCallExpression && element.calleeName() == name) {
            return true
        }
        element.children.forEach(stack::addLast)
    }

    return false
}

private fun String.isScreenComposableName(): Boolean =
    endsWith("Screen") || (contains("Screen") && endsWith("Content"))

private fun KtFile.normalizedPath(): String = virtualFilePath.replace('\\', '/')

private fun KtFile.fileName(): String = normalizedPath().substringAfterLast("/")

private fun KtFile.isMainSource(): Boolean = "/src/main/" in normalizedPath()

private fun KtFile.isBuildScript(): Boolean = name.endsWith(".gradle.kts")

private fun KtFile.isModuleBuildScript(): Boolean {
    val path = normalizedPath()

    return isBuildScript() &&
        name == "build.gradle.kts" &&
        Regex(""".+/(android|shared)(/.+)?/build\.gradle\.kts$""").matches(path) &&
        "/build-logic/" !in path &&
        "/tools/detekt-rules/" !in path
}

private inline fun <reified T : PsiElement> PsiElement.parentOfType(): T? =
    generateSequence(parent) { element -> element.parent }
        .filterIsInstance<T>()
        .firstOrNull()
