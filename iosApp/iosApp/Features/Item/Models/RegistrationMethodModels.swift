import SwiftUI

struct RegistrationMethodOption: Identifiable {
    enum Kind {
        case receipt
        case direct
    }

    let id: Kind
    let title: String
    let subtitle: String
    let symbolName: String
    let accentColor: Color
}

enum RegistrationMethodViewState {
    case success([RegistrationMethodOption])
}

enum RegistrationMethodDefaults {
    static let options: [RegistrationMethodOption] = [
        RegistrationMethodOption(
            id: .receipt,
            title: "영수증으로 등록하기",
            subtitle: "사진을 분석해 소모품 후보를 찾아요",
            symbolName: "doc.text.viewfinder",
            accentColor: OBRitColors.green300
        ),
        RegistrationMethodOption(
            id: .direct,
            title: "직접 등록하기",
            subtitle: "소모품 종류와 수량을 직접 입력해요",
            symbolName: "square.and.pencil",
            accentColor: OBRitColors.common00
        )
    ]
}
