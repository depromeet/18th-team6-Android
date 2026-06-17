import SwiftUI

struct ItemDetailEditRouteView: View {
    @StateObject private var viewModel: ItemDetailEditViewModel
    @State private var draft: ItemDetailEditDraft?
    @State private var isExitConfirmationPresented = false
    @State private var isKindSheetPresented = false
    @State private var kindSearchQuery = ""
    @State private var selectedKindCandidate: ItemKind?
    @State private var snackbar: ItemDetailEditSnackbarPresentation?
    @State private var snackbarDismissTask: Task<Void, Never>?

    private let onBack: () -> Void
    private let onMutationCompleted: () -> Void

    @MainActor
    init(
        itemId: Int,
        viewModelFactory: @MainActor @escaping (Int) -> ItemDetailEditViewModel,
        onBack: @escaping () -> Void,
        onMutationCompleted: @escaping () -> Void = {}
    ) {
        self.onBack = onBack
        self.onMutationCompleted = onMutationCompleted
        _viewModel = StateObject(wrappedValue: viewModelFactory(itemId))
    }

    @MainActor
    init(
        itemId: Int,
        onBack: @escaping () -> Void
    ) {
        self.init(
            itemId: itemId,
            viewModelFactory: AppDependencies.preview.makeItemDetailEditViewModel,
            onBack: onBack
        )
    }

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                content
                    .frame(width: geometry.size.width, height: geometry.size.height)

                if isExitConfirmationPresented {
                    OBRitColors.backgroundDefaultDimDefault
                        .ignoresSafeArea()
                        .onTapGesture {
                            isExitConfirmationPresented = false
                        }

                    ItemDetailConfirmationModal(
                        kind: .editExit,
                        onCancel: {
                            isExitConfirmationPresented = false
                        },
                        onConfirm: onBack
                    )
                    .transition(.scale.combined(with: .opacity))
                }

                if case let .success(data) = viewModel.state, isKindSheetPresented {
                    kindSelectionSheet(data: data, geometry: geometry)
                }

                if let snackbar {
                    VStack {
                        Spacer(minLength: 0)

                        OBRitSnackbar(message: snackbar.message, icon: .error)
                            .padding(.horizontal, OBRitSpacing.s5)
                            .padding(.bottom, OBRitSpacing.s6)
                            .transition(.move(edge: .bottom).combined(with: .opacity))
                    }
                    .allowsHitTesting(false)
                    .animation(.easeOut(duration: 0.18), value: snackbar.id)
                }
            }
        }
        .onChange(of: viewModel.effect) { _, effect in
            handleEffect(effect)
        }
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
        .onDisappear {
            snackbarDismissTask?.cancel()
        }
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .loading:
            ItemDetailEditMessageView(title: "소모품 정보를 불러오는 중이에요")
        case let .loadFailed(message):
            ItemDetailEditMessageView(title: message, buttonTitle: "다시 시도", action: viewModel.retry)
        case let .success(data):
            ItemDetailEditScaffoldView(
                draft: draftBinding(for: data.item),
                recommendedCycleDays: data.item.replacementCycle.intervalDays,
                averageCycleDays: data.averageCycleDays,
                existingItemNames: data.existingItemNames,
                isProcessing: data.isProcessing,
                onClose: {
                    isExitConfirmationPresented = true
                },
                onOpenKindSheet: {
                    openKindSheet(data: data)
                },
                onSubmit: {
                    viewModel.submit(draft: draftBinding(for: data.item).wrappedValue)
                }
            )
            .onAppear {
                if draft == nil {
                    draft = ItemDetailEditDraft(item: data.item)
                }
            }
        }
    }

    private func kindSelectionSheet(
        data: ItemDetailEditViewData,
        geometry: GeometryProxy
    ) -> some View {
        let bottomPadding = bottomSheetBottomPadding(in: geometry)

        return ZStack(alignment: .bottom) {
            Color.black.opacity(ItemRegistrationLayout.bottomSheetDimOpacity)
                .ignoresSafeArea()
                .onTapGesture(perform: dismissKindSheet)

            VStack {
                Spacer(minLength: ItemRegistrationLayout.spacerMinimumLength)
                ItemKindSelectionBottomSheet(
                    kinds: data.itemKinds,
                    selectedKind: draftBinding(for: data.item).wrappedValue.selectedKind,
                    selectedKindCandidate: selectedKindCandidate,
                    query: kindSearchQuery,
                    contentHeight: bottomSheetContentHeight(in: geometry, bottomPadding: bottomPadding),
                    bottomPadding: bottomPadding,
                    onDismiss: dismissKindSheet,
                    onUpdateQuery: updateKindSearchQuery,
                    onSelectCandidate: { kind in
                        selectedKindCandidate = kind
                    },
                    onConfirmSelection: {
                        confirmKindSelection(data: data)
                    }
                )
                .transition(.move(edge: .bottom).combined(with: .opacity))
            }
            .ignoresSafeArea(.container, edges: .bottom)
        }
        .animation(.easeOut(duration: ItemRegistrationLayout.bottomSheetAnimationDuration), value: isKindSheetPresented)
    }

    private func openKindSheet(data: ItemDetailEditViewData) {
        let currentDraft = draftBinding(for: data.item).wrappedValue
        kindSearchQuery = ""
        selectedKindCandidate = currentDraft.selectedKind ?? data.itemKinds.first
        isKindSheetPresented = true
    }

    private func dismissKindSheet() {
        isKindSheetPresented = false
        kindSearchQuery = ""
        selectedKindCandidate = nil
    }

    private func updateKindSearchQuery(_ query: String) {
        kindSearchQuery = query
        let normalizedQuery = query.trimmingCharacters(in: .whitespacesAndNewlines)

        guard case let .success(data) = viewModel.state else {
            selectedKindCandidate = nil
            return
        }

        selectedKindCandidate = data.itemKinds.first {
            normalizedQuery.isEmpty || $0.title.localizedCaseInsensitiveContains(normalizedQuery)
        }
    }

    private func confirmKindSelection(data: ItemDetailEditViewData) {
        guard let selectedKind = selectedKindCandidate else { return }

        var updatedDraft = draftBinding(for: data.item).wrappedValue
        updatedDraft.selectedKind = selectedKind
        updatedDraft.imageURL = selectedKind.imageURL
        draft = updatedDraft
        dismissKindSheet()
    }

    private func bottomSheetContentHeight(
        in geometry: GeometryProxy,
        bottomPadding: CGFloat
    ) -> CGFloat {
        let availableHeight = geometry.size.height
            - geometry.safeAreaInsets.top
            - ItemRegistrationLayout.bottomSheetTopPadding
            - ItemRegistrationLayout.bottomSheetHeaderHeight
            - bottomPadding
        return min(ItemRegistrationLayout.kindSheetMaxContentHeight, max(0, availableHeight))
    }

    private func bottomSheetBottomPadding(in geometry: GeometryProxy) -> CGFloat {
        max(
            OBRitSpacing.s5,
            min(geometry.safeAreaInsets.bottom, ItemRegistrationLayout.bottomSheetMaximumSafeAreaPadding)
        )
    }

    private func draftBinding(for item: ItemDetailItem) -> Binding<ItemDetailEditDraft> {
        Binding {
            draft ?? ItemDetailEditDraft(item: item)
        } set: { newValue in
            draft = newValue
        }
    }

    private func handleEffect(_ effect: ItemDetailEditViewEffect?) {
        guard let effect else { return }
        defer { viewModel.clearEffect() }

        switch effect {
        case .completed:
            onMutationCompleted()
            onBack()
        case let .showMessage(message):
            showSnackbar(message)
        }
    }

    private func showSnackbar(_ message: String) {
        snackbarDismissTask?.cancel()
        snackbar = ItemDetailEditSnackbarPresentation(message: message)
        snackbarDismissTask = Task { @MainActor in
            try? await Task.sleep(nanoseconds: ItemDetailEditSnackbarMetrics.displayDurationNanoseconds)
            guard !Task.isCancelled else { return }
            snackbar = nil
        }
    }
}

private struct ItemDetailEditSnackbarPresentation: Identifiable {
    let id = UUID()
    let message: String
}

private enum ItemDetailEditSnackbarMetrics {
    static let displayDurationNanoseconds: UInt64 = 2_400_000_000
}

private struct ItemDetailEditMessageView: View {
    let title: String
    var buttonTitle: String?
    var action: (() -> Void)?

    var body: some View {
        VStack(spacing: OBRitSpacing.s5) {
            Text(title)
                .multilineTextAlignment(.center)
                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.semiBold, color: OBRitColors.textDefaultDefault)

            if let buttonTitle, let action {
                OBRitFilledTextButton(text: buttonTitle, size: .middle, action: action)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(OBRitSpacing.s5)
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
    }
}

struct ItemDetailSpareRouteView: View {
    @StateObject private var viewModel: ItemDetailViewModel
    @State private var quantity = 0
    @State private var hasSyncedInitialQuantity = false
    @State private var snackbar: ItemDetailRouteSnackbarPresentation?
    @State private var snackbarDismissTask: Task<Void, Never>?

    private let onBack: () -> Void
    private let onMutationCompleted: () -> Void

    @MainActor
    init(
        itemId: Int,
        viewModelFactory: @MainActor @escaping (Int) -> ItemDetailViewModel,
        onBack: @escaping () -> Void,
        onMutationCompleted: @escaping () -> Void = {}
    ) {
        self.onBack = onBack
        self.onMutationCompleted = onMutationCompleted
        _viewModel = StateObject(wrappedValue: viewModelFactory(itemId))
    }

    @MainActor
    init(
        itemId: Int,
        onBack: @escaping () -> Void
    ) {
        self.init(
            itemId: itemId,
            viewModelFactory: AppDependencies.preview.makeItemDetailViewModel,
            onBack: onBack
        )
    }

    var body: some View {
        ZStack {
            content

            if let snackbar {
                VStack {
                    Spacer(minLength: 0)

                    OBRitSnackbar(message: snackbar.message, icon: .error)
                        .padding(.horizontal, OBRitSpacing.s5)
                        .padding(.bottom, OBRitSpacing.s6)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                }
                .allowsHitTesting(false)
                .animation(.easeOut(duration: 0.18), value: snackbar.id)
            }
        }
        .onChange(of: viewModel.effect) { _, effect in
            handleEffect(effect)
        }
        .onDisappear {
            snackbarDismissTask?.cancel()
        }
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
    }

    @ViewBuilder
    private var content: some View {
        switch viewModel.state {
        case .loading:
            ItemDetailRouteMessageView(title: "소모품 정보를 불러오는 중이에요")
        case let .loadFailed(message):
            ItemDetailRouteMessageView(title: message, buttonTitle: "다시 시도", action: viewModel.retry)
        case let .success(data):
            ZStack(alignment: .bottom) {
                OBRitColors.backgroundDefaultDefault
                    .ignoresSafeArea()

                ItemDetailStockManagementSheet(
                    itemName: data.item.name,
                    initialQuantity: data.spareDraft.quantity,
                    quantity: $quantity,
                    isProcessing: data.isProcessing,
                    onCommit: viewModel.updateSpareQuantity,
                    onDismiss: onBack
                )
            }
            .onAppear {
                syncQuantityIfNeeded(data.spareDraft.quantity)
            }
        }
    }

    private func syncQuantityIfNeeded(_ value: Int) {
        guard !hasSyncedInitialQuantity else { return }
        quantity = value
        hasSyncedInitialQuantity = true
    }

    private func handleEffect(_ effect: ItemDetailViewEffect?) {
        guard let effect else { return }
        defer { viewModel.clearEffect() }

        switch effect {
        case .itemUpdated:
            onMutationCompleted()
            onBack()
        case let .showMessage(message):
            showSnackbar(message)
        case .navigate, .itemDeleted, .replacementCompleted:
            break
        }
    }

    private func showSnackbar(_ message: String) {
        snackbarDismissTask?.cancel()
        snackbar = ItemDetailRouteSnackbarPresentation(message: message)
        snackbarDismissTask = Task { @MainActor in
            try? await Task.sleep(nanoseconds: ItemDetailRouteSnackbarMetrics.displayDurationNanoseconds)
            guard !Task.isCancelled else { return }
            snackbar = nil
        }
    }
}

private struct ItemDetailRouteSnackbarPresentation: Identifiable {
    let id = UUID()
    let message: String
}

private enum ItemDetailRouteSnackbarMetrics {
    static let displayDurationNanoseconds: UInt64 = 2_400_000_000
}

private struct ItemDetailRouteMessageView: View {
    let title: String
    var buttonTitle: String?
    var action: (() -> Void)?

    var body: some View {
        VStack(spacing: OBRitSpacing.s5) {
            Text(title)
                .multilineTextAlignment(.center)
                .obritTextStyle(
                    OBRitTypography.xl,
                    weight: OBRitFontWeight.semiBold,
                    color: OBRitColors.textDefaultDefault
                )

            if let buttonTitle, let action {
                OBRitFilledTextButton(text: buttonTitle, size: .middle, action: action)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(OBRitSpacing.s5)
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
    }
}
