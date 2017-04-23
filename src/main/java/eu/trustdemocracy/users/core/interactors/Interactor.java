package eu.trustdemocracy.users.core.interactors;

public interface Interactor<RequestDTO, ResponseDTO> {
  ResponseDTO execute(RequestDTO requestDTO);
}
