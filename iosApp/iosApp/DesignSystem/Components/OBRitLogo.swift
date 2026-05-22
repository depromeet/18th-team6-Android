import SwiftUI

public struct OBRitLogo: View {
    public init() {}

    public var body: some View {
        Image("obrit_logo")
            .resizable()
            .renderingMode(.original)
            .aspectRatio(contentMode: .fit)
            .accessibilityLabel("OBRit")
    }
}
