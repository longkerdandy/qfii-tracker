package com.github.longkerdandy.qfii.hkex.quartz;

import com.github.longkerdandy.qfii.hkex.Spider;
import com.github.longkerdandy.qfii.hkex.Tracker;
import com.github.longkerdandy.qfii.hkex.storage.PostgreStorage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracker Job for Quartz Scheduler
 */
public class TrackerJob implements Job {

  private static final Logger logger = LoggerFactory.getLogger(TrackerJob.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    // load data
    JobDataMap data = context.getMergedJobDataMap();
    String directory = data.getString("directory");
    String config = data.getString("config");
    Date date = DateUtils.addDays(new Date(), -1);

    // download web pages
    try {
      // create spider instance
      Spider spider = new Spider(directory);

      // download for all market
      spider.downloadRangeForAll(date, date);
    } catch (IOException e) {
      logger.warn("Error when download daily {} HKEX web pages.",
          DateFormatUtils.format(date, "yyyy/MM/dd"));
      return;
    }

    // parse downloaded pages
    try {
      // config file
      PropertiesConfiguration properties = new PropertiesConfiguration(config);

      // storage
      PostgreStorage storage = new PostgreStorage(properties.getString("storage.url"),
          properties.getString("storage.username"), properties.getString("storage.password"));

      // create tracker instance
      Tracker tracker = new Tracker(directory, storage);

      // parse and update downloaded stock data
      tracker.parseRangeForAll(date, date);
    } catch (ConfigurationException e) {
      logger.warn("Error when loading configuration file {}.", config);
    } catch (ParseException | IOException e) {
      logger.warn("Error when parsing daily downloaded {} HKEX web pages.",
          DateFormatUtils.format(date, "yyyy/MM/dd"));
    }
  }
}
