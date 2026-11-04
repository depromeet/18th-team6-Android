import SwiftUI
import UIKit

struct RegistrationSearchSuggestions: View {
    let query: String
    let suggestions: [String]
    let onSelect: (String) -> Void

    var body: some View {
        VStack(spacing: 0) {
            ForEach(Array(suggestions.enumerated()), id: \.offset) { _, name in
                Button {
                    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                    onSelect(name)
                } label: {
                    highlightedName(name)
                        .obritTextStyle(OBRitTypography.base, color: OBRitColors.common00)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.horizontal, OBRitSpacing.s4)
                        .padding(.vertical, OBRitSpacing.s3)
                }
                .buttonStyle(.plain)
            }
        }
        .background(OBRitColors.gray850, in: RoundedRectangle(cornerRadius: OBRitRadius.small))
    }

    private func highlightedName(_ name: String) -> Text {
        let query = query.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !query.isEmpty else { return Text(name) }
        var text = Text("")
        var rest = name[...]
        while let range = rest.range(of: query, options: [.caseInsensitive, .diacriticInsensitive]) {
            text = text + Text(String(rest[..<range.lowerBound])) + Text(String(rest[range])).foregroundColor(OBRitColors.green300)
            rest = rest[range.upperBound...]
        }
        return text + Text(String(rest))
    }
}

struct RegistrationNoSearchResults: View {
    var error: String? = nil
    var enabled = true
    let onDirectRegister: () -> Void

    var body: some View {
        VStack(spacing: OBRitSpacing.s4) {
            Text("소모품 검색 결과가 없어요!")
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.common00)
            Text("하단 버튼을 통해 직접 소모품을 등록할 수 있어요.")
                .obritTextStyle(OBRitTypography.base, color: OBRitColors.textDefaultSecondary)
            if let error {
                Text(error).obritTextStyle(OBRitTypography.small, color: OBRitColors.red400)
            } else {
                Button("직접 등록하기 >", action: onDirectRegister)
                    .buttonStyle(.plain)
                    .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.semiBold, color: OBRitColors.green300)
                    .disabled(!enabled)
            }
        }
        .multilineTextAlignment(.center)
        .frame(maxWidth: .infinity)
        .padding(.vertical, OBRitSpacing.s8)
        .padding(.horizontal, OBRitSpacing.s3)
    }
}
