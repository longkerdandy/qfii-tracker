package com.github.longkerdandy.qfii.hkex;

import com.github.longkerdandy.qfii.hkex.spider.ShanghaiConnectSpider;
import com.github.longkerdandy.qfii.hkex.spider.ShenzhenConnectSpider;
import com.github.longkerdandy.qfii.hkex.storage.InfluxDBStorage;
import java.util.Date;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Stock Tracker Application
 */
public class Tracker {

  public static void main(String[] args) throws Exception {
    // create the parser
    CommandLineParser parser = new DefaultParser();
    CommandLine line = parser.parse(getOptions(), args);

    // config file
    PropertiesConfiguration config = new PropertiesConfiguration(
        line.getOptionValue("config", "config/tracker.properties"));

    // storage
    InfluxDBStorage storage = new InfluxDBStorage(config.getString("influxDB.url"),
        config.getString("influxDB.username"), config.getString("influxDB.password"));

    // update
    if (line.hasOption("update")) {

      // spider
      ShanghaiConnectSpider shSpider = new ShanghaiConnectSpider(config.getInt("spider.timeout"),
          storage);
      ShenzhenConnectSpider szSpider = new ShenzhenConnectSpider(config.getInt("spider.timeout"),
          storage);

      // fetch date range and update storage
      Date startDate = DateUtils.parseDate(line.getOptionValue("start"), "yyyy/MM/dd");
      Date endDate = DateUtils.parseDate(line.getOptionValue("end"), "yyyy/MM/dd");
      shSpider.fetchRangeAndUpdate(startDate, endDate);
      szSpider.fetchRangeAndUpdate(startDate, endDate);
    }
  }

  private static Options getOptions() {
    Option refresh = new Option("update", "Update stocks shareholding data from HKEX web site");
    Option start = Option.builder("start").argName("start")
        .desc("Start date in the format of 2017/03/17")
        .hasArg().build();
    Option end = Option.builder("end").argName("end").desc("End date in the format of 2017/03/17")
        .hasArg().build();
    Option config = Option.builder("config").argName("config").desc("Configuration file")
        .hasArg().build();

    Options options = new Options();
    options.addOption(refresh);
    options.addOption(start);
    options.addOption(end);
    options.addOption(config);

    return options;
  }
}
