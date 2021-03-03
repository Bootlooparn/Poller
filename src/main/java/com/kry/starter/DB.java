package com.kry.starter;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class DB {

  MySQLPool pool;
  public DB (Vertx vertx) {
    Properties prop = new Properties();
    try {
      InputStream input = new FileInputStream("gradle.properties");
      prop.load(input);
    } catch(Exception e) {
      System.out.println(e);
    }
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(Integer.parseInt(prop.get("port").toString()))
      .setHost(prop.getProperty("host"))
      .setDatabase(prop.getProperty("db"))
      .setUser(prop.getProperty("user"))
      .setPassword(prop.getProperty("password"));

    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

    pool = MySQLPool.pool(vertx, connectOptions, poolOptions);
  }

  public MySQLPool getPool() {
    return pool;
  }
}
