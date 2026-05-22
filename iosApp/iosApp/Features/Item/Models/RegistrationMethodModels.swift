import SwiftUI

struct RegistrationMethodOption: Identifiable {
    enum Kind {
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

enum RegistrationMethodSampleData {
    static let options: [RegistrationMethodOption] = [
        RegistrationMethodOption(
            id: .direct,
            title: "직접 등록하기",
            subtitle: "소모품 종류와 수량을 직접 입력해요",
            symbolName: "square.and.pencil",
            accentColor: OBRitColors.common00
        )
    ]
}
