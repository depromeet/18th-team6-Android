package com.obrit.feature.home.screen.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.home.viewmodel.Bucket
import com.obrit.feature.home.viewmodel.BucketLevel
import com.obrit.feature.home.viewmodel.BucketStatus
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun ConsumableUsageStatusSection(
    buckets: List<Bucket>,
    modifier: Modifier = Modifier,
) {
    if (buckets.isEmpty()) return
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
            buckets.forEach { bucket ->
                UsageStatusItem(bucket = bucket)
            }
        }
    }
}

@Composable
private fun UsageStatusItem(
    bucket: Bucket,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = AtomSpacing.S4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(AtomSpacing.S10.dp)
                    .background(color = colors.gray700, shape = CircleShape),
        )

        Text(
            text = bucket.title,
            style = typography.base.copy(fontWeight = FontWeight.Medium),
            color = colors.common00,
            modifier = Modifier.weight(1f),
        )

        DaysInUseText(daysInUse = bucket.daysInUse)

        Spacer(modifier = Modifier.size(AtomSpacing.S4.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = colors.common00,
            modifier = Modifier.size(AtomSpacing.S4.dp),
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
private fun ConsumableUsageStatusSectionPreview() {
    OBRitTheme {
        ConsumableUsageStatusSection(
            buckets =
                listOf(
                    Bucket(
                        BucketStatus.DANGER,
                        "면도기",
                        0,
                        "2026-05-23",
                        BucketLevel.NONE_OVERDUE,
                        82,
                    ),
                    Bucket(BucketStatus.DANGER, "칫솔", 1, "2026-05-26", BucketLevel.NONE_WARN, 82),
                    Bucket(BucketStatus.WARN, "수건", 0, "2026-05-22", BucketLevel.HAS_OVERDUE, 82),
                    Bucket(BucketStatus.WARN, "세탁망", 2, "2026-05-30", BucketLevel.HAS_WARN, 82),
                    Bucket(BucketStatus.WARN, "필터", 3, "2026-05-26", BucketLevel.NONE_SAFE, 82),
                    Bucket(BucketStatus.WARN, "화장솜", 5, "2026-06-06", BucketLevel.NONE_SAFE, 82),
                    Bucket(BucketStatus.WARN, "청소포", 2, "2026-05-21", BucketLevel.NONE_OVERDUE, 82),
                    Bucket(BucketStatus.WARN, "욕실매트", 4, "2026-06-10", BucketLevel.HAS_OVERDUE, 82),
                    Bucket(BucketStatus.WARN, "샴푸", 1, "2026-06-15", BucketLevel.HAS_SAFE, 82),
                ),
        )
    }
}
