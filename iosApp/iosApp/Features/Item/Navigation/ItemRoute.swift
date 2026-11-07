enum ItemRoute: OBRitRoute {
    case registrationMethod
    case receiptAnalyze
    case itemRegistration
    case itemDetailInput
    case search
    case filter
    case sort
    case detail(itemId: Int)
    case statusInfo(itemId: Int)
    case edit(itemId: Int)
    case notification(itemId: Int)
    case delete(itemId: Int)
    case spareEdit(itemId: Int)
    case replacementComplete(itemId: Int)
}
