import SwiftUI

public struct OBRitHomeTopBar: View {
    @State private var logoTapCount = 0

    private let backgroundColor: Bool
    private let showNotificationButton: Bool
    private let onSearchClick: () -> Void
    private let onNotificationClick: () -> Void
    private let onProfileClick: () -> Void
    private let onLogoEasterEgg: () -> Void

    public init(
        backgroundColor: Bool = true,
        showNotificationButton: Bool = true,
        onSearchClick: @escaping () -> Void,
        onNotificationClick: @escaping () -> Void,
        onProfileClick: @escaping () -> Void,
        onLogoEasterEgg: @escaping () -> Void = {}
    ) {
        self.backgroundColor = backgroundColor
        self.showNotificationButton = showNotificationButton
        self.onSearchClick = onSearchClick
        self.onNotificationClick = onNotificationClick
        self.onProfileClick = onProfileClick
        self.onLogoEasterEgg = onLogoEasterEgg
    }

    public static func transparent(
        showNotificationButton: Bool = true,
        onSearchClick: @escaping () -> Void,
        onNotificationClick: @escaping () -> Void,
        onProfileClick: @escaping () -> Void,
        onLogoEasterEgg: @escaping () -> Void = {}
    ) -> OBRitHomeTopBar {
        OBRitHomeTopBar(
            backgroundColor: false,
            showNotificationButton: showNotificationButton,
            onSearchClick: onSearchClick,
            onNotificationClick: onNotificationClick,
            onProfileClick: onProfileClick,
            onLogoEasterEgg: onLogoEasterEgg
        )
    }

    public var body: some View {
        TopBarRoot(backgroundColor: backgroundColor) {
            HStack {
                Button(action: handleLogoClick) {
                    OBRitLogo()
                        .frame(width: 69, height: 24, alignment: .leading)
                        .frame(width: 92, height: OBRitSpacing.s10, alignment: .leading)
                        .contentShape(Rectangle())
                }
                .buttonStyle(.plain)

                Spacer()

                HStack(spacing: 0) {
                    TopBarIconButton(symbolName: "magnifyingglass", accessibilityLabel: "검색", action: onSearchClick)
                    if showNotificationButton {
                        TopBarIconButton(symbolName: "bell", accessibilityLabel: "알림", action: onNotificationClick)
                    }
                }
            }
            .padding(.leading, OBRitSpacing.s5)
            .padding(.trailing, OBRitSpacing.s3)
        }
    }

    private func handleLogoClick() {
        logoTapCount += 1
        guard logoTapCount >= 3 else { return }
        logoTapCount = 0
        onLogoEasterEgg()
    }
}

public struct OBRitCloseTopBar: View {
    private let title: String
    private let backgroundColor: Bool
    private let showPageTitle: Bool
    private let showRightButton: Bool
    private let onCloseClick: () -> Void
    private let onMoreClick: (() -> Void)?

    public init(
        title: String,
        backgroundColor: Bool = true,
        showPageTitle: Bool = true,
        showRightButton: Bool = true,
        onCloseClick: @escaping () -> Void,
        onMoreClick: (() -> Void)? = nil
    ) {
        self.title = title
        self.backgroundColor = backgroundColor
        self.showPageTitle = showPageTitle
        self.showRightButton = showRightButton
        self.onCloseClick = onCloseClick
        self.onMoreClick = onMoreClick
    }

    public var body: some View {
        TopBarWithTitle(
            title: title,
            backgroundColor: backgroundColor,
            showPageTitle: showPageTitle,
            showRightButton: showRightButton,
            leadingSymbolName: "xmark",
            leadingAccessibilityLabel: "닫기",
            onLeadingClick: onCloseClick,
            onMoreClick: onMoreClick
        )
    }
}

public struct OBRitDepthTopBar: View {
    private let title: String
    private let backgroundColor: Bool
    private let showPageTitle: Bool
    private let showRightButton: Bool
    private let onBackClick: () -> Void
    private let onMoreClick: (() -> Void)?

    public init(
        title: String,
        backgroundColor: Bool = true,
        showPageTitle: Bool = true,
        showRightButton: Bool = true,
        onBackClick: @escaping () -> Void,
        onMoreClick: (() -> Void)? = nil
    ) {
        self.title = title
        self.backgroundColor = backgroundColor
        self.showPageTitle = showPageTitle
        self.showRightButton = showRightButton
        self.onBackClick = onBackClick
        self.onMoreClick = onMoreClick
    }

    public var body: some View {
        TopBarWithTitle(
            title: title,
            backgroundColor: backgroundColor,
            showPageTitle: showPageTitle,
            showRightButton: showRightButton,
            leadingSymbolName: "chevron.left",
            leadingAccessibilityLabel: "뒤로",
            onLeadingClick: onBackClick,
            onMoreClick: onMoreClick
        )
    }
}

public struct OBRitSearchTopBar: View {
    @Binding private var query: String
    private let searchFocus: FocusState<Bool>.Binding
    private let placeholder: String
    private let backgroundColor: Bool
    private let onBackClick: () -> Void
    private let onSubmit: () -> Void

    public init(
        query: Binding<String>,
        searchFocus: FocusState<Bool>.Binding,
        placeholder: String = "원하시는 소모품을 검색해보세요",
        backgroundColor: Bool = true,
        onBackClick: @escaping () -> Void,
        onSubmit: @escaping () -> Void = {}
    ) {
        self._query = query
        self.searchFocus = searchFocus
        self.placeholder = placeholder
        self.backgroundColor = backgroundColor
        self.onBackClick = onBackClick
        self.onSubmit = onSubmit
    }

    public var body: some View {
        TopBarRoot(backgroundColor: backgroundColor) {
            HStack(spacing: OBRitSpacing.s3) {
                TopBarIconButton(symbolName: "chevron.left", accessibilityLabel: "뒤로", action: onBackClick)

                HStack(spacing: OBRitSpacing.s2) {
                    TextField("", text: $query, prompt: Text(placeholder).foregroundStyle(OBRitColors.gray700))
                        .lineLimit(1)
                        .tint(OBRitColors.common00)
                        .submitLabel(.search)
                        .focused(searchFocus)
                        .onSubmit(onSubmit)
                        .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.medium, color: OBRitColors.common00)
                    Image(systemName: "magnifyingglass")
                        .font(.system(size: OBRitSpacing.s5, weight: .regular))
                        .foregroundStyle(OBRitColors.common00)
                }
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.vertical, OBRitSpacing.s4)
                .overlay(
                    RoundedRectangle(cornerRadius: OBRitRadius.middle)
                        .stroke(OBRitColors.gray300, lineWidth: 1.4)
                )
            }
            .padding(.leading, OBRitSpacing.s3)
            .padding(.trailing, OBRitSpacing.s3)
        }
    }
}

private struct TopBarWithTitle: View {
    let title: String
    let backgroundColor: Bool
    let showPageTitle: Bool
    let showRightButton: Bool
    let leadingSymbolName: String
    let leadingAccessibilityLabel: String
    let onLeadingClick: () -> Void
    let onMoreClick: (() -> Void)?

    var body: some View {
        TopBarRoot(backgroundColor: backgroundColor) {
            ZStack {
                HStack {
                    TopBarIconButton(
                        symbolName: leadingSymbolName,
                        accessibilityLabel: leadingAccessibilityLabel,
                        action: onLeadingClick
                    )
                    Spacer()
                    if showRightButton, let onMoreClick {
                        TopBarIconButton(symbolName: "ellipsis", accessibilityLabel: "더보기", action: onMoreClick)
                            .rotationEffect(.degrees(90))
                    }
                }
                .padding(.horizontal, OBRitSpacing.s3)

                if showPageTitle {
                    Text(title)
                        .lineLimit(1)
                        .truncationMode(.tail)
                        .frame(width: 277)
                        .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                }
            }
        }
    }
}

private struct TopBarRoot<Content: View>: View {
    let backgroundColor: Bool
    let content: () -> Content

    init(
        backgroundColor: Bool = true,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.backgroundColor = backgroundColor
        self.content = content
    }

    var body: some View {
        content()
            .frame(maxWidth: .infinity)
            .frame(height: OBRitSpacing.s14)
            .background(backgroundColor ? OBRitColors.gray900 : Color.clear)
    }
}

private struct TopBarIconButton: View {
    let symbolName: String
    let accessibilityLabel: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: symbolName)
                .font(.system(size: OBRitSpacing.s5, weight: .regular))
                .foregroundStyle(OBRitColors.common00)
                .frame(width: OBRitSpacing.s10, height: OBRitSpacing.s10)
                .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        }
        .buttonStyle(.plain)
        .accessibilityLabel(accessibilityLabel)
    }
}
