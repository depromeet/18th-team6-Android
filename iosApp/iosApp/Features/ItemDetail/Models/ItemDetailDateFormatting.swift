import Foundation

extension Date {
    var itemDetailMonthDayText: String {
        let components = Calendar.current.dateComponents([.month, .day], from: self)
        return "\(components.month ?? 1)월 \(components.day ?? 1)일"
    }

    var itemDetailRecordedAtText: String {
        let components = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute], from: self)
        let hour = components.hour ?? 0
        let minute = components.minute ?? 0
        let meridiem = hour < 12 ? "오전" : "오후"
        let displayHour = hour == 0 ? 12 : (hour > 12 ? hour - 12 : hour)

        return String(
            format: "%04d. %02d. %02d %@ %02d:%02d 기록됨",
            components.year ?? 2026,
            components.month ?? 1,
            components.day ?? 1,
            meridiem,
            displayHour,
            minute
        )
    }
}
