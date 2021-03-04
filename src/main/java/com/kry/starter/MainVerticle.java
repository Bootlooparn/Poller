package com.kry.starter;

import io.vertx.core.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.HashSet;
import java.util.Set;

public class MainVerticle extends AbstractVerticle {
  Vertx vertx = Vertx.vertx();
  MySQLPool pool = new DB(vertx).getPool();
  PollingClient pollingClient = new PollingClient();

  @Override
  public void start() {
    Future future = createDatabases().compose( x -> startServer());
    future.onComplete(x -> System.out.println("All is up and running!"));

    vertx.setPeriodic(15000, x -> {
      pollingClient.poll(vertx);
    });
  }

  private Future createDatabases() {
    Promise promise = Promise.promise();

    pool.withTransaction(client -> client
      .query("create table if not exists users( user varchar(255), primary key(user))")
      .execute()
      .flatMap(res -> client
        .query("create table if not exists services( name varchar(255), url varchar(255), user varchar(255), status enum('OK', 'FAIL'), dateAdded timestamp, dateChanged timestamp, primary key(name, url, user), foreign key(user) references users(user))")
        .execute())
      )
      .onSuccess(v -> {
        System.out.println("Successfully created services and users tables!");
        promise.complete();
      })
      .onFailure(v -> System.out.println("failed to create services table" + v.getMessage()));

    return promise.future();
  }

  private Future startServer() {
    Promise promise = Promise.promise();
    System.out.println("starting!");
    HttpServer server = vertx.createHttpServer();

    server.requestHandler(getRoutes()).listen(3000).onComplete(x -> {
      System.out.println("Server is listening to port 3000");
      promise.complete();
    });
    return promise.future();
  }

  private Router getRoutes() {
    Router router = Router.router(vertx);

    Set<String> allowedHeaders = new HashSet<>();
    allowedHeaders.add("x-requested-with");
    allowedHeaders.add("Access-Control-Allow-Origin");
    allowedHeaders.add("origin");
    allowedHeaders.add("Content-Type");
    allowedHeaders.add("accept");

    Set<HttpMethod> allowedMethods = new HashSet<>();
    allowedMethods.add(HttpMethod.GET);
    allowedMethods.add(HttpMethod.POST);
    allowedMethods.add(HttpMethod.DELETE);


    router.route().handler(BodyHandler.create());
    router.route().handler(CorsHandler.create("*")
      .allowedHeaders(allowedHeaders)
      .allowedMethods(allowedMethods));

    router.route(HttpMethod.GET,"/").handler(ctx -> {
      HttpServerResponse response = ctx.response();
      response.end("Hello ze world");
    });

    router.route(HttpMethod.GET, "/users").handler(ctx -> {
      pool.query("select * from users")
        .execute(r -> {
          JsonArray array = new JsonArray();
          if (r.succeeded()) {
            RowSet<Row> rows = r.result();
            for (Row row: rows) {
              array.add(row.toJson());
            }
            ctx.json(array);
          } else {
            System.out.println("fetching users failed!");
            ctx.response().end();
          }
        });
    });

    router.route(HttpMethod.POST, "/users/add").handler(ctx -> {
      String user = ctx.getBodyAsJson().getString("user");

      pool.withTransaction(client -> client
      .preparedQuery("insert into users(user) values (?)")
      .execute(Tuple.of(user))
      .onSuccess(x -> System.out.println("user added!"))
      .onFailure(x -> System.out.println(x.getMessage()))
      );

      ctx.response().end();
    });

    router.route(HttpMethod.GET, "/services").handler(ctx -> {
      String user = ctx.pathParam("user");

      pool.query("select * from services").execute(r -> {
        if (r.succeeded()) {
          JsonArray array = new JsonArray();
          RowSet<Row> rows = r.result();
          for (Row row: rows) {
            array.add(row.toJson());
          }
          ctx.json(array);
        } else {
          System.out.println("failed");
          ctx.response().end();
        }
      });
    });

    router.route(HttpMethod.GET, "/services/:user").handler(ctx -> {
      String user = ctx.pathParam("user");

      pool.preparedQuery("select * from services where user=?").execute(Tuple.of(user) ,r -> {
        if (r.succeeded()) {
          JsonArray array = new JsonArray();
          RowSet<Row> rows = r.result();
          for (Row row: rows) {
            array.add(row.toJson());
          }
          ctx.json(array);
        } else {
          System.out.println("failed");
          ctx.response().end();
        }
      });
    });

    router.route(HttpMethod.POST,"/services/add").handler(ctx -> {
      String name = ctx.getBodyAsJson().getString("name");
      String url = ctx.getBodyAsJson().getString("url");
      String user = ctx.getBodyAsJson().getString("user");

      pool.withTransaction(client -> client
        .preparedQuery("insert into services( name, url, user, dateAdded) values(?, ?, ?, now()) on duplicate key update name=?, url=?, user=?, dateChanged=now()")
        .execute(Tuple.of(name, url, user, name, url, user))
        .onSuccess(x -> System.out.println("new service added"))
        .onFailure(x -> System.out.println(x.getMessage()))
      );
      ctx.response().end();
    });

    router.route(HttpMethod.DELETE,"/services/delete").handler(ctx -> {
      String name = ctx.getBodyAsJson().getString("name");
      String url = ctx.getBodyAsJson().getString("url");
      String user = ctx.getBodyAsJson().getString("user");

      pool.withTransaction(client -> client
        .preparedQuery("delete from services where name=? and user=? and url=?")
        .execute(Tuple.of(name, user, url))
        .onSuccess(x -> {
          System.out.println("a service has been deleted!");
          ctx.response().end();
        })
        .onFailure(x -> {
          System.out.println(x.getMessage());
          ctx.response().end();
        })
      );
    });

    return router;
  }
}
