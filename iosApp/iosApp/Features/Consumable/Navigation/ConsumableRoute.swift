enum ConsumableRoute: OBRitRoute {
    case registrationMethod
    case manualRegistration
    case manualDetailInput
    case receiptCaptureOrUpload
    case receiptRecognitionReview
    case receiptDetailInput
    case registrationComplete
    case search
    case filter
    case sort
    case detail(consumableId: Int)
    case statusInfo(consumableId: Int)
    case edit(consumableId: Int)
    case delete(consumableId: Int)
    case spareEdit(consumableId: Int)
    case replacementComplete(consumableId: Int)
}
