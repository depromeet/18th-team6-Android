enum ManualRegistrationSampleData {
    static let consumableKinds: [ManualConsumableKind] = {
        let assets = ["manual_consumable_razor", "manual_consumable_pouch", "manual_consumable_bottle"]
        let titles = [
            "면도기",
            "정수기 필터",
            "칫솔",
            "세탁 세제",
            "수건",
            "샤워기 필터",
            "수세미",
            "디퓨저",
            "키친타월",
            "바디워시",
            "샴푸",
            "린스",
            "핸드워시",
            "치약",
            "물티슈",
            "휴지",
            "건전지",
            "공기청정기 필터",
            "쓰레기 봉투",
            "지퍼백",
            "종이컵",
            "고무장갑",
            "주방 세제",
            "섬유유연제",
            "청소포",
            "스펀지",
            "칫솔모",
            "면봉",
            "화장솜",
            "클렌징 티슈",
            "비누",
            "로션",
            "선크림",
            "마스크",
            "반려동물 패드",
            "방향제",
            "제습제",
            "세면대 필터",
            "욕실 세정제",
            "행주",
            "랩"
        ]

        return titles.enumerated().map { index, title in
            ManualConsumableKind(
                id: index + 1,
                title: title,
                addedCount: 0,
                imageAssetName: assets[index % assets.count]
            )
        }
    }()

    static let imageOptions: [ManualConsumableImageOption] = (0..<41).map { index in
        let assets = ["manual_consumable_razor", "manual_consumable_pouch", "manual_consumable_bottle"]
        return ManualConsumableImageOption(id: index, assetName: assets[index % assets.count])
    }
}
