package model.chest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChestItem {

    private @NotNull ObjectId id;
    private @NotNull Map<String, Object> itemStack;
    private @NotNull ItemRarity itemRarity;
    private @NotNull ItemType itemType;
    private @Nullable String command;

}
