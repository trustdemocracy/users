package eu.trustdemocracy.users.core.models.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetTokenResponseDTO {

  private String accessToken;
  private String refreshToken;
}
