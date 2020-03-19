package fr.usubelli.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.usubelli.user.adapter.MongoUserRepository;
import fr.usubelli.user.dto.User;
import fr.usubelli.user.exception.UserAlreadyExistsException;
import fr.usubelli.user.exception.UserNotFoundException;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class UserVertx implements VertxMicroService {

    private static final String APPLICATION_JSON = "application/json";

    private final MongoUserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserVertx(MongoUserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public void route(Router router) {
        router.route().handler(BodyHandler.create());
        router.post("/user")
                .produces(APPLICATION_JSON)
                .handler(createUser());
        router.get("/user/:email")
                .produces(APPLICATION_JSON)
                .handler(findUserByEmail());
        router.put("/user")
                .produces(APPLICATION_JSON)
                .handler(updateUser());
    }

    private Handler<RoutingContext> createUser() {
        return rc -> {
            try {
                rc.response().setStatusCode(200).end(
                        objectMapper.writeValueAsString(
                                userRepository.createUser(
                                        objectMapper.readValue(rc.getBodyAsString(), User.class))));
            } catch (UserAlreadyExistsException e) {
                rc.response().setStatusCode(409).end();
            } catch (Exception e) {
                e.printStackTrace();
                rc.response().setStatusCode(500).end();
            }
        };
    }

    private Handler<RoutingContext> findUserByEmail() {
        return rc -> {
            try {
                rc.response().setStatusCode(200).end(
                        objectMapper.writeValueAsString(
                                userRepository.findUser(
                                        rc.request().getParam("email"))));
            } catch (UserNotFoundException e) {
                rc.response().setStatusCode(404).end();
            } catch (Exception e) {
                e.printStackTrace();
                rc.response().setStatusCode(500).end();
            }
        };
    }

    private Handler<RoutingContext> updateUser() {
        return rc -> {
            try {
                rc.response().setStatusCode(200).end(
                        objectMapper.writeValueAsString(
                                userRepository.updateUser(
                                        objectMapper.readValue(rc.getBodyAsString(), User.class))));
            } catch (UserNotFoundException e) {
                rc.response().setStatusCode(404).end();
            } catch (Exception e) {
                e.printStackTrace();
                rc.response().setStatusCode(500).end();
            }
        };
    }

}
