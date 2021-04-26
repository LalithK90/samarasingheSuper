package lk.samarasingher_super.asset.item.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MainCategory {
COOKING_ESSENTIALS("Cooking Essentials"),
    RICE("Rice "),
    DAIRY_PRODUCTS("Dairy Products"),
    BABY_PRODUCTS("Baby Products"),
    PERSONAL_CARE("Personal Care"),
    HOUSEHOLD("Household");

    private final String mainCategory;
}
