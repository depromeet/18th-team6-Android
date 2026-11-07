import SwiftUI

enum ReceiptImagePreprocessor {
    static func jpegData(from image: UIImage) throws -> Data {
        guard let normalizedImage = image.normalizedForUpload() else {
            throw ReceiptImagePreprocessorError.invalidImage
        }

        var currentImage = normalizedImage.scaledToFit(longEdge: maxImageDimension)
        while true {
            let data = try currentImage.jpegDataUnderByteLimit(maxUploadBytes)
            if data.count < maxUploadBytes {
                return data
            }

            guard let nextImage = currentImage.scaled(by: downscaleFactor) else {
                throw ReceiptImagePreprocessorError.compressionFailed
            }
            currentImage = nextImage
        }
    }

    private static let maxImageDimension: Double = 2048
    private static let maxUploadBytes = 1_000_000
    private static let downscaleFactor: Double = 0.85
}

enum ReceiptImagePreprocessorError: LocalizedError {
    case invalidImage
    case compressionFailed

    var errorDescription: String? {
        switch self {
        case .invalidImage:
            "이미지를 불러오지 못했어요."
        case .compressionFailed:
            "이미지를 업로드 형식으로 변환하지 못했어요."
        }
    }
}

private extension UIImage {
    func normalizedForUpload() -> UIImage? {
        let format = UIGraphicsImageRendererFormat.default()
        format.scale = 1
        let renderer = UIGraphicsImageRenderer(size: size, format: format)
        return renderer.image { _ in
            draw(in: CGRect(origin: .zero, size: size))
        }
    }

    func scaledToFit(longEdge: Double) -> UIImage {
        let currentLongEdge = max(size.width, size.height)
        guard currentLongEdge > longEdge else { return self }
        return scaled(by: longEdge / currentLongEdge) ?? self
    }

    func scaled(by factor: Double) -> UIImage? {
        let nextSize = CGSize(
            width: max(1, floor(size.width * factor)),
            height: max(1, floor(size.height * factor))
        )
        guard nextSize != size else { return nil }
        let format = UIGraphicsImageRendererFormat.default()
        format.scale = 1
        let renderer = UIGraphicsImageRenderer(size: nextSize, format: format)
        return renderer.image { _ in
            draw(in: CGRect(origin: .zero, size: nextSize))
        }
    }

    func jpegDataUnderByteLimit(_ byteLimit: Int) throws -> Data {
        var quality = ReceiptImageCompression.initialQuality
        guard var data = jpegData(compressionQuality: quality) else {
            throw ReceiptImagePreprocessorError.compressionFailed
        }

        while data.count >= byteLimit && quality > ReceiptImageCompression.minimumQuality {
            quality -= ReceiptImageCompression.qualityStep
            guard let compressedData = jpegData(compressionQuality: quality) else {
                throw ReceiptImagePreprocessorError.compressionFailed
            }
            data = compressedData
        }

        return data
    }
}

private enum ReceiptImageCompression {
    static let initialQuality: Double = 0.9
    static let minimumQuality: Double = 0.6
    static let qualityStep: Double = 0.05
}
