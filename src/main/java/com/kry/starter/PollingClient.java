package com.kry.starter;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class PollingClient {

  public void poll(Vertx vertx) {
    MySQLPool pool = new DB(vertx).getPool();
    WebClient client = WebClient.create(vertx);

    pool.query("select * from services").execute(r -> {
      if (r.succeeded()) {
        RowSet<Row> rows = r.result();
        for (Row row: rows) {
            String name = row.getString("name");
            String user = row.getString("user");
            String url = row.getString("url");

            client.get(80,url, "/")
              .timeout(2000)
              .send()
              .onSuccess(res -> {
                System.out.println(res.statusCode());
                pool.preparedQuery("update services set status=? where name=? and user=? and url=?").execute(Tuple.of("OK", name, user, url))
                  .onSuccess(x -> System.out.println("Status updated!"))
                  .onFailure(x -> System.out.println(x.getMessage()));
              })
              .onFailure(res -> {
                System.out.println(res.getMessage());
                pool.preparedQuery("update services set status=? where name=? and user=? and url=?").execute(Tuple.of("FAIL", name, user, url))
                  .onSuccess(x -> System.out.println("Status updated!"))
                  .onFailure(x -> System.out.println(x.getMessage()));
              });
        }
      } else {
        System.out.println("nothing to poll");
        pool.close();
      }
    });
  }
}
