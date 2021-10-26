package model.chest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRarity {

    private @NotNull String name;
    private @NotNull String prefix;
    private @NotNull String message;
    private @NotNull Integer weight;
}
