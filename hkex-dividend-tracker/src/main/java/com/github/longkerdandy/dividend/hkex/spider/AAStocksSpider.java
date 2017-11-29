package com.github.longkerdandy.dividend.hkex.spider;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AAStocks News Web Page Spider Application
 */
public class AAStocksSpider {

  private static final Logger logger = LoggerFactory.getLogger(AAStocksSpider.class);

  private final String directory; // base directory for saved pages

  public AAStocksSpider(String directory) {
    if (!directory.endsWith("/")) {
      directory = directory + "/";
    }
    this.directory = directory;
  }

  public void download(List<String> symbols) {
    for (String symbol : symbols) {
      try {
        download(symbol);
      } catch (IOException ignore) {
        logger.warn("Failed to download AAStocks dividend data for symbol (code) {}", symbol);
      }
    }
  }

  public void download(String symbol) throws IOException {
    try (final WebClient webClient = getWebClient()) {
      // url
      final String url =
          "http://www.aastocks.com/sc/stocks/analysis/dividend.aspx?symbol=" + symbol;

      // Get the query page
      final HtmlPage page = webClient.getPage(url);

      // Save page
      File outputFile = new File(this.directory, symbol + ".html");
      Files.deleteIfExists(outputFile.toPath());
      FileUtils.writeStringToFile(outputFile, page.asXml(), "UTF-8");

      logger.info("Successfully downloaded AAStocks dividend page for symbol {}", symbol);
    }

  }

  protected WebClient getWebClient() {
    final WebClient webClient = new WebClient();
    webClient.getOptions().setJavaScriptEnabled(false);
    webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    webClient.getOptions().setTimeout(10000);
    return webClient;
  }
}
