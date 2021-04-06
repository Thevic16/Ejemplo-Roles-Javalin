import io.javalin.Javalin;
import io.javalin.core.security.Role;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static io.javalin.core.security.SecurityUtil.roles;

enum MyRole implements Role {
    ANYONE, ROLE_ONE, ROLE_TWO, ROLE_THREE;
}

public class Main {
    private static Javalin app;

    public static void main(String[] args){
        System.out.println("Hola Mundo");

        app = Javalin.create().start(7000);

        aplicandoRutas();

    }

    public static void aplicandoRutas(){
        // Set the access-manager that Javalin should use
        app.config.accessManager((handler, ctx, permittedRoles) -> {
            HashMap<String, MyRole> userRole = getUserRoles();
            if (permittedRoles.contains(userRole.get("ROLE_ONE")) || permittedRoles.contains(userRole.get("ANYONE")) ) {
                handler.handle(ctx);
            } else {
                ctx.status(401).result("Unauthorized");
            }
        });

        app.routes(() -> {
            app.get("/anyone-one",   ctx -> ctx.result("Hello"),   roles(MyRole.ANYONE,MyRole.ROLE_ONE));
            app.get("/anyone",   ctx -> ctx.result("Hello"),   roles(MyRole.ANYONE));
            app.get("/one",      ctx -> ctx.result("Hello"),   roles(MyRole.ROLE_ONE));
            app.get("/two",      ctx -> ctx.result("Hello"),   roles(MyRole.ROLE_TWO));
            app.get("/", ctx -> ctx.result("Hello World")); // No permite entrar.
        });
    }

    static HashMap<String, MyRole> getUserRoles() {
        // determine user role based on request
        // typically done by inspecting headers
        HashMap<String, MyRole> myRoles = new HashMap<String, MyRole>();
        myRoles.put("ANYONE",MyRole.ANYONE);
        //myRoles.put("ROLE_ONE",MyRole.ROLE_ONE);

        return myRoles;
    }
}


