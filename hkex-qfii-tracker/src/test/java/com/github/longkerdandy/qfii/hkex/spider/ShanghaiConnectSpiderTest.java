package com.github.longkerdandy.qfii.hkex.spider;

import com.github.longkerdandy.qfii.hkex.model.StockShareholding;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

/**
 * Unit Test
 */
public class ShanghaiConnectSpiderTest {

  @Test
  public void fetchTest() throws IOException, ParseException {
    ShanghaiConnectSpider spider = new ShanghaiConnectSpider(10000);
    List<StockShareholding> shanghaiStockShareholdings = spider.fetch(DateUtils.parseDate("2017/03/17", "yyyy/MM/dd"));
    for (StockShareholding stockShareholding : shanghaiStockShareholdings) {
      System.out.println(stockShareholding);
    }
  }
}
