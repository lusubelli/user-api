package fr.usubelli.common;

import io.vertx.ext.web.Router;

public interface VertxMicroService {

    void route(Router router);

}
