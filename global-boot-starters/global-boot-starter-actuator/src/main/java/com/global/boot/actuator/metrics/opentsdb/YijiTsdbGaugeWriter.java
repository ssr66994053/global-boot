/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * daidai@yiji.com 2016-04-07 16:38 创建
 *
 */
package com.yiji.boot.actuator.metrics.opentsdb;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Splitter;
import com.yiji.framework.hera.client.exception.HeraException;
import com.yiji.framework.hera.client.listener.Event;
import com.yiji.framework.hera.client.listener.ValueTrigger;
import com.yiji.framework.hera.client.support.annotation.AutoConfig;
import com.yjf.common.lang.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.opentsdb.OpenTsdbData;
import org.springframework.boot.actuate.metrics.opentsdb.OpenTsdbNamingStrategy;
import org.springframework.boot.actuate.metrics.writer.GaugeWriter;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPOutputStream;

/**
 * @author daidai@yiji.com
 */
public class YijiTsdbGaugeWriter implements GaugeWriter, DisposableBean, ValueTrigger {
	private static Logger logger = LoggerFactory.getLogger(YijiTsdbGaugeWriter.class);
	
	private static final String DEBUG_OPTION_SUFFIX = "details";
	
	private volatile String[] tsdURLs;
	private volatile String[] tsdURLsDebug;
	private AtomicLong visitIdx = new AtomicLong(0);
	private int flushingIntervalInMilliSecs = 60000;
	private int flushingBatchSize = 60;
	private int compressionBatchSizeThreshold = 8;
	
	@AutoConfig("yiji.metrics.opentsdb.debug")
	private volatile boolean debug = false;
	
	@AutoConfig("yiji.metrics.opentsdb.enable")
	private volatile boolean enable = false;
	
	private OpenTsdbNamingStrategy namingStrategy = new AttachAppAndHostNamingStrategy(new YijiTsdbNamingStrategy());
	private ExecutorService executorService;
	private TsdbBatchData batchData = new TsdbBatchData(flushingBatchSize);
	
	public YijiTsdbGaugeWriter() {
		this(null);
	}
	
	public YijiTsdbGaugeWriter(String[] tsdURLs) {
		this.setUrls(tsdURLs);
		this.executorService = Executors.newSingleThreadExecutor(new CustomizableThreadFactory("tsdbWriterThread"));
	}
	
	public void setUrls(String[] tsdURLs) {
		if (tsdURLs == null || tsdURLs.length == 0) {
			return;
		}
		this.tsdURLs = tsdURLs;
		this.tsdURLsDebug = new String[this.tsdURLs.length];
		for (int i = 0; i < this.tsdURLs.length; i++) {
			this.tsdURLsDebug[i] = this.tsdURLs[i] + "?" + DEBUG_OPTION_SUFFIX;
		}
	}
	
	@Override
	public void destroy() throws Exception {
		this.executorService.shutdownNow();
	}
	
	public boolean isDebug() {
		return debug;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	@Override
	public void set(Metric<?> value) {
		if (enable && this.tsdURLs != null) {
			batchData.add(new OpenTsdbData(namingStrategy.getName(value.getName()), value.getValue(), value
				.getTimestamp().getTime() / 1000));
			if (batchData.isFull() || batchData.getAge() > flushingIntervalInMilliSecs) {
				TsdbBatchData tmpBatchData = this.batchData;
				this.batchData = new TsdbBatchData(flushingBatchSize);
				executorService.submit(createBatchSendTask(tmpBatchData));
			}
		}
	}
	
	private Runnable createBatchSendTask(TsdbBatchData tmpBatchData) {
		return () -> {
			boolean debugEabled = this.debug;
			String[] realUrl;
			if (debugEabled) {
				realUrl = tsdURLsDebug;
			} else {
				realUrl = tsdURLs;
			}
			int idx = (int) (visitIdx.getAndIncrement() % tsdURLs.length);
			try {
				HttpRequest request = HttpRequest.post(realUrl[idx]).accept("application/json").connectTimeout(10000)
					.readTimeout(10000);
				String data = JSON.toJSONString(tmpBatchData.getDataList());
				// fixme: add data compress
				if (tmpBatchData.size() > compressionBatchSizeThreshold) {
					request.header("Content-Encoding", "gzip");
					request.send(gzip(data.getBytes("UTF-8")));
				} else {
					request.send(data);
				}
				int code = request.code();
				if (code == 204 || code == 200) {
					logger.trace("send Opentsdb data successfully!");
				} else if (code == 400) {
					if (debugEabled) {
						try {
							final JSONObject parse = JSON.parseObject(request.body());
							JSONObject error = parse.getJSONObject("error");
							if (error != null) {
								logger.warn("send Opentsdb data failed:{}", error.get("message"));
							} else {
								final Integer failed = parse.getInteger("failed");
								final Integer success = parse.getInteger("success");
								final Object errors = parse.get("errors");
								logger.warn("send Opentsdb data failed number: {}, success: {}, errors: {}", success,
									failed, errors);
							}
						} catch (Exception e) {
							logger.warn("send Opentsdb data failed and parse details exception!", e);
						}
					} else {
						logger.warn("send Opentsdb data failed, enable debug to see more details!");
					}
				} else {
					logger.warn("unsupported status code {}!", code);
				}
			} catch (Exception e) {
				logger.warn("send Opentsdb data failed,ex={}", e.getMessage());
			}
			
		};
	}
	
	private byte[] gzip(byte[] input) {
		GZIPOutputStream gzipOS = null;
		try {
			ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
			gzipOS = new GZIPOutputStream(byteArrayOS);
			gzipOS.write(input);
			gzipOS.flush();
			gzipOS.close();
			gzipOS = null;
			return byteArrayOS.toByteArray();
		} catch (Exception e) {
			throw Exceptions.newRuntimeException(e);
		} finally {
			if (gzipOS != null) {
				try {
					gzipOS.close();
				} catch (Exception ignored) {
				}
			}
		}
	}
	
	@Override
	public void onChange(Event event) throws HeraException {
		event.ifPresent("yiji.metrics.opentsdb.urls",value -> {
			this.setUrls(Splitter.on(",").omitEmptyStrings().splitToList(value.toString()).toArray(new String[0]));
			logger.info("hera修改opentsdb urls:{}", value);
		});
	}
}
