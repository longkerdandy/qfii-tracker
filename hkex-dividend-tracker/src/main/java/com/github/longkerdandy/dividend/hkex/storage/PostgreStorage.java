package com.github.longkerdandy.dividend.hkex.storage;

import com.github.longkerdandy.dividend.hkex.model.StockDividend;
import com.zaxxer.hikari.HikariDataSource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

/**
 * PostgreSQL Storage
 */
public class PostgreStorage {

  private final DBI dbi;

  public PostgreStorage(String url, String username, String password) {
    HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(url);
    ds.setUsername(username);
    ds.setPassword(password);
    this.dbi = new DBI(ds);
    createTables();
  }

  private void createTables() {
    try (Handle h = this.dbi.open()) {
      h.execute(
          "create table if not exists dividend (code varchar(100) primary key, name varchar(100), public_date timestamp with time zone, year varchar(100), reason varchar(100), description varchar(100), method varchar(100), "
              + "rights_off_date timestamp with time zone, transfer_begin timestamp with time zone, transfer_end timestamp with time zone, divide_date timestamp with time zone)");
      h.execute(
          "create index if not exists rights_off_idx on dividend (rights_off_date)");
    }
  }

  public void saveDividend(StockDividend dividend) {
    try (Handle h = this.dbi.open()) {
      h.createStatement(
          "delete from dividend where code = :code")
          .bind("code", dividend.getCode())
          .execute();

      h.createStatement(
          "insert into dividend "
              + "(code, name, public_date, year, reason, description, method, rights_off_date, transfer_begin, transfer_end, divide_date) "
              + "values (:code, :name, :public_date, :year, :reason, :description, :method, :rights_off_date, :transfer_begin, :transfer_end, :divide_date)")
          .bind("code", dividend.getCode())
          .bind("name", dividend.getName())
          .bind("public_date", dividend.getPublicDate())
          .bind("year", dividend.getYear())
          .bind("reason", dividend.getReason())
          .bind("description", dividend.getDescription())
          .bind("method", dividend.getMethod())
          .bind("rights_off_date", dividend.getRightsOffDate())
          .bind("transfer_begin", dividend.getTransferBegin())
          .bind("transfer_end", dividend.getTransferEnd())
          .bind("divide_date", dividend.getDivideDate())
          .execute();
    }
  }
}
