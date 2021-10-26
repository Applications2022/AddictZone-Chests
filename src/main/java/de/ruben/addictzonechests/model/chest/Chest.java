package de.ruben.addictzonechests.model.chest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chest {

    private @NotNull UUID id;
    private @NotNull String name;
    private @NotNull List<ChestItem> items;

}
