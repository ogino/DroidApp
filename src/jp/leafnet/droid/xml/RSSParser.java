package jp.leafnet.droid.xml;

import java.io.IOException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jp.leafnet.droid.xml.handler.RSSHandler;
import jp.leafnet.droid.xml.rss.Channel;

import org.xml.sax.SAXException;

/**
 * RSSParser
 * @author ogino
 * RSS2.0のパーサ. RSS1.0には対応していない.
 */
public class RSSParser {

	private RSSHandler handler;
	public Channel createChannel(final String url) throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		this.handler = new RSSHandler();
		parser.parse(url, this.handler);
		return this.handler.getChannel();
	}

}
