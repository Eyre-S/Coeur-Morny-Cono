package cc.sukazyo.cono.morny.data.ip186;

/**
 * {@link IP186QueryHandler} 的请求结果数据的通用封装类.
 *
 * @since 0.4.2.10
 * @param url 请求数据的<u>人类可读的</u>来源链接，<b>并非api链接</b>
 * @param body API 传回的数据内容
 */
public record IP186QueryResponse(String url, String body) {
}
