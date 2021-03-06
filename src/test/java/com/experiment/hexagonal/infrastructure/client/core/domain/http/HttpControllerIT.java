package com.experiment.hexagonal.infrastructure.client.core.domain.http;

import com.experiment.hexagonal.AppConfigTest;
import com.experiment.hexagonal.core.api.CrudAdresse;
import com.experiment.hexagonal.core.domain.AdresseService;
import com.experiment.hexagonal.core.domain.UserLoginService;
import com.experiment.hexagonal.core.domain.UserService;
import com.experiment.hexagonal.core.factory.UserFactory;
import com.experiment.hexagonal.core.spi.AdresseRepository;
import com.experiment.hexagonal.core.spi.UserRepository;
import com.experiment.hexagonal.infrastructure.application.adapter.ApplicationCoreAdresseAdpateur;
import com.experiment.hexagonal.infrastructure.application.adapter.ApplicationCoreAuthentificationAdpateur;
import com.experiment.hexagonal.infrastructure.application.adapter.ApplicationCoreFindUserByEmailAdpateur;
import com.experiment.hexagonal.infrastructure.application.adapter.ApplicationCoreUserAdpateur;
import com.experiment.hexagonal.infrastructure.application.core.api.ApplicationAuthentification;
import com.experiment.hexagonal.infrastructure.application.core.api.ApplicationCreateUser;
import com.experiment.hexagonal.infrastructure.application.core.api.ApplicationCrudAdresse;
import com.experiment.hexagonal.infrastructure.application.core.api.ApplicationDeleteUser;
import com.experiment.hexagonal.infrastructure.application.core.api.ApplicationFindUserByEmail;
import com.experiment.hexagonal.infrastructure.application.core.api.ApplicationUpdateUser;
import com.experiment.hexagonal.infrastructure.application.core.domain.ClientAuthentificationService;
import com.experiment.hexagonal.infrastructure.application.core.domain.ClientCreateUserService;
import com.experiment.hexagonal.infrastructure.application.core.domain.ClientCrudAdresseService;
import com.experiment.hexagonal.infrastructure.application.core.domain.ClientDeleteUserService;
import com.experiment.hexagonal.infrastructure.application.core.domain.ClientFindUserByEmailService;
import com.experiment.hexagonal.infrastructure.application.core.domain.ClientUpdateUserService;
import com.experiment.hexagonal.infrastructure.application.core.model.ClientAdresse;
import com.experiment.hexagonal.infrastructure.application.core.model.ClientUser;
import com.experiment.hexagonal.infrastructure.application.core.spi.APICreateUser;
import com.experiment.hexagonal.infrastructure.application.core.spi.APICrudAdresse;
import com.experiment.hexagonal.infrastructure.application.core.spi.APIDeleteUser;
import com.experiment.hexagonal.infrastructure.application.core.spi.APIFindByEmail;
import com.experiment.hexagonal.infrastructure.application.core.spi.APIUpdateUser;
import com.experiment.hexagonal.infrastructure.client.adapter.ClientApplicationCrudAdresseAdpateur;
import com.experiment.hexagonal.infrastructure.client.adapter.ClientApplicationAuthentificationAdpateur;
import com.experiment.hexagonal.infrastructure.client.adapter.ClientApplicationFindUserByEmailAdpateur;
import com.experiment.hexagonal.infrastructure.client.adapter.ClientApplicationUserAdpateur;
import com.experiment.hexagonal.infrastructure.client.core.spi.ClientAuthentification;
import com.experiment.hexagonal.infrastructure.client.core.spi.ClientCreateUser;
import com.experiment.hexagonal.infrastructure.client.core.spi.ClientCrudAdresse;
import com.experiment.hexagonal.infrastructure.client.core.spi.ClientDeleteUser;
import com.experiment.hexagonal.infrastructure.client.core.spi.ClientFindUserByEmail;
import com.experiment.hexagonal.infrastructure.client.core.spi.ClientUpdateUser;
import com.experiment.hexagonal.infrastructure.repository.database.adapter.DatabaseUserRepository;
import com.experiment.hexagonal.infrastructure.repository.database.core.api.CrudDatabaseUser;
import com.experiment.hexagonal.infrastructure.repository.database.core.domain.DatabaseUserService;
import com.experiment.hexagonal.infrastructure.repository.inMemory.adapter.InMemoryAdresseRepository;
import com.experiment.hexagonal.infrastructure.repository.inMemory.adapter.InMemoryUserRepository;
import com.experiment.hexagonal.infrastructure.repository.inMemory.core.api.CrudInMemoryAdresse;
import com.experiment.hexagonal.infrastructure.repository.inMemory.core.api.CrudInMemoryUser;
import com.experiment.hexagonal.infrastructure.repository.inMemory.core.domain.set.InMemorySetAdresseService;
import com.experiment.hexagonal.infrastructure.repository.inMemory.core.domain.set.InMemorySetUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigTest.class})
public class HttpControllerIT {
    @Autowired
    private HttpController controller;
    
    // @Before
    public void setUp() {
        CrudInMemoryUser crudInMemoryUser = new InMemorySetUserService();
        UserRepository inMemoryUserRepository = new InMemoryUserRepository(crudInMemoryUser);
        
        CrudDatabaseUser crudDatabaseUser = new DatabaseUserService();
        UserRepository databaseUserRepository = new DatabaseUserRepository(crudDatabaseUser);       
        
        UserLoginService userLoginService = new UserLoginService(databaseUserRepository);
        
        UserFactory userFactory = new UserFactory();        
        UserService userSerivce = new UserService(inMemoryUserRepository, userFactory);
        UserService createUser = userSerivce;
        UserService updateUser = createUser;
        UserService deleteUser = createUser;
        UserService findUser = createUser;
        
        ApplicationCoreAuthentificationAdpateur apiAuthentification = new ApplicationCoreAuthentificationAdpateur(userLoginService);
        ApplicationCoreUserAdpateur apiUser = new ApplicationCoreUserAdpateur(createUser, updateUser, deleteUser);
        APICreateUser apiCreateUser = apiUser;
        APIUpdateUser apiUpdateUser = apiUser;
        APIDeleteUser apiDeleteUser = apiUser;        
        APIFindByEmail apiFindUserByEmail = new ApplicationCoreFindUserByEmailAdpateur(findUser);
        
        ApplicationAuthentification applicationAuthentification = new ClientAuthentificationService(apiAuthentification);
        ApplicationCreateUser applicationCreateUser = new ClientCreateUserService(apiCreateUser);
        ApplicationUpdateUser applicationUpdateUser = new ClientUpdateUserService(apiUpdateUser);
        ApplicationDeleteUser applicationDeleteUser = new ClientDeleteUserService(apiDeleteUser);
        ApplicationFindUserByEmail applicationFindUserByEmail = new ClientFindUserByEmailService(apiFindUserByEmail);
        
        ClientAuthentification clientAuthentification = new ClientApplicationAuthentificationAdpateur(applicationAuthentification);
        ClientApplicationUserAdpateur applicationUserAdpateur = new ClientApplicationUserAdpateur(applicationCreateUser, applicationUpdateUser, applicationDeleteUser);
        ClientCreateUser clientCreateUser = applicationUserAdpateur;
        ClientUpdateUser clientUpdateUser = applicationUserAdpateur;
        ClientDeleteUser clientDeleteUser = applicationUserAdpateur;
        ClientFindUserByEmail clientApplicationFindUserByEmail = new ClientApplicationFindUserByEmailAdpateur(applicationFindUserByEmail);
        
        CrudInMemoryAdresse crudInMemoryAdresse = new InMemorySetAdresseService();
        AdresseRepository adresseRepository = new InMemoryAdresseRepository(crudInMemoryAdresse);
        CrudAdresse crudAdresse = new AdresseService(adresseRepository);
        APICrudAdresse apiCrudAdresse = new ApplicationCoreAdresseAdpateur(crudAdresse);
        ApplicationCrudAdresse applicationCrudAdresse = new ClientCrudAdresseService(apiCrudAdresse);
        ClientCrudAdresse clientCrudAdresse = new ClientApplicationCrudAdresseAdpateur(applicationCrudAdresse);
                
        controller = new HttpController(
                clientAuthentification,
                clientCreateUser, 
                clientUpdateUser, 
                clientDeleteUser,
                clientApplicationFindUserByEmail,
                clientCrudAdresse);
    }

    @Test
    public void doitCreerUnUser() {
        // WHEN
        controller.createUser("email", "password", "MR", "fullName");
        
        // THEN
        assertThat(controller.findUserWithEmail("email")).extracting(ClientUser::getFullName)
                .containsExactly("fullName");
    }

    @Test
    public void doitRetournerUnUserParSonEmail() {
        // THEN
        controller.createUser("email", "password", null, "fullName");
        
        // WHEN
        ClientUser clientUser = controller.findUserWithEmail("email");
        
        // THEN
        assertThat(clientUser).extracting(ClientUser::getEmail, ClientUser::getFullName)
                .containsExactly("email", "fullName");
    }

    @Test
    public void doitRetournerNullSiUnUserNExistePas() {
        // THEN
        controller.createUser("email", "password", "MME", "fullName");
        
        // WHEN
        ClientUser clientUser = controller.findUserWithEmail("email incorrect");
        
        // THEN
        assertThat(clientUser).isNull();
    }

    @Test
    public void doitRetournerLeFullNameDUnUserParSonEmail() {
        // THEN
        controller.createUser("email", "password", "X", "fullName");
        
        // WHEN
        String found = controller.findUserFullNameWithEmail("email");
        
        // THEN
        assertThat(found).isEqualTo("fullName");
    }
    
    @Test
    public void doitMettreAJourUnUser() {
        // GIVEN
        controller.createUser("email", "password", null, "fullName");
        ClientUser user = controller.findUserWithEmail("email");
                
        // WHEN
        controller.updateUser(user.getId(), "nouveau email", "X", "nouveau fullName");
        
        // THEN
        assertThat(controller.findUserWithEmail("email")).isNull();
        assertThat(controller.findUserWithEmail("nouveau email"))
                .extracting(
                    ClientUser::getGender, ClientUser::getFullName)
                .containsExactly(
                        "X", "nouveau fullName");
    }
    
    @Test
    public void doitRetirerUnUser() {
        // GIVEN
        controller.createUser("email", "password", null, "fullName");
        ClientUser user = controller.findUserWithEmail("email");
                
        // WHEN
        controller.deleteUser(user.getId());
        
        // THEN
        assertThat(controller.findUserWithEmail("email")).isNull();
    }

    @Test
    public void doitCreerUneAdresse() {
        // WHEN
        controller.createAdresse("ville");
        
        // THEN
        assertThat(controller.findAdresseWithVille("ville")).extracting(ClientAdresse::getVille)
                .containsExactly("ville");
    }
    
    @Test
    public void doitMettreAJourUneAdresse() {
        // GIVEN
        controller.createAdresse("ville");
        ClientAdresse adresse = controller.findAdresseWithVille("ville");
                
        // WHEN
        controller.updateAdresse(adresse.getId(), "nouvelle adresse");
        
        // THEN
        assertThat(controller.findAdresseWithVille("ville")).isNull();
        assertThat(controller.findAdresseWithVille("nouvelle adresse"))
                .extracting(ClientAdresse::getVille)
                .containsExactly("nouvelle adresse");
    }
    
    @Test
    public void doitRetirerUeAdresse() {
        // GIVEN
        controller.createAdresse("ville");
        ClientAdresse adresse = controller.findAdresseWithVille("ville");
                
        // WHEN
        controller.deleteAdresse(adresse.getId());
        
        // THEN
        assertThat(controller.findAdresseWithVille("ville")).isNull();
    }
}
