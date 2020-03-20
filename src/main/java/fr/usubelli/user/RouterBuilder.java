package fr.usubelli.user;

import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BasicAuthHandler;

public class RouterBuilder {

    private final Router router;

    private RouterBuilder(Router router) {
        this.router = router;
    }

    public void htpasswd(AuthProvider authProvider, String realm) {
        router.route().handler(BasicAuthHandler.create(authProvider, realm));
    }

    public RouterBuilder protect() {
        router.route().handler(ctx -> {
            ctx.response()
                    // do not allow proxies to cache the data
                    .putHeader("Cache-Control", "no-store, no-cache")
                    // prevents Internet Explorer from MIME - sniffing a
                    // response away from the declared content-type
                    .putHeader("X-Content-Type-Options", "nosniff")
                    // Strict HTTPS (for about ~6Months)
                    .putHeader("Strict-Transport-Security", "max-age=" + 15768000)
                    // IE8+ do not allow opening of attachments in the context of this resource
                    .putHeader("X-Download-Options", "noopen")
                    // enable XSS for IE
                    .putHeader("X-XSS-Protection", "1; mode=block")
                    // deny frames
                    .putHeader("X-FRAME-OPTIONS", "DENY");
            ctx.next();
        });
        return this;
    }

    public static RouterBuilder router(Router router) {
        return new RouterBuilder(router);
    }

    public Router build() {
        return this.router;
    }
}
