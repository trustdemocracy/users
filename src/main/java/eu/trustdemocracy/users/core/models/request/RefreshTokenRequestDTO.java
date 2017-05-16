package eu.trustdemocracy.users.core.models.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RefreshTokenRequestDTO {

  private String accessToken;
  private String refreshToken;
}
