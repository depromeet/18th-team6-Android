package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import com.obrit.feature.register.screen.complete.RegisterCompleteScreen
import com.obrit.feature.register.screen.manual.ManualRegisterNavigation
import com.obrit.feature.register.screen.manual.ManualRegisterScreen
import com.obrit.feature.register.screen.receipt.ReceiptCameraScreen
import com.obrit.feature.register.screen.receipt.ReceiptDetailNavigation
import com.obrit.feature.register.screen.receipt.ReceiptDetailScreen
import com.obrit.feature.register.screen.receipt.ReceiptResultNavigation
import com.obrit.feature.register.screen.receipt.ReceiptResultScreen
import com.obrit.feature.register.viewmodel.ReceiptDraftItem
import com.obrit.obrit.navigation.route.RegisterRoute
import com.obrit.obrit.shared.model.receipts.ReceiptAnalysis

@Composable
@Suppress("LongMethod")
fun RegisterNavigation(
    onExit: () -> Unit,
    onManualRegister: (itemName: String) -> Unit,
    modifier: Modifier = Modifier,
    startDestination: RegisterRoute = RegisterRoute.ManualRegister,
) {
    val registerBackStack = rememberNavBackStack(startDestination)
    // 영수증 분석 결과(Camera→Result)와 등록 초안(Result→Detail)을 화면 간 전달하기 위한 hoist 상태.
    // 백스택은 복원되는데 이 값만 사라지면 회전이나 프로세스 종료 뒤 상세 화면이 비고 결과 화면이 하얗게 남는다.
    // 그래서 백스택과 같이 저장한다. 결과 화면에서 한 삭제와 추가는 ViewModel에만 있어 프로세스 종료 뒤에는 초기화된다.
    var receiptAnalysis by rememberSaveable(stateSaver = ReceiptAnalysisSaver) {
        mutableStateOf<ReceiptAnalysis?>(null)
    }
    var receiptDraftItems by rememberSaveable(stateSaver = ReceiptDraftItemsSaver) {
        mutableStateOf<List<ReceiptDraftItem>>(emptyList())
    }
    var receiptImageUrl by rememberSaveable { mutableStateOf("") }

    OBRitNavDisplay(
        backStack = registerBackStack,
        modifier = modifier,
        onBack = { registerBackStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<RegisterRoute.ManualRegister> {
                    ManualRegisterScreen(
                        navigation =
                            ManualRegisterNavigation(
                                onBack = onExit,
                                onRegistered = onManualRegister,
                            ),
                        modifier = Modifier,
                    )
                }
                entry<RegisterRoute.RegisterComplete> {
                    RegisterCompleteScreen(
                        onExit = onExit,
                        modifier = Modifier,
                    )
                }
                // 좌상단 X → onExit으로 register 그래프를 pop 하여 홈으로 복귀한다.
                entry<RegisterRoute.ReceiptCamera> {
                    ReceiptCameraScreen(
                        onClose = onExit,
                        onAnalysisComplete = { analysis ->
                            receiptAnalysis = analysis
                            registerBackStack.add(RegisterRoute.ReceiptResult)
                        },
                        modifier = Modifier,
                    )
                }
                entry<RegisterRoute.ReceiptResult> {
                    val analysis = receiptAnalysis
                    if (analysis != null) {
                        ReceiptResultScreen(
                            analysis = analysis,
                            navigation =
                                ReceiptResultNavigation(
                                    onBack = { registerBackStack.removeLastOrNull() },
                                    onNext = { imageUrl, items ->
                                        receiptImageUrl = imageUrl
                                        // 직전 Detail 방문에서 보존된 편집값을 id 기준으로 병합한다.
                                        // 살아있는 항목은 편집값 복원, 새 항목은 fresh, 삭제 항목은 자연 폐기.
                                        val saved = receiptDraftItems.associateBy { it.id }
                                        receiptDraftItems = items.map { item -> saved[item.id] ?: item }
                                        registerBackStack.add(RegisterRoute.ReceiptDetail)
                                    },
                                ),
                            modifier = Modifier,
                        )
                    }
                }
                entry<RegisterRoute.ReceiptDetail> {
                    ReceiptDetailScreen(
                        items = receiptDraftItems,
                        receiptImageUrl = receiptImageUrl,
                        navigation =
                            ReceiptDetailNavigation(
                                onBack = { registerBackStack.removeLastOrNull() },
                                onComplete = {
                                    registerBackStack.add(RegisterRoute.RegisterComplete)
                                },
                                onFormsChange = { forms -> receiptDraftItems = forms },
                            ),
                        modifier = Modifier,
                    )
                }
            },
    )
}
