import SwiftUI

struct OBRitRemoteImage<Placeholder: View>: View {
    let urlString: String
    let contentMode: ContentMode
    let placeholder: () -> Placeholder

    init(
        urlString: String,
        contentMode: ContentMode = .fit,
        @ViewBuilder placeholder: @escaping () -> Placeholder = { Color.clear }
    ) {
        self.urlString = urlString
        self.contentMode = contentMode
        self.placeholder = placeholder
    }

    var body: some View {
        if let url = URL(string: urlString.trimmingCharacters(in: .whitespacesAndNewlines)) {
            AsyncImage(url: url) { phase in
                switch phase {
                case let .success(image):
                    image
                        .resizable()
                        .aspectRatio(contentMode: contentMode)
                case .empty, .failure:
                    placeholder()
                @unknown default:
                    placeholder()
                }
            }
        } else {
            placeholder()
        }
    }
}
