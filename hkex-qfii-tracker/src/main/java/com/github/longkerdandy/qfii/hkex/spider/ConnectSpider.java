package com.github.longkerdandy.qfii.hkex.spider;

import com.github.longkerdandy.qfii.hkex.model.StockShareholding;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Base Spider Class
 */
public abstract class ConnectSpider {

  public List<StockShareholding> fetch(Date queryDate, String url, int timeout) throws IOException, ParseException {
    // fetch html from HKEX web site
    Document doc = Jsoup.connect(url)
        .data("ddlShareholdingYear", DateFormatUtils.format(queryDate, "yyyy"))
        .data("ddlShareholdingMonth", DateFormatUtils.format(queryDate, "MM"))
        .data("ddlShareholdingDay", DateFormatUtils.format(queryDate, "dd"))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0")
        .cookie("kanhanBase", "sc.hkexnews.hk/TuniS/,www.hkexnews.hk")
        .timeout(timeout)
        .post();

    // parse actual query date
    Date actualDate = null;
    Elements divs = doc.select("div#pnlResult > div");
    for (Element div : divs) {
      if (div.text().startsWith("持股日期")) {
        actualDate = DateUtils
            .parseDate(div.text().substring(div.text().length() - 9), "dd/MM/yyyy");
        break;
      }
    }
    if (actualDate == null) {
      throw new IllegalStateException("Date not present in the query result");
    }

    // result
    List<StockShareholding> stockShareholdings = new ArrayList<>();

    // parse shareholding data
    Elements rows = doc.select("tr.row0,tr.row1");
    for (Element row : rows) {
      StockShareholding stockShareholding = new StockShareholding();
      stockShareholding.setDate(actualDate);
      stockShareholding.setCode(adjustCode(row.child(0).ownText()));
      stockShareholding.setName(adjustName(row.child(1).ownText()));
      stockShareholding.setShareholding(NumberUtils.toLong(row.child(2).ownText().replaceAll(",", "")));
      stockShareholding.setPercent(NumberUtils.toFloat(row.child(3).ownText().replaceAll("%", "")) / 100);
      stockShareholdings.add(stockShareholding);
    }

    return stockShareholdings;
  }

  public abstract String adjustCode(String code);

  public abstract String adjustName(String name);
}
