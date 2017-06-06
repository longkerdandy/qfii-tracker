package com.github.longkerdandy.qfii.hkex.storage;

import com.github.longkerdandy.qfii.hkex.model.StockShareholding;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

/**
 * InfluxDB Storage
 */
public class InfluxDBStorage {

  private static final String DB_NAME = "stocks";
  private static final String SHAREHOLDING_MEASUREMENT = "shareholding";
  private final InfluxDB influxDB;

  public InfluxDBStorage(String url, String username, String password) {
    // connect to influxDB
    this.influxDB = InfluxDBFactory.connect(url, username, password);

    // create database
    this.influxDB.createDatabase(DB_NAME);
  }

  public void clear() {
    this.influxDB.deleteDatabase(DB_NAME);
    this.influxDB.createDatabase(DB_NAME);
  }

  public void saveShareholdings(List<StockShareholding> stockShareholdings) {
    BatchPoints batchPoints = BatchPoints
        .database(DB_NAME)
        .tag("async", "true")
        .retentionPolicy("autogen")
        .consistency(ConsistencyLevel.ALL)
        .build();

    for (StockShareholding stockShareholding : stockShareholdings) {
      Point point = Point
          .measurement(SHAREHOLDING_MEASUREMENT)
          .time(stockShareholding.getDate().getTime(), TimeUnit.MILLISECONDS)
          .tag("code", stockShareholding.getCode())
          .tag("name", stockShareholding.getName())
          .addField("shareholding", stockShareholding.getShareholding())
          .addField("percent", stockShareholding.getPercent())
          .build();
      batchPoints.point(point);
    }

    this.influxDB.write(batchPoints);
  }
}
