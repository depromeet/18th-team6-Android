enum OnboardingDefaults {
    static let groups: [OnboardingCategoryGroup] = [
        OnboardingCategoryGroup(id: 1, name: "욕실", displayOrder: 1),
        OnboardingCategoryGroup(id: 2, name: "주방", displayOrder: 2),
        OnboardingCategoryGroup(id: 3, name: "청소·세탁", displayOrder: 3)
    ]

    static let options: [OnboardingItemOption] = [
        OnboardingItemOption(
            id: 1,
            title: "면도기",
            addedCount: 0,
            imageURL: "",
            categoryGroupId: 1
        ),
        OnboardingItemOption(
            id: 2,
            title: "정수기 필터",
            addedCount: 0,
            imageURL: "",
            categoryGroupId: 2
        ),
        OnboardingItemOption(
            id: 3,
            title: "칫솔",
            addedCount: 0,
            imageURL: "",
            categoryGroupId: 1
        ),
        OnboardingItemOption(
            id: 4,
            title: "세탁 세제",
            addedCount: 0,
            imageURL: "",
            categoryGroupId: 3
        ),
        OnboardingItemOption(
            id: 5,
            title: "수건",
            addedCount: 0,
            imageURL: "",
            categoryGroupId: 1
        ),
        OnboardingItemOption(
            id: 6,
            title: "샤워기 필터",
            addedCount: 0,
            imageURL: "",
            categoryGroupId: 1
        ),
        OnboardingItemOption(
            id: 7,
            title: "수세미",
            addedCount: 0,
            imageURL: "",
            categoryGroupId: 2
        )
    ]
}
