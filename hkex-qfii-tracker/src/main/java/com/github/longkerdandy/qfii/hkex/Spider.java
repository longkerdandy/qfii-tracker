package com.github.longkerdandy.qfii.hkex;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HKEX News Web Page Spider Application
 */
public class Spider {

  private static final Logger logger = LoggerFactory.getLogger(Spider.class);

  private final String directory; // base directory for saved pages

  public Spider(String directory) {
    if (!directory.endsWith("/")) {
      directory = directory + "/";
    }
    this.directory = directory;
  }

  public static void main(String[] args) throws Exception {
    // create the parser
    CommandLineParser parser = new DefaultParser();
    CommandLine line = parser.parse(getOptions(), args);

    // download
    if (line.hasOption("download")) {

      // files directory
      String directory = line.getOptionValue("directory", "hkex/");

      // parse date range
      Date startDate = DateUtils.parseDate(line.getOptionValue("start"), "yyyy/MM/dd");
      Date endDate = DateUtils.parseDate(line.getOptionValue("end"), "yyyy/MM/dd");

      // create spider instance
      Spider spider = new Spider(directory);

      // target market
      String market = line.getOptionValue("market", "all");
      switch (market) {
        case "all":
          spider.downloadRangeForAll(startDate, endDate);
          break;
        case "shanghai":
          spider.downloadRangeForShanghai(startDate, endDate);
          break;
        case "shenzhen":
          spider.downloadRangeForShenZhen(startDate, endDate);
          break;
        case "hongkong":
          spider.downloadRangeForHongKong(startDate, endDate);
          break;
        default:
          throw new IllegalArgumentException(
              "Unexpected value for line option 'market': " + market);
      }
    }
  }

  private static Options getOptions() {
    Option update = new Option("download",
        "Download query result from HKEX News web site");
    Option directory = Option.builder("directory").argName("directory")
        .desc("Downloaded HKEX files root directory")
        .hasArg().build();
    Option market = Option.builder("market").argName("market")
        .desc("Target market could be all, shanghai, shenzhen and hongkong")
        .hasArg().build();
    Option start = Option.builder("start").argName("start")
        .desc("Start date in the format of 2017/03/17")
        .hasArg().build();
    Option end = Option.builder("end").argName("end").desc("End date in the format of 2017/03/17")
        .hasArg().build();

    Options options = new Options();
    options.addOption(update);
    options.addOption(directory);
    options.addOption(market);
    options.addOption(start);
    options.addOption(end);

    return options;
  }

  /**
   * Download pages for all market with the specific date range
   *
   * @param startDate Start Date (include)
   * @param endDate End Date (include)
   */
  public void downloadRangeForAll(Date startDate, Date endDate) throws IOException {
    downloadRangeForShanghai(startDate, endDate);
    downloadRangeForShenZhen(startDate, endDate);
    downloadRangeForHongKong(startDate, endDate);
  }

  /**
   * Download pages for Shanghai market with the specific date range
   *
   * @param startDate Start Date (include)
   * @param endDate End Date (include)
   */
  public void downloadRangeForShanghai(Date startDate, Date endDate) throws IOException {
    downloadRange(this.directory + "shanghai/", startDate, endDate,
        "http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/mutualmarket_c.aspx?t=sh");
  }

  /**
   * Download pages for Shenzhen market with the specific date range
   *
   * @param startDate Start Date (include)
   * @param endDate End Date (include)
   */
  public void downloadRangeForShenZhen(Date startDate, Date endDate) throws IOException {
    downloadRange(this.directory + "shenzhen/", startDate, endDate,
        "http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/mutualmarket_c.aspx?t=sz");
  }

  /**
   * Download pages for Hongkong market with the specific date range
   *
   * @param startDate Start Date (include)
   * @param endDate End Date (include)
   */
  public void downloadRangeForHongKong(Date startDate, Date endDate) throws IOException {
    downloadRange(this.directory + "hongkong/", startDate, endDate,
        "http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/mutualmarket_c.aspx?t=hk");
  }

  /**
   * Download pages from url with the specific date range
   *
   * @param directory Directory
   * @param startDate Start Date (include)
   * @param endDate End Date (include)
   */
  protected void downloadRange(String directory, Date startDate, Date endDate, String url)
      throws IOException {
    while (startDate.before(endDate) || startDate.equals(endDate)) {
      download(directory, startDate, url);
      startDate = DateUtils.addDays(startDate, 1);
    }
  }

  /**
   * Download pages from url with the specific date, and save to the directory
   *
   * @param directory Directory
   * @param queryDate Query Date
   * @param url Query URL
   */
  protected void download(String directory, Date queryDate, String url) throws IOException {
    try (final WebClient webClient = getWebClient()) {
      // Get the query page
      final HtmlPage queryPage = webClient.getPage(url);

      // Get the date select list elements and the search button
      final HtmlSelect year = queryPage.getElementByName("ddlShareholdingYear");
      final HtmlSelect month = queryPage.getElementByName("ddlShareholdingMonth");
      final HtmlSelect day = queryPage.getElementByName("ddlShareholdingDay");
      final HtmlImageInput button = queryPage.getElementByName("btnSearch");

      // Change the value of the date select list
      year.setSelectedAttribute(DateFormatUtils.format(queryDate, "yyyy"), true);
      month.setSelectedAttribute(DateFormatUtils.format(queryDate, "MM"), true);
      day.setSelectedAttribute(DateFormatUtils.format(queryDate, "dd"), true);

      // Now submit the query by clicking the button and get back the result page
      final HtmlPage resultPage = (HtmlPage) button.click();
      File outputFile = new File(directory,
          DateFormatUtils.format(queryDate, "yyyyMMdd") + ".html");
      FileUtils.writeStringToFile(outputFile, resultPage.asXml(), "UTF-8");

      logger.info("Successfully downloaded HKEX web page for market {} and date {}",
          url.substring(url.length() - 2), DateFormatUtils.format(queryDate, "yyyy/MM/dd"));
    }
  }

  protected WebClient getWebClient() {
    final WebClient webClient = new WebClient();
    webClient.getOptions().setJavaScriptEnabled(true);
    webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    webClient.getOptions().setTimeout(10000);
    return webClient;
  }
}
