import SwiftUI

/// Reuses the manual registration catalog and direct-registration pages in receipt results.
struct ItemKindPickerOverlay: View {
    @ObservedObject var viewModel: ItemRegistrationViewModel
    let onDismiss: () -> Void
    let onSelect: (ItemKind) -> Void
    @State private var hasOpened = false
    @State private var errorMessage: String?

    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .bottom) {
                Color.black.opacity(ItemRegistrationLayout.bottomSheetDimOpacity)
                    .ignoresSafeArea()
                    .onTapGesture { if !isProcessing { onDismiss() } }
                let padding = max(OBRitSpacing.s5, geometry.safeAreaInsets.bottom)
                let height = max(0, geometry.size.height - geometry.safeAreaInsets.top - ItemRegistrationLayout.bottomSheetTopPadding - ItemRegistrationLayout.bottomSheetHeaderHeight - padding)
                switch viewModel.state {
                case .loading:
                    OBRitBottomSheet(contentHeight: height, bottomPadding: padding, onDismiss: onDismiss) { ProgressView().tint(OBRitColors.green300) }
                case let .loadFailed(message):
                    OBRitBottomSheet(contentHeight: height, bottomPadding: padding, onDismiss: onDismiss) {
                        VStack(spacing: OBRitSpacing.s4) {
                            Text(message).foregroundStyle(OBRitColors.common00)
                            OBRitFilledTextButton(text: "다시 시도", action: viewModel.retry)
                        }
                    }
                case let .success(data):
                    let action = ItemRegistrationAction.kindPicker(using: viewModel, onDismiss: { if !data.isProcessing { onDismiss() } })
                    if data.bottomSheet == .directKind {
                        ItemDirectKindRegistrationView(data: data, action: action, contentHeight: height, bottomPadding: padding)
                    } else {
                        ManualKindSelectionBottomSheet(data: data, action: action, contentHeight: height, bottomPadding: padding)
                    }
                }
                if let errorMessage {
                    OBRitSnackbar(message: errorMessage, icon: .error)
                        .padding(OBRitSpacing.s5)
                        .allowsHitTesting(false)
                        .task(id: errorMessage) {
                            try? await Task.sleep(for: .seconds(2.5))
                            self.errorMessage = nil
                        }
                }
            }
            .ignoresSafeArea(.container, edges: .bottom)
        }
        .onChange(of: viewModel.state, initial: true) { _, state in
            guard case let .success(data) = state else { return }
            if !hasOpened {
                hasOpened = true
                viewModel.resetToForm()
                viewModel.openKindSheet()
            } else if let kind = data.draft.selectedKind, data.bottomSheet == nil {
                onSelect(kind)
            }
        }
        .onChange(of: viewModel.effect) { _, effect in
            if case let .showMessage(message) = effect { errorMessage = message }
            viewModel.clearEffect()
        }
    }

    private var isProcessing: Bool {
        if case let .success(data) = viewModel.state { return data.isProcessing }
        return false
    }
}
