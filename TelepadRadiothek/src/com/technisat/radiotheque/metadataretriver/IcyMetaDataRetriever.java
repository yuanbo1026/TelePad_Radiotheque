package com.technisat.radiotheque.metadataretriver;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IcyMetaDataRetriever {

	protected URL streamUrl;
	private Map<String, String> metadata;
	private boolean isError;
	private final String TAG = "NEXXOO";

	public IcyMetaDataRetriever(URL streamUrl) {
		setStreamUrl(streamUrl);

		isError = false;
	}

	/**
	 * Get artist using stream's title
	 *
	 * @return String
	 * @throws IOException
	 */
	public String getArtist() throws IOException {
		Map<String, String> data = getMetadata();

		if (data == null || !data.containsKey("StreamTitle")) return "";

		String streamTitle = data.get("StreamTitle");
		String title = streamTitle.substring(0, streamTitle.indexOf("-"));
		return title.trim();
	}

	/**
	 * Get title using stream's title
	 *
	 * @return String
	 * @throws IOException
	 */
	public String getTitle() throws IOException {
		Map<String, String> data = getMetadata();

		if (data == null || !data.containsKey("StreamTitle")) return "";

		String streamTitle = data.get("StreamTitle");
		String artist = streamTitle.substring(streamTitle.indexOf("-") + 1);
		return artist.trim();
	}

	public Map<String, String> getMetadata() throws IOException {
		if (metadata == null) {
			refreshMeta();
//			RetriveStationMedadata();
//			getMedadataFromUrl();
		}

		return metadata;
	}
	private void getMedadataFromUrl() throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(streamUrl.openStream(), StandardCharsets.UTF_8));
		LinkedList<String> lines = new LinkedList();
		String readLine;

		while ((readLine = in.readLine()) != null) {
			lines.add(readLine);
			Log.d(TAG, "" + readLine);
		}
	}

	private void RetriveStationMedadata() {
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		try {
			Map<String,String> mMap= new HashMap<String,String>();
			mMap.put("Icy-MetaData", "1");
			mMap.put("Connection", "close");

			mmr.setDataSource(streamUrl.toString(),mMap);
			String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE); // api level 10, 即从GB2.3.3开始有此功能
			Log.d(TAG, "title:" + title);
			String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			Log.d(TAG, "artist:" + artist);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void refreshMeta() throws IOException {
		retreiveMetadata();
	}

	private void retreiveMetadata() throws IOException {
		URLConnection con = streamUrl.openConnection();
		con.setRequestProperty("Icy-MetaData", "1");
		con.setRequestProperty("Connection", "close");
		con.setRequestProperty("Accept", null);
		con.setConnectTimeout(1500);
		con.connect();

		int metaDataOffset = 0;
		Map<String, List<String>> headers = con.getHeaderFields();
		InputStream stream = con.getInputStream();

		try {

			if (headers.containsKey("icy-metaint")) {
				// Headers are sent via HTTP
				metaDataOffset = Integer.parseInt(headers.get("icy-metaint").get(0));
			} else {
				// Headers are sent within a stream
				StringBuilder strHeaders = new StringBuilder();
				char c;
				while ((c = (char) stream.read()) != -1) {
					strHeaders.append(c);
					if (strHeaders.length() > 5) {// && (strHeaders.substring((strHeaders.length() - 4), strHeaders.length()).equals("\r\n\r\n"))) {
						// end of headers
						break;
					}
				}

				// Match headers to get metadata offset within a stream
				Pattern p = Pattern.compile("\\r\\n(icy-metaint):\\s*(.*)\\r\\n");
				Matcher m = p.matcher(strHeaders.toString());
				if (m.find()) {
					metaDataOffset = Integer.parseInt(m.group(2));
				}
			}

			// In case no data was sent
			if (metaDataOffset == 0) {
				isError = true;
				return;
			}

			// Read metadata
			int b;
			int count = 0;
			int metaDataLength = 4080; // 4080 is the max length
			boolean inData = false;
			StringBuilder metaData = new StringBuilder();
			// Stream position should be either at the beginning or right after headers
			while ((b = stream.read()) != -1) {
				count++;

				// Length of the metadata
				if (count == metaDataOffset + 1) {
					metaDataLength = b * 16;
				}

				if (count > metaDataOffset + 1 && count < (metaDataOffset + metaDataLength)) {
					inData = true;
				} else {
					inData = false;
				}
				if (inData) {
					if (b != 0) {
						char non = (char) b;
						byte a = (byte) b;
						Log.d("NEXXOO", "Integer : " + b + " Char : " + non + " Byte :" + a);
						metaData.append((char) b);
//						metaData.append(Integer.toString(b).getBytes(StandardCharsets.UTF_8));
					}
				}
				if (count > (metaDataOffset + metaDataLength)) {
					break;
				}

			}


			// Set the data
			metadata = IcyMetaDataRetriever.parseMetadata(metaData.toString());
		} finally {
			// Close
//			Log.d("Nexxoo", "stream closed");
			stream.close();
		}
	}

	public boolean isError() {
		return isError;
	}

	public URL getStreamUrl() {
		return streamUrl;
	}

	public void setStreamUrl(URL streamUrl) {
		this.metadata = null;
		this.streamUrl = streamUrl;
		this.isError = false;
	}

	public static Map<String, String> parseMetadata(String metaString) {
		metaString = new String(metaString.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		Map<String, String> metadata = new HashMap<String, String>();
		String[] metaParts = metaString.split(";");
		Pattern p = Pattern.compile("^([a-zA-Z]+)=\\'([^\\']*)\\'$");
		Matcher m;
		for (int i = 0; i < metaParts.length; i++) {
			m = p.matcher(metaParts[i]);
			if (m.find()) {
				metadata.put((String) m.group(1), (String) m.group(2));
			}
		}

		return metadata;
	}

}
