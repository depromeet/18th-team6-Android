import SwiftUI

struct RoutePlaceholderView<Actions: View>: View {
    let title: String
    let subtitle: String
    let actions: Actions

    init(
        title: String,
        subtitle: String,
        @ViewBuilder actions: () -> Actions
    ) {
        self.title = title
        self.subtitle = subtitle
        self.actions = actions()
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text(title)
                    .font(.title2.weight(.semibold))
                Text(subtitle)
                    .font(.body)
                    .foregroundStyle(.secondary)
                VStack(spacing: 10) {
                    actions
                }
                .padding(.top, 12)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(20)
        }
        .navigationTitle(title)
        .navigationBarTitleDisplayMode(.inline)
    }
}

extension RoutePlaceholderView where Actions == EmptyView {
    init(title: String, subtitle: String) {
        self.title = title
        self.subtitle = subtitle
        self.actions = EmptyView()
    }
}

struct NavigationActionButton: View {
    let title: String
    let action: () -> Void

    init(_ title: String, action: @escaping () -> Void) {
        self.title = title
        self.action = action
    }

    var body: some View {
        Button(action: action) {
            HStack {
                Text(title)
                Spacer()
                Image(systemName: "chevron.right")
                    .font(.footnote.weight(.semibold))
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 12)
            .frame(maxWidth: .infinity)
            .background(Color(.secondarySystemBackground))
            .clipShape(RoundedRectangle(cornerRadius: 8))
        }
        .buttonStyle(.plain)
    }
}
