package eu.trustdemocracy.users.infrastructure;

import eu.trustdemocracy.users.core.interactors.auth.GetToken;
import eu.trustdemocracy.users.core.interactors.auth.RefreshToken;
import eu.trustdemocracy.users.core.interactors.user.CreateUser;
import eu.trustdemocracy.users.core.interactors.user.DeleteUser;
import eu.trustdemocracy.users.core.interactors.user.GetUser;
import eu.trustdemocracy.users.core.interactors.user.GetUsers;
import eu.trustdemocracy.users.core.interactors.user.UpdateUser;

public interface InteractorFactory {

  CreateUser getCreateUser();

  DeleteUser getDeleteUser();

  GetUser getGetUser();

  GetUsers getGetUsers();

  UpdateUser getUpdateUser();

  GetToken getGetToken();

  RefreshToken getRefreshToken();
}
