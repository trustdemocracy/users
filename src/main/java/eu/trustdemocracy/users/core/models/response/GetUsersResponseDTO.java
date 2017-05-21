package eu.trustdemocracy.users.core.models.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetUsersResponseDTO {

  private List<UserResponseDTO> users = new ArrayList<>();
}
