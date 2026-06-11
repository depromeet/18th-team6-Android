package com.obrit.feature.home.screen.homeSection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.home.HomeItemCard
import com.obrit.obrit.shared.model.home.ItemBucket

@Composable
internal fun ItemUsageStatusSection(
    items: List<HomeItemCard>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current

    Column(
        modifier = modifier.padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S5.dp),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
    ) {
        Text(
            text = "사용 현황",
            style = typography.xl.copy(fontWeight = FontWeight.Bold),
            color = colors.common00,
        )
        Column {
            items.forEach { item ->
                UsageStatusItem(item = item, onItemClick = onItemClick)
            }
        }
    }
}

@Composable
private fun UsageStatusItem(
    item: HomeItemCard,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onItemClick(item.itemId) }
                .padding(vertical = AtomSpacing.S4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
    ) {
        AsyncImage(
            model = item.iconUrl,
            contentDescription = null,
            modifier =
                Modifier
                    .size(AtomSpacing.S10.dp)
                    .clip(CircleShape)
                    .background(color = colors.gray700),
        )

        Text(
            text = item.name,
            style = typography.base.copy(fontWeight = FontWeight.Medium),
            color = colors.common00,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        DaysInUseText(daysInUse = item.daysInUse)

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = colors.common00,
            modifier = Modifier.padding(start = AtomSpacing.S1.dp).size(AtomSpacing.S4.dp),
        )
    }
}

@Composable
private fun DaysInUseText(daysInUse: Int) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Text(
        text =
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colors.common00)) {
                    append("${daysInUse}일")
                }
                withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = colors.gray300)) {
                    append("째 사용중")
                }
            },
        style = typography.base,
    )
}

@Suppress("MagicNumber")
@Preview(showBackground = true, backgroundColor = 0xFF1D1B20, widthDp = 412)
@Composable
private fun ItemUsageStatusSectionPreview() {
    OBRitTheme {
        ItemUsageStatusSection(
            items =
                listOf(
                    HomeItemCard(1L, "면도기", 82, "교체 D-3", 0, "", ItemBucket.NONE_OVERDUE),
                    HomeItemCard(2L, "칫솔", 82, "교체 D-day", 1, "", ItemBucket.NONE_WARN),
                    HomeItemCard(3L, "수건", 82, "교체 D+5", 0, "", ItemBucket.HAS_OVERDUE),
                    HomeItemCard(4L, "세탁망", 82, "교체 D-7", 2, "", ItemBucket.HAS_WARN),
                    HomeItemCard(5L, "필터", 82, "교체 D-10", 3, "", ItemBucket.NONE_SAFE),
                ),
            onItemClick = {},
        )
    }
}
