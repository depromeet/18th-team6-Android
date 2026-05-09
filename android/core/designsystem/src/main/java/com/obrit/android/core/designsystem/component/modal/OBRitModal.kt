package com.obrit.android.core.designsystem.component.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.button.FilledButtonColor
import com.obrit.android.core.designsystem.component.button.OBRitMiddleFilledTextButton
import com.obrit.android.core.designsystem.component.dim.OBRitDim
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

enum class OBRitModalAppearance {
    Light,
    Dark,
}

@Composable
@Suppress("LongParameterList")
fun OBRitModal(
    title: String,
    description: String,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    appearance: OBRitModalAppearance = OBRitModalAppearance.Light,
    showImage: Boolean = true,
    secondaryButtonText: String? = null,
    onSecondaryButtonClick: (() -> Unit)? = null,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val colorScheme = obritModalColorScheme(colors = colors, appearance = appearance)

    Column(
        modifier =
            modifier
                .width(OBRitModalWidth)
                .clip(OBRitModalShape)
                .background(colorScheme.containerColor)
                .padding(
                    horizontal = OBRitModalHorizontalPadding,
                    vertical = OBRitModalVerticalPadding,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(OBRitModalContentGap),
    ) {
        if (showImage) {
            OBRitModalSmallImage()
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(OBRitModalTextAndActionGap),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(OBRitModalTextGap),
            ) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    style =
                        typography.xl3.copy(
                            color = colorScheme.titleColor,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        ),
                    maxLines = OBRitModalTextMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = description,
                    modifier = Modifier.fillMaxWidth(),
                    style =
                        typography.base.copy(
                            color = colorScheme.descriptionColor,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                        ),
                    maxLines = OBRitModalTextMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(OBRitModalButtonGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (secondaryButtonText != null && onSecondaryButtonClick != null) {
                    OBRitModalGrayFilledTextButton(
                        text = secondaryButtonText,
                        onClick = onSecondaryButtonClick,
                        appearance = appearance,
                        modifier = Modifier.width(OBRitModalSecondaryButtonWidth),
                    )
                }
                OBRitMiddleFilledTextButton(
                    text = primaryButtonText,
                    onClick = onPrimaryButtonClick,
                    color = colorScheme.primaryButtonColor,
                    modifier =
                        if (secondaryButtonText != null && onSecondaryButtonClick != null) {
                            Modifier.weight(1f)
                        } else {
                            Modifier.fillMaxWidth()
                        },
                )
            }
        }
    }
}

@Composable
private fun OBRitModalGrayFilledTextButton(
    text: String,
    onClick: () -> Unit,
    appearance: OBRitModalAppearance,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val colorScheme = obritModalColorScheme(colors = colors, appearance = appearance)

    Box(
        modifier =
            modifier
                .clip(OBRitModalButtonShape)
                .background(colorScheme.secondaryButtonContainerColor)
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(
                    horizontal = OBRitModalSecondaryButtonHorizontalPadding,
                    vertical = OBRitModalSecondaryButtonVerticalPadding,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style =
                typography.xl.copy(
                    color = colorScheme.secondaryButtonTextColor,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun OBRitModalSmallImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_sample_img),
        contentDescription = null,
        modifier =
            modifier
                .size(OBRitModalImageSize),
        contentScale = ContentScale.Fit,
    )
}

@Preview(
    name = "OBRitModal Light Small Image",
    showBackground = true,
)
@Composable
private fun OBRitModalPreview() {
    OBRitModalPreviewContainer {
        OBRitModal(
            title = "Title Text\n최대 두 줄까지 작성 가능합니다.",
            description = "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
            primaryButtonText = "CTA",
            onPrimaryButtonClick = {},
            showImage = true,
            secondaryButtonText = "CTA",
            onSecondaryButtonClick = {},
        )
    }
}

@Preview(
    name = "OBRitModal Light One Button",
    showBackground = true,
)
@Composable
private fun OBRitModalSingleButtonPreview() {
    OBRitModalPreviewContainer {
        OBRitModal(
            title = "Title Text\n최대 두 줄까지 작성 가능합니다.",
            description = "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
            primaryButtonText = "CTA",
            onPrimaryButtonClick = {},
            showImage = true,
        )
    }
}

@Preview(
    name = "OBRitModal Light No Image",
    showBackground = true,
)
@Composable
private fun OBRitModalWithoutImagePreview() {
    OBRitModalPreviewContainer {
        OBRitModal(
            title = "Title Text\n최대 두 줄까지 작성 가능합니다.",
            description = "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
            primaryButtonText = "CTA",
            onPrimaryButtonClick = {},
            showImage = false,
            secondaryButtonText = "CTA",
            onSecondaryButtonClick = {},
        )
    }
}

@Preview(
    name = "OBRitModal Dark Small Image",
    showBackground = true,
)
@Composable
private fun OBRitModalDarkPreview() {
    OBRitModalPreviewContainer {
        OBRitModal(
            title = "Title Text\n최대 두 줄까지 작성 가능합니다.",
            description = "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
            primaryButtonText = "CTA",
            onPrimaryButtonClick = {},
            appearance = OBRitModalAppearance.Dark,
            showImage = true,
            secondaryButtonText = "CTA",
            onSecondaryButtonClick = {},
        )
    }
}

@Preview(
    name = "OBRitModal Dark One Button",
    showBackground = true,
)
@Composable
private fun OBRitModalDarkSingleButtonPreview() {
    OBRitModalPreviewContainer {
        OBRitModal(
            title = "Title Text\n최대 두 줄까지 작성 가능합니다.",
            description = "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
            primaryButtonText = "CTA",
            onPrimaryButtonClick = {},
            appearance = OBRitModalAppearance.Dark,
            showImage = true,
        )
    }
}

@Preview(
    name = "OBRitModal Dark No Image Two Buttons",
    showBackground = true,
)
@Composable
private fun OBRitModalDarkWithoutImagePreview() {
    OBRitModalPreviewContainer {
        OBRitModal(
            title = "Title Text\n최대 두 줄까지 작성 가능합니다.",
            description = "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
            primaryButtonText = "CTA",
            onPrimaryButtonClick = {},
            appearance = OBRitModalAppearance.Dark,
            showImage = false,
            secondaryButtonText = "CTA",
            onSecondaryButtonClick = {},
        )
    }
}

@Composable
private fun OBRitModalPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            OBRitDim()
            content()
        }
    }
}

private fun obritModalColorScheme(
    colors: OBRitColor,
    appearance: OBRitModalAppearance,
): OBRitModalColorScheme =
    when (appearance) {
        OBRitModalAppearance.Light ->
            OBRitModalColorScheme(
                containerColor = colors.common00,
                titleColor = colors.gray900,
                descriptionColor = colors.gray600,
                secondaryButtonContainerColor = colors.gray100,
                secondaryButtonTextColor = colors.gray700,
                primaryButtonColor = FilledButtonColor.Green,
            )
        OBRitModalAppearance.Dark ->
            OBRitModalColorScheme(
                containerColor = colors.gray800,
                titleColor = colors.common00,
                descriptionColor = colors.gray300,
                secondaryButtonContainerColor = colors.gray750,
                secondaryButtonTextColor = colors.gray300,
                primaryButtonColor = FilledButtonColor.Green,
            )
    }

private val OBRitModalWidth = 370.dp
private val OBRitModalShape = RoundedCornerShape(AtomRadius.ExtraLarge.dp)
private val OBRitModalHorizontalPadding = 28.dp
private val OBRitModalVerticalPadding = AtomSpacing.S6.dp
private val OBRitModalContentGap = AtomSpacing.S2_5.dp
private val OBRitModalTextAndActionGap = AtomSpacing.S4.dp
private val OBRitModalTextGap = AtomSpacing.S2.dp
private val OBRitModalButtonGap = AtomSpacing.S3.dp
private val OBRitModalButtonShape = RoundedCornerShape(AtomRadius.Middle.dp)
private val OBRitModalSecondaryButtonWidth = 82.dp
private val OBRitModalSecondaryButtonHorizontalPadding = 20.dp
private val OBRitModalSecondaryButtonVerticalPadding = 12.dp
private val OBRitModalImageSize = 68.dp
private const val OBRitModalTextMaxLines = 2

private data class OBRitModalColorScheme(
    val containerColor: Color,
    val titleColor: Color,
    val descriptionColor: Color,
    val secondaryButtonContainerColor: Color,
    val secondaryButtonTextColor: Color,
    val primaryButtonColor: FilledButtonColor,
)
