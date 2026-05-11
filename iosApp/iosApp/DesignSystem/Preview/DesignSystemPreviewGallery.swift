import SwiftUI

struct DesignSystemPreviewGallery: View {
    @State private var defaultText = ""
    @State private var filledText = "TEXT"
    @State private var disabledText = ""
    @State private var errorText = "TEXT"
    @State private var successText = "TEXT"
    @State private var trailingText = "TEXT"
    @State private var searchQuery = ""
    @State private var sliderValue = 0.5
    @State private var selectedChipIndex = 0
    @State private var selectedTabIndex = 0

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: OBRitSpacing.s6) {
                previewSection("TopBar") {
                    VStack(spacing: OBRitSpacing.s3) {
                        OBRitHomeTopBar(onSearchClick: {}, onNotificationClick: {}, onProfileClick: {})
                        OBRitHomeTopBar(backgroundColor: false, onSearchClick: {}, onNotificationClick: {}, onProfileClick: {})
                        OBRitDepthTopBar(title: "PageTitle", onBackClick: {}, onMoreClick: {})
                        OBRitDepthTopBar(title: "PageTitle", backgroundColor: false, onBackClick: {}, onMoreClick: {})
                        OBRitCloseTopBar(title: "PageTitle", onCloseClick: {}, onMoreClick: {})
                        OBRitCloseTopBar(title: "PageTitle", showPageTitle: false, showRightButton: false, onCloseClick: {}, onMoreClick: {})
                        OBRitSearchTopBar(query: $searchQuery, onBackClick: {})
                        OBRitSearchTopBar(query: $searchQuery, backgroundColor: false, onBackClick: {})
                    }
                }

                previewSection("Buttons") {
                    VStack(spacing: OBRitSpacing.s3) {
                        buttonRow(size: .large)
                        buttonRow(size: .middle)
                        buttonRow(size: .small)
                        buttonRow(size: .large, enabled: false)
                        OBRitFilledTextButton(
                            text: "Button",
                            size: .middle,
                            color: .gray,
                            enabled: false,
                            action: {}
                        ) { contentColor in
                            Image(systemName: "info.circle")
                                .foregroundStyle(contentColor)
                        } trailingIcon: { contentColor in
                            Image(systemName: "info.circle")
                                .foregroundStyle(contentColor)
                        }
                    }
                }

                previewSection("Selection") {
                    HStack(spacing: OBRitSpacing.s5) {
                        OBRitCheckBox(checked: true)
                        OBRitCheckBox(checked: false)
                        OBRitCheckBox(checked: true, enabled: false)
                        OBRitCheckBox(checked: false, enabled: false)
                        OBRitRadioButton(selected: true)
                        OBRitRadioButton(selected: false)
                        OBRitRadioButton(selected: true, enabled: false)
                        OBRitRadioButton(selected: false, enabled: false)
                    }
                }

                previewSection("Indicators") {
                    VStack(alignment: .leading, spacing: OBRitSpacing.s4) {
                        HStack(spacing: OBRitSpacing.s4) {
                            OBRitNumber(text: "N")
                            OBRitNumber(text: "N", selected: true)
                            OBRitIndicatorDot(active: true)
                            OBRitIndicatorDot()
                        }
                        OBRitPageIndicator(count: 18, selectedIndex: 0)
                            .frame(width: 412)
                    }
                }

                previewSection("Dropdown") {
                    VStack(spacing: OBRitSpacing.s3) {
                        OBRitDropdown(value: "", placeholder: "TEXT", onClick: {})
                        OBRitDropdown(value: "TEXT", placeholder: "TEXT", onClick: {})
                        OBRitDropdown(value: "", placeholder: "TEXT", expanded: true, onClick: {})
                        OBRitDropdown(value: "TEXT", placeholder: "TEXT", inputState: .error, supportingText: "에러 텍스트를 입력해주세요", onClick: {})
                        OBRitDropdown(value: "", placeholder: "TEXT", enabled: false, onClick: {})
                        HStack(spacing: OBRitSpacing.s6) {
                            VStack(spacing: OBRitSpacing.s3) {
                                OBRitDropdownMenuItem(text: "TEXT", size: .small)
                                OBRitDropdownMenuItem(text: "TEXT", size: .small, selected: true)
                            }
                            VStack(spacing: OBRitSpacing.s3) {
                                OBRitDropdownMenuItem(text: "TEXT", size: .large)
                                OBRitDropdownMenuItem(text: "TEXT", size: .large, selected: true)
                            }
                        }
                        OBRitDropdownMenu(items: Array(repeating: "TEXT", count: 6), selectedIndex: 1, onItemClick: { _ in })
                    }
                }

                previewSection("Badges") {
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: OBRitSpacing.s2) {
                            OBRitBadge(text: "Text", type: .warningFilled)
                            OBRitBadge(text: "Text", type: .gray750Filled)
                            OBRitBadge(text: "Text", type: .warningWhiteBackgroundFilled)
                            OBRitBadge(text: "Text", type: .red250Filled)
                            OBRitBadge(text: "Text", type: .red800Filled)
                        }
                    }
                }

                previewSection("Chips") {
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: OBRitSpacing.s2) {
                            OBRitChip(text: "Text", selected: selectedChipIndex == 0, number: 12) { selectedChipIndex = 0 }
                            OBRitChip(text: "Text", selected: selectedChipIndex == 1, number: 12) { selectedChipIndex = 1 }
                            OBRitChip(text: "Text", selected: selectedChipIndex == 2) { selectedChipIndex = 2 }
                            OBRitChip(text: "Text", selected: selectedChipIndex == 3) { selectedChipIndex = 3 }
                        }
                    }
                }

                previewSection("Tabs") {
                    OBRitTabs(
                        items: [
                            OBRitTabItem(text: "{Text}"),
                            OBRitTabItem(text: "{Text}"),
                            OBRitTabItem(text: "{Text}"),
                            OBRitTabItem(text: "{Text}"),
                            OBRitTabItem(text: "{Text}"),
                            OBRitTabItem(text: "{Text}"),
                            OBRitTabItem(text: "{Text}"),
                            OBRitTabItem(text: "{Text}")
                        ],
                        selectedIndex: selectedTabIndex,
                        onSelect: { selectedTabIndex = $0 }
                    )
                }

                previewSection("Slider") {
                    OBRitSlider(value: sliderValue) { sliderValue = $0 }
                        .frame(maxWidth: .infinity)
                }

                previewSection("Stepper") {
                    VStack(alignment: .leading, spacing: OBRitSpacing.s8) {
                        HStack(spacing: OBRitSpacing.s10) {
                            OBRitStepper(valueText: "N")
                            OBRitStepper(value: 0)
                        }
                        HStack(spacing: OBRitSpacing.s10) {
                            OBRitStepper(valueText: "N", size: .large)
                            OBRitStepper(valueText: "N", size: .large, isMinimum: true)
                        }
                    }
                }

                previewSection("Cards") {
                    VStack(spacing: OBRitSpacing.s3) {
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: OBRitSpacing.s3) {
                                OBRitCardGrid(level: .l1, title: "필터", stockCount: 0, daysLabel: "D-day")
                                OBRitCardGrid(level: .l3, title: "필터", stockCount: 5, daysLabel: "D-day")
                                OBRitCardGrid(level: .l5, title: "필터", stockCount: 0, daysLabel: "D-7")
                            }
                        }
                        OBRitCardList(level: .l1, title: "필터", daysInUseLabel: "30일", replaceLabel: "교체 D+2", sparesLabel: "여분 5개")
                        OBRitCardList(level: .l2, title: "필터", daysInUseLabel: "27일", replaceLabel: "교체 D-3", sparesLabel: "여분 0개")
                        OBRitCardList(level: .l6, title: "필터", daysInUseLabel: "7일", replaceLabel: "교체 D-7", sparesLabel: "여분 5개")
                    }
                }

                previewSection("Modal") {
                    VStack(spacing: OBRitSpacing.s5) {
                        HStack(spacing: OBRitSpacing.s5) {
                            OBRitModal(
                                title: "Title Text\n최대 두 줄까지 작성 가능합니다.",
                                description: "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
                                mode: .light,
                                buttonCount: .two,
                                imageSize: .small,
                                onPrimaryClick: {}
                            )
                            OBRitModal(
                                title: "Title Text\n최대 두 줄까지 작성 가능합니다.",
                                description: "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
                                mode: .dark,
                                buttonCount: .two,
                                imageSize: .small,
                                onPrimaryClick: {}
                            )
                        }
                        HStack(spacing: OBRitSpacing.s5) {
                            OBRitModal(
                                title: "Title Text\n최대 두 줄까지 작성 가능합니다.",
                                description: "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
                                mode: .light,
                                buttonCount: .one,
                                imageSize: .large,
                                onPrimaryClick: {}
                            )
                            OBRitModal(
                                title: "Title Text\n최대 두 줄까지 작성 가능합니다.",
                                description: "모달의 상세 내용을 작성해주세요.\n최대 두 줄까지 작성 가능합니다.",
                                mode: .dark,
                                buttonCount: .one,
                                imageSize: .large,
                                onPrimaryClick: {}
                            )
                        }
                    }
                }

                previewSection("Snackbar") {
                    VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                        OBRitSnackbar(message: "토스트 팝업 내용을 입력하세요.")
                        OBRitSnackbar(message: "토스트 팝업 내용을 입력하세요.\n토스트 팝업 내용을 입력하세요.", icon: .default)
                        OBRitSnackbar(message: "토스트 팝업 내용을 입력하세요.", icon: .error)
                        OBRitSnackbar(message: "토스트 팝업 내용을 입력하세요.", icon: .success)
                    }
                }

                previewSection("Tooltip") {
                    VStack(spacing: OBRitSpacing.s6) {
                        HStack(spacing: OBRitSpacing.s8) {
                            OBRitTooltip(text: "Place your text here", direction: .top, alignment: .start)
                            OBRitTooltip(text: "Place your text here", direction: .top)
                            OBRitTooltip(text: "Place your text here", direction: .top, alignment: .end)
                        }
                        HStack(spacing: OBRitSpacing.s8) {
                            OBRitTooltip(text: "Place your text here", direction: .bottom, alignment: .start)
                            OBRitTooltip(text: "Place your text here", direction: .bottom)
                            OBRitTooltip(text: "Place your text here", direction: .bottom, alignment: .end)
                        }
                        HStack(spacing: OBRitSpacing.s8) {
                            OBRitTooltip(text: "Place your text here", direction: .right, alignment: .start)
                            OBRitTooltip(text: "Place your text here", direction: .right)
                            OBRitTooltip(text: "Place your text here", direction: .right, alignment: .end)
                        }
                        HStack(spacing: OBRitSpacing.s8) {
                            OBRitTooltip(text: "Place your text here", direction: .left, alignment: .start)
                            OBRitTooltip(text: "Place your text here", direction: .left)
                            OBRitTooltip(text: "Place your text here", direction: .left, alignment: .end)
                        }
                    }
                }

                previewSection("BottomSheet") {
                    OBRitBottomSheet {
                        Color.clear
                    }
                }

                previewSection("TextField") {
                    VStack(spacing: OBRitSpacing.s3) {
                        OBRitOutlinedTextField(text: $defaultText, placeholder: "TEXT", maxLength: 30, singleLine: true)
                        OBRitOutlinedTextField(text: $filledText, placeholder: "TEXT", maxLength: 30, singleLine: true)
                        OBRitOutlinedTextField(text: $filledText, placeholder: "TEXT", maxLength: 30, singleLine: true, forceFocused: true)
                        OBRitOutlinedTextField(text: $defaultText, placeholder: "TEXT", style: .lined, maxLength: 30, singleLine: true)
                        OBRitOutlinedTextField(text: $filledText, placeholder: "TEXT", style: .lined, maxLength: 30, singleLine: true)
                        OBRitOutlinedTextField(text: $disabledText, placeholder: "TEXT", maxLength: 30, enabled: false, singleLine: true)
                        OBRitOutlinedTextField(text: $errorText, placeholder: "TEXT", inputResultState: .error, maxLength: 30, supportingText: "에러 텍스트를 입력해주세요", singleLine: true)
                        OBRitOutlinedTextField(text: $successText, placeholder: "TEXT", inputResultState: .success, maxLength: 30, supportingText: "완료 텍스트를 입력해주세요", singleLine: true)
                        OBRitOutlinedTextField(text: $trailingText, placeholder: "TEXT", maxLength: 30, singleLine: true) {
                            OBRitIcon(kind: .question, color: OBRitColors.common00)
                        } trailingIcon: {
                            OBRitIcon(kind: .success, color: OBRitColors.green300)
                        }
                    }
                }
            }
            .padding(OBRitSpacing.s5)
        }
        .background(OBRitColors.gray900)
    }

    private func buttonRow(size: OBRitFilledButtonSize, enabled: Bool = true) -> some View {
        HStack(spacing: OBRitSpacing.s3) {
            OBRitFilledTextButton(text: "Button", size: size, color: .green, enabled: enabled, action: {})
            OBRitFilledTextButton(text: "Button", size: size, color: .gray, enabled: enabled, action: {})
            OBRitFilledTextButton(text: "Button", size: size, color: .white, enabled: enabled, action: {})
        }
    }

    private func previewSection<Content: View>(
        _ title: String,
        @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            Text(title)
                .obritTextStyle(OBRitTypography.base, color: OBRitColors.gray300)
            content()
        }
    }
}

struct DesignSystemPreviewGallery_Previews: PreviewProvider {
    static var previews: some View {
        DesignSystemPreviewGallery()
            .previewLayout(.sizeThatFits)
    }
}
