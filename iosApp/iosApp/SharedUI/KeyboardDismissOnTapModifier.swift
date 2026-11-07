import SwiftUI
import UIKit

func dismissKeyboard() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
}

private struct KeyboardDismissOnTapModifier: ViewModifier {
    func body(content: Content) -> some View {
        content
            .background(KeyboardDismissTapInstaller())
    }
}

private struct KeyboardDismissTapInstaller: UIViewRepresentable {
    func makeCoordinator() -> Coordinator {
        Coordinator()
    }

    func makeUIView(context: Context) -> UIView {
        let view = UIView(frame: .zero)
        view.isUserInteractionEnabled = false
        return view
    }

    func updateUIView(_ uiView: UIView, context: Context) {
        DispatchQueue.main.async {
            context.coordinator.installIfNeeded(in: uiView.window)
        }
    }

    final class Coordinator: NSObject, UIGestureRecognizerDelegate {
        private weak var installedWindow: UIWindow?
        private var tapGestureRecognizer: UITapGestureRecognizer?

        func installIfNeeded(in window: UIWindow?) {
            guard let window else { return }
            guard installedWindow !== window else { return }

            if let tapGestureRecognizer, let installedWindow {
                installedWindow.removeGestureRecognizer(tapGestureRecognizer)
            }

            let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(handleBackgroundTap))
            tapGestureRecognizer.cancelsTouchesInView = false
            tapGestureRecognizer.delegate = self
            window.addGestureRecognizer(tapGestureRecognizer)

            self.installedWindow = window
            self.tapGestureRecognizer = tapGestureRecognizer
        }

        @objc private func handleBackgroundTap() {
            dismissKeyboard()
        }

        func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
            !touch.view.hasInteractiveAncestor
        }
    }
}

private extension Optional where Wrapped == UIView {
    var hasInteractiveAncestor: Bool {
        var currentView = self

        while let view = currentView {
            if view is UIControl || view is UITextField || view is UITextView {
                return true
            }
            currentView = view.superview
        }

        return false
    }
}

extension View {
    func dismissKeyboardOnBackgroundTap() -> some View {
        modifier(KeyboardDismissOnTapModifier())
    }
}
