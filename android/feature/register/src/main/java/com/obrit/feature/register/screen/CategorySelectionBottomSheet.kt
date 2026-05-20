package com.obrit.feature.register.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.bottomsheet.OBRitBottomSheet
import com.obrit.android.core.designsystem.component.button.FilledButtonColor
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.radiobutton.OBRitRadioButton
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategorySelectionBottomSheet(
    initialSelected: String,
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.Transparent,
        shape = RectangleShape,
        modifier = modifier,
    ) {
        CategorySelectionBottomSheetContent(
            initialSelected = initialSelected,
            onConfirm = onConfirm,
        )
    }
}

@Composable
private fun CategorySelectionBottomSheetContent(
    initialSelected: String,
    onConfirm: (String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedName by rememberSaveable(initialSelected) { mutableStateOf(initialSelected) }
    val filtered =
        remember(query) {
            if (query.isBlank()) {
                MockCategories
            } else {
                MockCategories.filter { it.contains(query, ignoreCase = true) }
            }
        }

    val colors = LocalOBRitColor.current
    val configuration = LocalConfiguration.current
    val sheetHeight = (configuration.screenHeightDp.dp - CategorySheetTopGapFromScreen)
        .coerceAtLeast(CategorySheetMinHeight)

    val listState = rememberLazyListState()
    // M3 ModalBottomSheet의 AnchoredDraggable이 LazyColumn의 잔여 fling 속도를 받아 진동하는 것을 차단.
    // LazyColumn이 스크롤 가능한 동안엔 fling을 전부 소비해 sheet의 nestedScroll로 전파하지 않음.
    // 리스트가 끝까지 갔을 때만 over-scroll fling이 sheet로 전파되어 swipe-to-dismiss는 정상 동작.
    val listFlingGuard =
        remember(listState) {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset = Offset.Zero

                override suspend fun onPreFling(available: Velocity): Velocity =
                    if (listState.canScrollForward || listState.canScrollBackward) {
                        available
                    } else {
                        Velocity.Zero
                    }
            }
        }

    OBRitBottomSheet(
        modifier = Modifier
            .height(sheetHeight)
            .imePadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CategorySearchField(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(CategorySheetSectionGap))

            CategoryCountTitle(count = filtered.size)

            Spacer(modifier = Modifier.height(CategorySheetTitleGap))

            // 리스트 영역 — CTA 바로 위까지만, CTA 아래에는 리스트가 침범하지 않음
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(listFlingGuard),
                    verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
                    contentPadding = PaddingValues(bottom = CategoryListBottomFade),
                ) {
                    items(filtered, key = { it }) { name ->
                        CategoryListItem(
                            name = name,
                            addedCount = 0,
                            selected = name == selectedName,
                            onClick = { selectedName = name },
                        )
                    }
                }

                // 리스트 하단 fade — 스크롤 중 아이템이 CTA 직전에서 부드럽게 사라지는 효과
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(CategoryListBottomFade)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, colors.gray900),
                            ),
                        ),
                )
            }

            // CTA — 리스트 영역과 분리되어 그 아래에 별도 배치
            OBRitLargeFilledButton(
                onClick = { onConfirm(selectedName) },
                enabled = selectedName.isNotBlank(),
                color = FilledButtonColor.Green,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = CategoryCtaBottomPadding),
            ) { contentColor ->
                Text(
                    text = CategorySheetCtaLabel,
                    style =
                        LocalOBRitTypography.current.xl.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    color = contentColor,
                )
            }
        }
    }
}

@Composable
private fun CategoryCountTitle(count: Int) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val text =
        buildAnnotatedString {
            withStyle(SpanStyle(color = colors.common00)) {
                append(CategorySheetTitlePrefix)
            }
            withStyle(SpanStyle(color = colors.green300)) {
                append(CategorySheetCountFormat.format(count))
            }
        }
    Text(
        text = text,
        style = typography.xl3.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun CategoryListItem(
    name: String,
    addedCount: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = AtomSpacing.S5.dp,
                    vertical = AtomSpacing.S4.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(CategoryItemImageSize)
                    .clip(CircleShape)
                    .background(colors.gray750),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
        ) {
            Text(
                text = name,
                style =
                    typography.xl.copy(
                        color = colors.common00,
                        fontWeight = FontWeight.Bold,
                    ),
                maxLines = 1,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S0_5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = CategorySheetAddedCountLabel,
                    style =
                        typography.s.copy(
                            color = colors.gray400,
                            fontWeight = FontWeight.Medium,
                        ),
                )
                Text(
                    text = CategorySheetCountFormat.format(addedCount),
                    style =
                        typography.s.copy(
                            color = colors.common00,
                            fontWeight = FontWeight.SemiBold,
                        ),
                )
            }
        }
        OBRitRadioButton(
            selected = selected,
            onClick = onClick,
        )
    }
}

// OBRitTopBar.kt의 TopBarSearchInput과 동일 디자인 (디테일 변경 시 양쪽 함께 수정).
// Figma SSOT에 정식 컴포넌트 등록되면 designsystem으로 추출 예정.
@Composable
private fun CategorySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val textStyle =
        typography.xl.copy(
            fontWeight = FontWeight.Medium,
            color = colors.common00,
        )
    val placeholderStyle = textStyle.copy(color = colors.gray700)

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            modifier
                .clip(RoundedCornerShape(AtomRadius.Middle.dp))
                .border(
                    width = CategorySearchBorderWidth,
                    color = colors.gray300,
                    shape = RoundedCornerShape(AtomRadius.Middle.dp),
                ).padding(
                    horizontal = AtomSpacing.S5.dp,
                    vertical = AtomSpacing.S4.dp,
                ),
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(colors.common00),
    ) { innerTextField ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = CategorySheetSearchPlaceholder,
                        style = placeholderStyle,
                        maxLines = 1,
                    )
                }
                innerTextField()
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_topbar_search),
                contentDescription = null,
                tint = colors.common00,
                modifier = Modifier.size(AtomSpacing.S6.dp),
            )
        }
    }
}

private const val CategorySheetSearchPlaceholder = "원하시는 소모품을 검색해보세요"
private const val CategorySheetTitlePrefix = "전체 소모품 "
private const val CategorySheetCountFormat = "%d개"
private const val CategorySheetAddedCountLabel = "추가된 소모품"
private const val CategorySheetCtaLabel = "소모품 종류 선택하기"

// Figma: 디바이스 최상단부터 시트 상단까지 85dp (status bar 포함).
// 디바이스마다 status bar 높이가 달라 시각적으로 미세한 차이는 발생할 수 있음 (기종 차이로 수용).
private val CategorySheetTopGapFromScreen = 85.dp
private val CategorySheetMinHeight = 400.dp

private val CategorySheetSectionGap = AtomSpacing.S8.dp
private val CategorySheetTitleGap = AtomSpacing.S3.dp
private val CategoryItemImageSize = 52.dp

// 리스트 하단 fade gradient 높이 — 스크롤되는 아이템이 부드럽게 사라지는 영역
// LazyColumn contentPadding(bottom)으로도 동일 값을 사용해 마지막 아이템이 fade 위에서 정지
private val CategoryListBottomFade = 40.dp

// CTA 아래 ~ 시트 바닥 총 40dp.
// OBRitBottomSheet 자체가 pb=20을 가지므로, 여기서 20dp만 추가하면 합쳐서 40dp.
// (디바이스 navigation bar safe area는 ModalBottomSheet의 windowInsets가 자동 처리)
private val CategoryCtaBottomPadding = AtomSpacing.S5.dp

private val CategorySearchBorderWidth = 1.4f.dp

private val MockCategories =
    listOf(
        "면도기",
        "정수기 필터",
        "칫솔",
        "치약",
        "세탁 세제",
        "수건",
        "샤워기 필터",
        "수세미",
        "주방세제",
        "휴지",
    )

@Preview(name = "CategorySelectionBottomSheet Empty", showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun CategorySelectionBottomSheetEmptyPreview() {
    OBRitTheme(dynamicColor = false) {
        CategorySelectionBottomSheetContent(
            initialSelected = "",
            onConfirm = {},
        )
    }
}

@Preview(name = "CategorySelectionBottomSheet Selected", showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun CategorySelectionBottomSheetSelectedPreview() {
    OBRitTheme(dynamicColor = false) {
        CategorySelectionBottomSheetContent(
            initialSelected = "정수기 필터",
            onConfirm = {},
        )
    }
}
