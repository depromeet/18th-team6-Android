enum ItemAssetCatalog {
    struct Entry: Equatable {
        let title: String
        let assetName: String
    }

    static let entries: [Entry] = [
        Entry(title: "면도기", assetName: "item_razor"),
        Entry(title: "정수기 필터", assetName: "item_water_filter"),
        Entry(title: "칫솔", assetName: "item_toothbrush"),
        Entry(title: "세탁 세제", assetName: "item_detergent"),
        Entry(title: "수건", assetName: "item_towel"),
        Entry(title: "샤워기 필터", assetName: "item_shower_filter"),
        Entry(title: "수세미", assetName: "item_scrub_sponge"),
        Entry(title: "디퓨저", assetName: "item_diffuser"),
        Entry(title: "키친타월", assetName: "item_kitchen_towel"),
        Entry(title: "바디워시", assetName: "item_body_wash"),
        Entry(title: "샴푸", assetName: "item_shampoo"),
        Entry(title: "트리트먼트", assetName: "item_treatment"),
        Entry(title: "손소독제", assetName: "item_hand_sanitizer"),
        Entry(title: "치약", assetName: "item_toothpaste"),
        Entry(title: "물티슈", assetName: "item_wet_wipes"),
        Entry(title: "휴지", assetName: "item_toilet_paper"),
        Entry(title: "밴드", assetName: "item_bandage"),
        Entry(title: "생활용품", assetName: "item_misc"),
        Entry(title: "공기청정기 필터", assetName: "item_air_purifier_filter"),
        Entry(title: "쓰레기 봉투", assetName: "item_trash_bag"),
        Entry(title: "지퍼백", assetName: "item_zip_bag"),
        Entry(title: "칫솔 살균기 필터", assetName: "item_toothbrush_sanitizer_filter"),
        Entry(title: "빨래집게", assetName: "item_clothespin"),
        Entry(title: "고무장갑", assetName: "item_rubber_gloves"),
        Entry(title: "주방 세제", assetName: "item_dish_soap"),
        Entry(title: "섬유유연제", assetName: "item_fabric_softener"),
        Entry(title: "청소포", assetName: "item_cleaning_wipe"),
        Entry(title: "스펀지", assetName: "item_sponge"),
        Entry(title: "세탁망", assetName: "item_laundry_net"),
        Entry(title: "면봉", assetName: "item_cotton_swab"),
        Entry(title: "화장솜", assetName: "item_cotton_pad"),
        Entry(title: "폼클렌저", assetName: "item_foam_cleanser"),
        Entry(title: "마스크", assetName: "item_mask"),
        Entry(title: "디퓨저 리필", assetName: "item_diffuser_refill"),
        Entry(title: "배수구 거름망", assetName: "item_drain_filter"),
        Entry(title: "욕실 세정제", assetName: "item_bathroom_cleaner"),
        Entry(title: "행주", assetName: "item_dishcloth"),
        Entry(title: "랩/호일", assetName: "item_wrap_foil"),
        Entry(title: "샤워볼", assetName: "item_shower_ball"),
        Entry(title: "전구", assetName: "item_light_bulb"),
        Entry(title: "변기 세정제", assetName: "item_toilet_cleaner")
    ]

    static let assetNames = entries.map(\.assetName)

    static let accessibilityNames = Dictionary(uniqueKeysWithValues: entries.map { ($0.assetName, $0.title) })
}

enum CategoryIconKind: Int64, CaseIterable {
    case razor = 1
    case waterFilter
    case toothbrush
    case detergent
    case towel
    case showerFilter
    case scrubSponge
    case diffuser
    case kitchenTowel
    case bodyWash
    case shampoo
    case treatment
    case handSanitizer
    case toothpaste
    case wetWipes
    case toiletPaper
    case bandage
    case misc
    case airPurifierFilter
    case trashBag
    case zipBag
    case toothbrushSanitizerFilter
    case clothespin
    case rubberGloves
    case dishSoap
    case fabricSoftener
    case cleaningWipe
    case sponge
    case laundryNet
    case cottonSwab
    case cottonPad
    case foamCleanser
    case mask
    case diffuserRefill
    case drainFilter
    case bathroomCleaner
    case dishcloth
    case wrapFoil
    case showerBall
    case lightBulb
    case toiletCleaner

    var assetName: String {
        ItemAssetCatalog.assetNames[index]
    }

    private var index: Int {
        Int(rawValue - 1)
    }

    static func assetName(iconId: Int64, url: String) -> String {
        assetName(fromURL: url)
            ?? CategoryIconKind(rawValue: iconId)?.assetName
            ?? ItemRegistrationAsset.fallbackItemImage
    }

    private static func assetName(fromURL url: String) -> String? {
        let decodedURL = url.removingPercentEncoding ?? url
        return ItemAssetCatalog.assetNames.first { decodedURL.contains($0) }
    }
}

enum ItemRegistrationSampleData {
    static let itemKinds: [ItemKind] = {
        ItemAssetCatalog.entries.enumerated().map { index, entry in
            ItemKind(
                id: index + ItemRegistrationConfig.nextIDIncrement,
                title: entry.title,
                addedCount: ItemRegistrationConfig.newKindInitialAddedCount,
                imageAssetName: entry.assetName
            )
        }
    }()

    static let imageOptions: [ItemImageOption] = CategoryIconKind.allCases.map { kind in
        ItemImageOption(id: Int(kind.rawValue), assetName: kind.assetName)
    }
}
