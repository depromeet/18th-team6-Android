import Foundation

enum OBRitSharedAssetMapper {
    static func itemAssetName(for name: String) -> String {
        let normalizedName = name.trimmingCharacters(in: .whitespacesAndNewlines)

        for (keyword, assetName) in keywordAssetPairs where normalizedName.contains(keyword) {
            return assetName
        }

        return fallbackItemAssetName
    }

    static func homeOrbAssetName(for name: String) -> String {
        switch itemAssetName(for: name) {
        case "item_toothbrush":
            return "home_orb_toothbrush"
        case "item_razor":
            return "home_orb_razor"
        case "item_shower_filter":
            return "home_orb_shower_filter"
        case "item_detergent":
            return "home_orb_detergent"
        case "item_towel":
            return "home_orb_towel"
        case "item_diffuser":
            return "home_orb_diffuser"
        case "item_scrub_sponge", "item_sponge":
            return "home_orb_sponge"
        default:
            return fallbackItemAssetName
        }
    }

    static let fallbackItemAssetName = "item_misc"

    private static let keywordAssetPairs: [(keyword: String, assetName: String)] = [
        ("칫솔", "item_toothbrush"),
        ("치약", "item_toothpaste"),
        ("면도", "item_razor"),
        ("수세미", "item_scrub_sponge"),
        ("스펀지", "item_sponge"),
        ("샤워기", "item_shower_filter"),
        ("샤워볼", "item_shower_ball"),
        ("필터", "item_water_filter"),
        ("세탁", "item_detergent"),
        ("세제", "item_detergent"),
        ("주방 세제", "item_dish_soap"),
        ("디퓨저", "item_diffuser"),
        ("수건", "item_towel"),
        ("키친타월", "item_kitchen_towel"),
        ("휴지", "item_toilet_paper"),
        ("물티슈", "item_wet_wipes"),
        ("마스크", "item_mask"),
        ("쓰레기", "item_trash_bag"),
        ("지퍼백", "item_zip_bag"),
        ("바디워시", "item_body_wash"),
        ("샴푸", "item_shampoo"),
        ("클렌저", "item_foam_cleanser"),
        ("장갑", "item_rubber_gloves"),
        ("전구", "item_light_bulb")
    ]
}
