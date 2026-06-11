import SwiftUI

struct ItemKindSelectionBottomSheet: View {
    let data: ItemRegistrationViewData
    let action: ItemRegistrationAction
    let contentHeight: CGFloat
    let bottomPadding: CGFloat

    var body: some View {
        let filteredKinds = data.filteredKinds

        OBRitBottomSheet(
            contentHeight: contentHeight,
            bottomPadding: bottomPadding,
            onDismiss: action.onDismissBottomSheet
        ) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s8) {
                ItemSearchInputField(
                    text: data.kindSearchQuery,
                    placeholder: "원하시는 소모품을 검색해보세요",
                    onTextChange: action.onUpdateKindSearchQuery
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                    ItemSheetCountText(
                        prefix: data.kindSearchQuery.isEmpty ? "전체 소모품" : "검색 결과",
                        count: data.kindSearchQuery.isEmpty ? data.itemKinds.count : filteredKinds.count
                    )

                    if filteredKinds.isEmpty {
                        ItemKindNoResultView(action: action)
                    } else {
                        ScrollView(showsIndicators: false) {
                            LazyVStack(spacing: OBRitSpacing.s2) {
                                ForEach(filteredKinds) { kind in
                                    ItemKindSelectionRow(
                                        kind: kind,
                                        selected: kind == data.kindCandidateForDisplay,
                                        action: action
                                    )
                                }
                            }
                            .padding(.bottom, ItemRegistrationLayout.sheetBottomButtonReservedHeight + OBRitSpacing.s6)
                        }
                        .overlay(alignment: .bottom) {
                            ItemSheetGradientButton(
                                text: "소모품 종류 선택하기",
                                enabled: data.selectedKindCandidate != nil,
                                action: action.onConfirmKindSelection
                            )
                        }
                    }
                }
            }
        }
    }
}

private struct ItemSheetCountText: View {
    let prefix: String
    let count: Int

    var body: some View {
        (
            Text(prefix + ItemRegistrationText.space)
                .foregroundColor(OBRitColors.common00) +
                Text(ItemRegistrationText.countText(count))
                .foregroundColor(OBRitColors.green300)
        )
        .font(OBRitTypography.font(OBRitTypography.s3xl, weight: OBRitFontWeight.bold))
        .tracking(OBRitTypography.letterSpacing(for: OBRitTypography.s3xl))
    }
}

private struct ItemKindSelectionRow: View {
    let kind: ItemKind
    let selected: Bool
    let action: ItemRegistrationAction

    var body: some View {
        Button {
            action.onSelectKindCandidate(kind)
        } label: {
            HStack(spacing: OBRitSpacing.s4) {
                ItemImage(
                    imageURL: kind.imageURL,
                    size: ItemRegistrationLayout.itemThumbnailSize
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                    Text(kind.title)
                        .lineLimit(ItemRegistrationLayout.singleLineLimit)
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: OBRitFontWeight.bold,
                            color: OBRitColors.common00
                        )
                    Text(ItemRegistrationText.addedCountText(kind.addedCount))
                        .lineLimit(ItemRegistrationLayout.singleLineLimit)
                        .obritTextStyle(
                            OBRitTypography.small,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.common00.opacity(ItemRegistrationLayout.secondaryTextOpacity)
                        )
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                ItemKindSelectionRadioIndicator(selected: selected)
                    .frame(width: OBRitSpacing.s10, height: OBRitSpacing.s10)
            }
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.vertical, OBRitSpacing.s4)
            .background(OBRitColors.backgroundDefaultSecondary)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        }
        .buttonStyle(.plain)
    }
}

private struct ItemKindSelectionRadioIndicator: View {
    let selected: Bool

    var body: some View {
        ZStack {
            Circle()
                .stroke(ringColor, lineWidth: ItemRegistrationLayout.radioBorderWidth)
                .frame(width: ItemRegistrationLayout.radioOuterSize, height: ItemRegistrationLayout.radioOuterSize)
            if selected {
                Circle()
                    .fill(ringColor)
                    .frame(width: OBRitSpacing.s3, height: OBRitSpacing.s3)
            }
        }
        .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
        .accessibilityHidden(true)
    }

    private var ringColor: Color {
        selected ? OBRitColors.green300 : OBRitColors.gray400
    }
}

private struct ItemKindNoResultView: View {
    let action: ItemRegistrationAction

    var body: some View {
        VStack(spacing: OBRitSpacing.s5) {
            VStack(spacing: OBRitSpacing.s2) {
                Text("소모품 검색 결과가 없어요!")
                    .obritTextStyle(
                        OBRitTypography.s3xl,
                        weight: OBRitFontWeight.bold,
                        color: OBRitColors.common00
                    )
                Text("직접 소모품 종류를 등록할 수 있어요.")
                    .obritTextStyle(
                        OBRitTypography.base,
                        weight: OBRitFontWeight.medium,
                        color: OBRitColors.textDefaultSecondary
                    )
            }

            OBRitFilledTextButton(
                text: "직접 등록",
                size: .small,
                color: .white,
                action: action.onShowDirectKindRegistration
            )
        }
        .frame(maxWidth: .infinity)
        .frame(height: ItemRegistrationLayout.kindNoResultHeight)

        ItemSheetGradientButton(text: "소모품 종류 선택하기", enabled: false, action: {})
    }
}

private struct ItemSheetGradientButton: View {
    let text: String
    let enabled: Bool
    let action: () -> Void

    init(text: String, enabled: Bool = true, action: @escaping () -> Void) {
        self.text = text
        self.enabled = enabled
        self.action = action
    }

    var body: some View {
        VStack(spacing: ItemRegistrationLayout.zeroSpacing) {
            LinearGradient(
                colors: [
                    OBRitColors.backgroundDefaultDefault.opacity(ItemRegistrationLayout.transparentOpacity),
                    OBRitColors.backgroundDefaultDefault
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: OBRitSpacing.s5)

            OBRitFilledTextButton(
                text: text,
                color: .green,
                enabled: enabled,
                fillsWidth: true,
                action: action
            )
        }
        .background(OBRitColors.backgroundDefaultDefault)
        .disabled(!enabled)
    }
}
