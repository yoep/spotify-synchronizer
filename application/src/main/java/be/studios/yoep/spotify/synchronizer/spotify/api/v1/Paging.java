package be.studios.yoep.spotify.synchronizer.spotify.api.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Paging<T> {
    @NotNull
    private String href;
    @NotNull
    private List<T> items;
    @NotNull
    private Integer limit;
    private String next;
    private Integer offset;
    private String previous;
    @NotNull
    private Integer total;
}
