package com.github.longkerdandy.qfii.hkex.parser;

import com.github.longkerdandy.qfii.hkex.storage.PostgreStorage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class HongkongConnectParser extends ConnectParser {

  private final String directory;
  private final PostgreStorage storage;

  public HongkongConnectParser(String directory, PostgreStorage storage) {
    this.directory = directory + "hongkong/";
    this.storage = storage;
  }

  public void parseRangeAndUpdate(Date startDate, Date endDate) throws IOException, ParseException {
    parseRangeAndUpdate(startDate, endDate, this.directory, this.storage, "hongkong");
  }

  @Override
  public String adjustCode(String code) {
    StringBuilder builder = new StringBuilder(code);
    while (builder.length() < 5) {
      builder.insert(0, "0");
    }
    return builder.toString();
  }

  @Override
  public String adjustName(String name) {
    name = StringUtils.trim(name);
    switch (name) {
      case "VTECH HOLDINGS":
        return "伟易达";
      case "VITASOY INT'L":
        return "维他奶国际";
      case "ASM PACIFIC":
        return "ASM 太平洋";
      case "IGG INC":
        return "IGG";
      case "BRILLIANCE CHI":
        return "华晨中国";
      case "IMAX CHINA HOLDING, INC.":
        return "IMAX 中国";
      case "CHINA HANKING HOLDINGS LIMITED":
        return "中国罕王";
      case "HUATAI SECURITIES CO., LTD.":
        return "华泰证券";
      default:
        return name;
    }
  }
}
