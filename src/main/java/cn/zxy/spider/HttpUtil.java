package cn.zxy.spider;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于 httpclient 4.3.1版本的 http工具类
 *
 * @author mcSui
 */
public class HttpUtil {

    private static final Log LOG = LogFactory.getLog(HttpUtil.class);
    public static final String CHARSET = "UTF-8";

    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, params, CHARSET);
    }

    /**
     * 自定义超时时间
     *
     * @param url
     * @param params
     * @param connectTimeout 单位  ms
     * @param socketTimeout  单位  ms
     * @return
     */
    public static String doGet(String url, Map<String, String> params, int connectTimeout, int socketTimeout) {
        return doGet(url, params, CHARSET, connectTimeout, socketTimeout);
    }

    public static String doPost(String url, Map<String, String> params) {
        return doPost(url, params, CHARSET);
    }

    /**
     * HTTP Get 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doGet(String url, Map<String, String> params, String charset) {
        int connectTimeout = 9000;
        int socketTimeout = 6000;
        return doGet(url, params, charset, connectTimeout, socketTimeout);
    }

    public static String doGet(String url, Map<String, String> params, String charset, int connectTimeout, int socketTimeout) {
        LOG.info("HttpUtil doGet url=====" + url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        HttpGet httpGet = null;

        try {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

            if (params != null) {
                LOG.info("HttpUtil doGet params=====" + JSONObject.toJSON(params));
            }

            if (StringUtils.isBlank(url)) {
                return null;
            }

            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            LOG.info("HttpUtil doGet url:" + url);
            httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
            }
            return result;
        } catch (ParseException e) {
            LOG.error("doGet error ", e);
        } catch (UnsupportedEncodingException e) {
            LOG.error("doGet error ", e);
        } catch (Exception e) {
            LOG.error("doGet error ", e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (Throwable ex) {
                }
            }

            if (response != null) {
                try {
                    response.close();
                } catch (Throwable ex) {
                }
            }

            if (httpGet != null) {
                try {
                    httpGet.abort();
                } catch (Throwable ex) {
                }
            }

            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Throwable ex) {
                }
            }
        }
        return null;
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> params, String charset) {
        LOG.info("HttpUtil doPost url=====" + url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        HttpPost httpPost = null;

        try {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

            if (params != null) {
                LOG.info("HttpUtil doPost params=====" + JSONObject.toJSON(params));
            }

            if (StringUtils.isBlank(url)) {
                return null;
            }

            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }

            httpPost = new HttpPost(url);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
            }
            return result;
        } catch (ParseException e) {
            LOG.error("doGet error ", e);
        } catch (UnsupportedEncodingException e) {
            LOG.error("doGet error ", e);
        } catch (IOException e) {
            LOG.error("doGet error ", e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (Throwable ex) {
                }
            }

            if (response != null) {
                try {
                    response.close();
                } catch (Throwable ex) {
                }
            }

            if (httpPost != null) {
                try {
                    httpPost.abort();
                } catch (Throwable ex) {
                }
            }

            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Throwable ex) {
                }
            }
        }
        return null;
    }

    public static String doPostXml(String url, String content) {
        LOG.info("HttpUtil doPost url=====" + url);
        LOG.info("HttpUtil doPost content=====" + content);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        HttpPost httpPost = null;

        try {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

            httpPost = new HttpPost(url);
            httpPost.setEntity(new ByteArrayEntity(content.getBytes(CHARSET), ContentType.APPLICATION_XML));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            LOG.error("doPost UnsupportedEncodingException ", e);
        } catch (ClientProtocolException e) {
            LOG.error("doPost ClientProtocolException ", e);
        } catch (IOException e) {
            LOG.error("doPost IOException ", e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (Throwable ex) {
                }
            }

            if (response != null) {
                try {
                    response.close();
                } catch (Throwable ex) {
                }
            }

            if (httpPost != null) {
                try {
                    httpPost.abort();
                } catch (Throwable ex) {
                }
            }

            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Throwable ex) {
                }
            }
        }
        return null;
    }


    public static String doPost(String url, String content) {
        LOG.info("HttpUtil doPost url=====" + url);
        LOG.info("HttpUtil doPost content=====" + content);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        HttpPost httpPost = null;

        try {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

            httpPost = new HttpPost(url);
            httpPost.setEntity(new ByteArrayEntity(content.getBytes(CHARSET), ContentType.APPLICATION_JSON));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            LOG.error("doPost UnsupportedEncodingException ", e);
        } catch (ClientProtocolException e) {
            LOG.error("doPost ClientProtocolException ", e);
        } catch (IOException e) {
            LOG.error("doPost IOException ", e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (Throwable ex) {
                }
            }

            if (response != null) {
                try {
                    response.close();
                } catch (Throwable ex) {
                }
            }

            if (httpPost != null) {
                try {
                    httpPost.abort();
                } catch (Throwable ex) {
                }
            }

            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Throwable ex) {
                }
            }
        }
        return null;
    }

    /**
     * HTTP Get 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static byte[] getDownLoad(String url, Map<String, String> params, String charset) {
        LOG.info("HttpUtil getDownLoad url=====" + url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        HttpGet httpGet = null;

        try {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

            if (params != null) {
                LOG.info("HttpUtil getDownLoad params=====" + JSONObject.toJSON(params));
            }

            if (StringUtils.isBlank(url)) {
                return null;
            }

            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }

            httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            entity = response.getEntity();
            long length = entity.getContentLength();
            if (length == 0) return null;
            byte[] result = new byte[(int) length];
            int readInt = entity.getContent().read(result);
            if (readInt < 0) {

            }
            return result;
        } catch (Exception e) {
            LOG.error("doGet error ", e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (Throwable ex) {
                }
            }

            if (response != null) {
                try {
                    response.close();
                } catch (Throwable ex) {
                }
            }

            if (httpGet != null) {
                try {
                    httpGet.abort();
                } catch (Throwable ex) {
                }
            }

            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Throwable ex) {
                }
            }
        }
        return null;
    }


    /**
     * HTTP Post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doPost(String url, Map<String, String> params, String charset, Header[] headers) {
        LOG.info("HttpUtil doPost url=====" + url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        HttpPost httpPost = null;

        try {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

            if (params != null) {
                LOG.info("HttpUtil doPost params=====" + JSONObject.toJSON(params));
            }

            if (StringUtils.isBlank(url)) {
                return null;
            }

            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            httpPost = new HttpPost(url);
            httpPost.setHeaders(headers);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
            }
            return result;
        } catch (Exception e) {
            LOG.error("doPost error ", e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (Throwable ex) {
                }
            }

            if (response != null) {
                try {
                    response.close();
                } catch (Throwable ex) {
                }
            }

            if (httpPost != null) {
                try {
                    httpPost.abort();
                } catch (Throwable ex) {
                }
            }

            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Throwable ex) {
                }
            }
        }
        return null;
    }


    /**
     * Http Post throw new Exception
     *
     * @param url
     * @param content
     * @return
     */
    public static String doPostWithThrows(String url, String content) throws Exception {
        LOG.info("HttpUtil doPost url=====" + url);
        LOG.info("HttpUtil doPost content=====" + content);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        HttpPost httpPost = null;

        try {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

            httpPost = new HttpPost(url);
            httpPost.setEntity(new ByteArrayEntity(content.getBytes(CHARSET), ContentType.APPLICATION_JSON));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, CHARSET);
                LOG.info("HttpUtil result====" + result);
            }
            return result;
        } catch (Exception e) {
            LOG.error("doPost IOException ", e);
            throw new Exception(e);
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (Throwable ex) {
                }
            }

            if (response != null) {
                try {
                    response.close();
                } catch (Throwable ex) {
                }
            }

            if (httpPost != null) {
                try {
                    httpPost.abort();
                } catch (Throwable ex) {
                }
            }

            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Throwable ex) {
                }
            }
        }
    }

}