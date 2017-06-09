package com.github.longkerdandy.qfii.hkex.storage;

import com.github.longkerdandy.qfii.hkex.model.StockShareholding;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DateFormatUtils;
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
          "create table if not exists hkex (id varchar(100) primary key, date timestamp with time zone, code varchar(100), name varchar(100), shareholding bigint, diff_shareholding bigint, percent real, diff_percent real)");
      h.execute(
          "create index if not exists diff_idx on hkex (date, code)");
    }
  }

  public void clear() {
    try (Handle h = this.dbi.open()) {
      h.execute("truncate hkex");
    }
  }

  public void saveShareholdings(List<StockShareholding> stockShareholdings) {
    try (Handle h = this.dbi.open()) {
      for (StockShareholding stockShareholding : stockShareholdings) {
        // calculate difference
        long diffShareholding = 0;
        float diffPercent = 0;
        if (!DateFormatUtils.format(stockShareholding.getDate(), "yyyyMMdd").equals("20170317")) {
          Map<String, Object> r = h.createQuery(
              "select shareholding, percent from hkex where code = :code and date < :date order by date desc")
              .bind("date", stockShareholding.getDate())
              .bind("code", stockShareholding.getCode())
              .setFetchSize(1)
              .first();
          diffShareholding =
              r != null && !r.isEmpty() ? stockShareholding.getShareholding() - (long) r
                  .get("shareholding") : stockShareholding.getShareholding();
          diffPercent = r != null && !r.isEmpty() ? stockShareholding.getPercent() - (float) r
              .get("percent") : stockShareholding.getPercent();
        }

        // insert data
        String id = DateFormatUtils.format(stockShareholding.getDate(), "yyyyMMdd")
            + "-" + stockShareholding.getCode();
        h.createStatement(
            "insert into hkex(id, date, code, name, shareholding, diff_shareholding, percent, diff_percent) values (:id, :date, :code, :name, :shareholding, :diff_shareholding, :percent, :diff_percent)")
            .bind("id", id)
            .bind("date", stockShareholding.getDate())
            .bind("code", stockShareholding.getCode())
            .bind("name", stockShareholding.getName())
            .bind("shareholding", stockShareholding.getShareholding())
            .bind("diff_shareholding", diffShareholding)
            .bind("diff_percent", diffPercent)
            .bind("percent", stockShareholding.getPercent())
            .execute();
      }
    }
  }
}
