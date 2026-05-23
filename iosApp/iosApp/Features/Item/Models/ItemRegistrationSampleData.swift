enum ItemRegistrationSampleData {
    private static let itemEntries: [(title: String, assetName: String)] = [
        ("면도기", "item_razor"),
        ("정수기 필터", "item_water_filter"),
        ("칫솔", "item_toothbrush"),
        ("세탁 세제", "item_detergent"),
        ("수건", "item_towel"),
        ("샤워기 필터", "item_shower_filter"),
        ("수세미", "item_scrub_sponge"),
        ("디퓨저", "item_diffuser"),
        ("키친타월", "item_kitchen_towel"),
        ("바디워시", "item_body_wash"),
        ("샴푸", "item_shampoo"),
        ("린스", "item_treatment"),
        ("핸드워시", "item_hand_sanitizer"),
        ("치약", "item_toothpaste"),
        ("물티슈", "item_wet_wipes"),
        ("휴지", "item_toilet_paper"),
        ("건전지", "item_misc"),
        ("공기청정기 필터", "item_air_purifier_filter"),
        ("쓰레기 봉투", "item_trash_bag"),
        ("지퍼백", "item_zip_bag"),
        ("종이컵", "item_misc"),
        ("고무장갑", "item_rubber_gloves"),
        ("주방 세제", "item_dish_soap"),
        ("섬유유연제", "item_fabric_softener"),
        ("청소포", "item_cleaning_wipe"),
        ("스펀지", "item_sponge"),
        ("칫솔모", "item_toothbrush"),
        ("면봉", "item_cotton_swab"),
        ("화장솜", "item_cotton_pad"),
        ("클렌징 티슈", "item_wet_wipes"),
        ("비누", "item_foam_cleanser"),
        ("로션", "item_misc"),
        ("선크림", "item_misc"),
        ("마스크", "item_mask"),
        ("반려동물 패드", "item_misc"),
        ("방향제", "item_diffuser_refill"),
        ("제습제", "item_misc"),
        ("세면대 필터", "item_drain_filter"),
        ("욕실 세정제", "item_bathroom_cleaner"),
        ("행주", "item_dishcloth"),
        ("랩", "item_wrap_foil")
    ]

    private static let imageOptionAssetNames = [
        "item_razor",
        "item_water_filter",
        "item_toothbrush",
        "item_detergent",
        "item_towel",
        "item_shower_filter",
        "item_scrub_sponge",
        "item_diffuser",
        "item_kitchen_towel",
        "item_body_wash",
        "item_shampoo",
        "item_treatment",
        "item_hand_sanitizer",
        "item_toothpaste",
        "item_wet_wipes",
        "item_toilet_paper",
        "item_bandage",
        "item_misc",
        "item_air_purifier_filter",
        "item_trash_bag",
        "item_zip_bag",
        "item_toothbrush_sanitizer_filter",
        "item_clothespin",
        "item_rubber_gloves",
        "item_dish_soap",
        "item_fabric_softener",
        "item_cleaning_wipe",
        "item_sponge",
        "item_laundry_net",
        "item_cotton_swab",
        "item_cotton_pad",
        "item_foam_cleanser",
        "item_mask",
        "item_diffuser_refill",
        "item_drain_filter",
        "item_bathroom_cleaner",
        "item_dishcloth",
        "item_wrap_foil",
        "item_shower_ball",
        "item_light_bulb",
        "item_toilet_cleaner"
    ]

    static let itemKinds: [ItemKind] = {
        itemEntries.enumerated().map { index, entry in
            ItemKind(
                id: index + 1,
                title: entry.title,
                addedCount: 0,
                imageAssetName: entry.assetName
            )
        }
    }()

    static let imageOptions: [ItemImageOption] = imageOptionAssetNames.enumerated().map { index, assetName in
        ItemImageOption(id: index, assetName: assetName)
    }
}
