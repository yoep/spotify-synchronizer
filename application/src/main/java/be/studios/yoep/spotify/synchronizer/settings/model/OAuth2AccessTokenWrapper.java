package be.studios.yoep.spotify.synchronizer.settings.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class OAuth2AccessTokenWrapper {
    @NotNull
    private LocalDateTime expireDate;

    @NotNull
    private OAuth2AccessToken token;

    /**
     * Verify is the access token is expired.
     *
     * @return Returns true when expired, else false.
     */
    @JsonIgnore
    public boolean isExpired() {
        return expireDate.isBefore(LocalDateTime.now());
    }
}
