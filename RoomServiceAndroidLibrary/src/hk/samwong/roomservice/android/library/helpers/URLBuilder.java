package hk.samwong.roomservice.android.library.helpers;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;


public class URLBuilder {
	public static URL build(String servletUrl, List<NameValuePair> nvps) throws UnsupportedEncodingException, MalformedURLException{
		StringBuilder url = new StringBuilder(servletUrl);
		url.append("?");
		for (int i = 0; i < nvps.size(); i++) {
			NameValuePair nvp = nvps.get(i);
			url.append(URLEncoder.encode(nvp.getName(), "UTF-8"));
			url.append("=");
			url.append(URLEncoder.encode(nvp.getValue(), "UTF-8"));
			if(i != nvps.size()-1){
				url.append("&");
			}
		}
		return new URL(url.toString());
	}
}
