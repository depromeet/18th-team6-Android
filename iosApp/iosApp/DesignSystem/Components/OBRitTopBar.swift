import SwiftUI
import Shared

public struct OBRitHomeTopBar: View {
    private let backgroundColor: Bool
    private let onSearchClick: () -> Void
    private let onNotificationClick: () -> Void
    private let onProfileClick: () -> Void

    public init(
        backgroundColor: Bool = true,
        onSearchClick: @escaping () -> Void,
        onNotificationClick: @escaping () -> Void,
        onProfileClick: @escaping () -> Void
    ) {
        self.backgroundColor = backgroundColor
        self.onSearchClick = onSearchClick
        self.onNotificationClick = onNotificationClick
        self.onProfileClick = onProfileClick
    }

    public var body: some View {
        TopBarRoot(backgroundColor: backgroundColor) {
            HStack {
                OBRitLogo()
                    .frame(width: 80, height: 25, alignment: .leading)

                Spacer()

                HStack(spacing: 0) {
                    TopBarIconButton(symbolName: "magnifyingglass", accessibilityLabel: "검색", action: onSearchClick)
                    TopBarIconButton(symbolName: "bell", accessibilityLabel: "알림", action: onNotificationClick)
                    TopBarIconButton(symbolName: "person", accessibilityLabel: "프로필", action: onProfileClick)
                }
            }
            .padding(.leading, OBRitSpacing.s5)
            .padding(.trailing, OBRitSpacing.s3)
        }
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
    private let placeholder: String
    private let backgroundColor: Bool
    private let onBackClick: () -> Void

    public init(
        query: Binding<String>,
        placeholder: String = "원하시는 소모품을 검색해보세요",
        backgroundColor: Bool = true,
        onBackClick: @escaping () -> Void
    ) {
        self._query = query
        self.placeholder = placeholder
        self.backgroundColor = backgroundColor
        self.onBackClick = onBackClick
    }

    public var body: some View {
        TopBarRoot(backgroundColor: backgroundColor) {
            HStack(spacing: OBRitSpacing.s3) {
                TopBarIconButton(symbolName: "chevron.left", accessibilityLabel: "뒤로", action: onBackClick)

                HStack(spacing: OBRitSpacing.s2) {
                    TextField("", text: $query, prompt: Text(placeholder).foregroundStyle(OBRitColors.gray700))
                        .lineLimit(1)
                        .tint(OBRitColors.common00)
                        .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.Medium, color: OBRitColors.common00)
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
                        .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.Bold, color: OBRitColors.common00)
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
