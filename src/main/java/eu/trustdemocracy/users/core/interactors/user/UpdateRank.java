package eu.trustdemocracy.users.core.interactors.user;

import eu.trustdemocracy.users.core.interactors.Interactor;
import eu.trustdemocracy.users.core.models.request.RankRequestDTO;
import eu.trustdemocracy.users.gateways.repositories.UserRepository;

public class UpdateRank implements Interactor<RankRequestDTO, Boolean> {

  private UserRepository userRepository;

  public UpdateRank(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Boolean execute(RankRequestDTO rankRequestDTO) {
    userRepository.updateRanks(rankRequestDTO.getRankings());
    return true;
  }
}
