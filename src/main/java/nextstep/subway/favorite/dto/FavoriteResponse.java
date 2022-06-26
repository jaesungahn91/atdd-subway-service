package nextstep.subway.favorite.dto;

import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class FavoriteResponse {

    private Long id;
    private StationResponse source;
    private StationResponse target;

    public FavoriteResponse(Favorite favorite) {
        this.id = favorite.getId();
        this.source = StationResponse.of(favorite.getSource());
        this.target = StationResponse.of(favorite.getTarget());
    }

    protected FavoriteResponse() {
    }

    public static FavoriteResponse of(Favorite favorite) {
        return new FavoriteResponse(favorite);
    }

    public static List<FavoriteResponse> of(List<Favorite> favorites) {
        return favorites.stream()
                .map(FavoriteResponse::of)
                .collect(toList());
    }

    public Long getId() {
        return id;
    }

    public StationResponse getSource() {
        return source;
    }

    public StationResponse getTarget() {
        return target;
    }

}
