package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
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
    public boolean isExpired() {
        return expireDate.isAfter(LocalDateTime.now());
    }
}
