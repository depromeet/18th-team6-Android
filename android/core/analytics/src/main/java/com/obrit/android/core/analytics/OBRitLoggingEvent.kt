package com.obrit.android.core.analytics

@Suppress("TooManyFunctions")
sealed class OBRitLoggingEvent(
    val name: String,
    val params: Map<String, Any?> = emptyMap(),
) {
    data class InitialLaunch(
        val anonymousUserId: String,
        val occurredAt: Long,
    ) : OBRitLoggingEvent(
            name = "initial_launch",
            params =
                mapOf(
                    "anonymous_user_id" to anonymousUserId,
                    "occurred_at" to occurredAt,
                ),
        )

    data object OnboardingStart : OBRitLoggingEvent("onboarding_start")

    data class OnboardingComplete(
        val selectionCount: Int,
        val durationMs: Long,
        val success: Boolean,
    ) : OBRitLoggingEvent(
            name = "onboarding_complete",
            params =
                mapOf(
                    "selection_count" to selectionCount,
                    "duration_ms" to durationMs,
                    "success" to success.toString(),
                ),
        )

    data class RegisterMethodSelect(
        val method: RegisterMethod,
    ) : OBRitLoggingEvent(
            name = "register_method_select",
            params = mapOf("method" to method.value),
        )

    data object ManualRegisterStart : OBRitLoggingEvent("manual_register_start")

    data class ManualRegisterComplete(
        val durationMs: Long,
    ) : OBRitLoggingEvent(
            name = "manual_register_complete",
            params = mapOf("duration_ms" to durationMs),
        )

    data class ManualRegisterFail(
        val failureType: String,
    ) : OBRitLoggingEvent(
            name = "manual_register_fail",
            params = mapOf("failure_type" to failureType),
        )

    data object ReceiptAnalyzeStart : OBRitLoggingEvent("receipt_analyze_start")

    data class ReceiptAnalyzeComplete(
        val processingMs: Long,
        val recognizedCount: Int,
    ) : OBRitLoggingEvent(
            name = "receipt_analyze_complete",
            params =
                mapOf(
                    "processing_ms" to processingMs,
                    "recognized_count" to recognizedCount,
                ),
        )

    data class ReceiptAnalyzeFail(
        val processingMs: Long,
        val failureType: String,
    ) : OBRitLoggingEvent(
            name = "receipt_analyze_fail",
            params =
                mapOf(
                    "processing_ms" to processingMs,
                    "failure_type" to failureType,
                ),
        )

    data class ReceiptBulkRegisterComplete(
        val registeredCount: Int,
    ) : OBRitLoggingEvent(
            name = "receipt_bulk_register_complete",
            params = mapOf("registered_count" to registeredCount),
        )

    data class ReceiptBulkRegisterFail(
        val failureType: String,
    ) : OBRitLoggingEvent(
            name = "receipt_bulk_register_fail",
            params = mapOf("failure_type" to failureType),
        )

    data class HomePageView(
        val consumableCount: Int,
    ) : OBRitLoggingEvent(
            name = "home_page_view",
            params = mapOf("consumable_count" to consumableCount),
        )

    data class ListPageView(
        val consumableCount: Int,
    ) : OBRitLoggingEvent(
            name = "list_page_view",
            params = mapOf("consumable_count" to consumableCount),
        )

    data class SearchPageView(
        val consumableCount: Int,
    ) : OBRitLoggingEvent(
            name = "search_page_view",
            params = mapOf("consumable_count" to consumableCount),
        )

    data class DetailPageView(
        val consumableId: String,
    ) : OBRitLoggingEvent(
            name = "detail_page_view",
            params = mapOf("consumable_id" to consumableId),
        )

    data class ReplacementSuccess(
        val consumableId: String,
        val daysElapsed: Int,
    ) : OBRitLoggingEvent(
            name = "replacement_success",
            params =
                mapOf(
                    "consumable_id" to consumableId,
                    "days_elapsed" to daysElapsed,
                ),
        )

    data class ReplacementFail(
        val consumableId: String,
        val failureType: String,
    ) : OBRitLoggingEvent(
            name = "replacement_fail",
            params =
                mapOf(
                    "consumable_id" to consumableId,
                    "failure_type" to failureType,
                ),
        )

    data class SpareEditSuccess(
        val consumableId: String,
    ) : OBRitLoggingEvent(
            name = "spare_edit_success",
            params = mapOf("consumable_id" to consumableId),
        )

    data class SpareEditFail(
        val consumableId: String,
        val failureType: String,
    ) : OBRitLoggingEvent(
            name = "spare_edit_fail",
            params = mapOf("consumable_id" to consumableId, "failure_type" to failureType),
        )

    data class DeleteSuccess(
        val consumableId: String,
    ) : OBRitLoggingEvent(
            name = "delete_success",
            params = mapOf("consumable_id" to consumableId),
        )

    data class DeleteFail(
        val consumableId: String,
        val failureType: String,
    ) : OBRitLoggingEvent(
            name = "delete_fail",
            params = mapOf("consumable_id" to consumableId, "failure_type" to failureType),
        )

    data class DetailEditSuccess(
        val consumableId: String,
    ) : OBRitLoggingEvent(
            name = "detail_edit_success",
            params = mapOf("consumable_id" to consumableId),
        )

    data class DetailEditFail(
        val consumableId: String,
        val failureType: String,
    ) : OBRitLoggingEvent(
            name = "detail_edit_fail",
            params = mapOf("consumable_id" to consumableId, "failure_type" to failureType),
        )

    enum class RegisterMethod(
        val value: String,
    ) {
        DIRECT("manual_register"),
        RECEIPT("receipt_register"),
    }
}
