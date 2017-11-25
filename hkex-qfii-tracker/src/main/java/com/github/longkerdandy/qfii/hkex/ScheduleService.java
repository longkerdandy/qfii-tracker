package com.github.longkerdandy.qfii.hkex;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import com.github.longkerdandy.qfii.hkex.quartz.TrackerJob;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Schedule Service Application
 */
public class ScheduleService {

  public static void main(String[] args) throws Exception {
    // create the parser
    CommandLineParser parser = new DefaultParser();
    CommandLine line = parser.parse(getOptions(), args);

    // files directory
    String directory = line.getOptionValue("directory", "hkex/");

    // config file
    String config = line.getOptionValue("config", "config/tracker.properties");

    // init scheduler
    SchedulerFactory sf = new StdSchedulerFactory();
    Scheduler scheduler = sf.getScheduler();

    // define job instance
    JobDetail job = newJob(TrackerJob.class)
        .withIdentity("job", "group")
        .usingJobData("directory", directory)
        .usingJobData("config", config)
        .build();

    // define trigger instance
    Trigger trigger = newTrigger()
        .withIdentity("trigger", "group")
        .startNow()
        .withSchedule(dailyAtHourAndMinute(8, 30)) // fire every day at 8:30
        .build();

    // schedule the job with the trigger
    scheduler.scheduleJob(job, trigger);

    // start scheduler
    scheduler.start();
  }

  private static Options getOptions() {
    Option directory = Option.builder("directory").argName("directory")
        .desc("Downloaded HKEX files root directory")
        .hasArg().build();
    Option config = Option.builder("config").argName("config").desc("Configuration file")
        .hasArg().build();

    Options options = new Options();
    options.addOption(directory);
    options.addOption(config);

    return options;
  }
}
