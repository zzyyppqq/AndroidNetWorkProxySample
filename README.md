BrowserMob Proxy 是一个基于 Java 的代理服务器，可以用来捕获和分析浏览器的网络流量。它提供了一个 REST API，可以通过编程方式控制代理服务器和报告内部状态、收集流量数据、自动生成 HAR 文件等等。

BrowserMob Proxy 可以用于以下方面：

网络性能测试：通过控制网络流量，模拟现实世界的网络环境并度量网页性能指标，例如页面加载时间、响应时间、错误率等等。
评估网站质量：通过捕获和分析网络流量，评估网站的质量、安全性和易用性，查找潜在的性能问题或安全漏洞。
爬虫开发：通过拦截和修改网络请求和响应，使用代理服务器改进爬虫的效率和精度。
测试自动化：通过编程方式控制代理服务器，使其与测试框架和工具进行集成，自动生成测试报告和 HAR 文件，减少测试工作量。
BrowserMob Proxy 还提供了多种自定义选项，例如可以自定义代理服务器的端口、日志级别、代理行为、证书等等。

总之，BrowserMob Proxy 是一个功能强大的代理服务器，可以帮助您捕获、修改和分析浏览器的网络流量，并将其用于各种网络性能测试、评估和爬虫开发场景。

在Android上使用BrowserMob-Proxy进行抓包可以通过以下步骤实现：

添加依赖和权限：
在你的Android项目中的build.gradle文件中添加BrowserMob-Proxy的Maven依赖。例如：

gradle
dependencies {
    implementation 'net.lightbody.bmp:browsermob-core:2.1.5'
}
并确保在AndroidManifest.xml文件中添加网络权限：

xml
<uses-permission android:name="android.permission.INTERNET" />
初始化BrowserMob-Proxy：
在你的代码中，创建一个BrowserMobProxyServer实例并启动它。例如：

java
int port = 8888; // 代理服务器的端口号
BrowserMobProxy proxy = new BrowserMobProxyServer();
proxy.start(port);
配置WebView使用代理：
如果你要使用WebView进行网络请求，需要配置WebView使用BrowserMob-Proxy作为代理服务器。例如：

java
WebView webView = findViewById(R.id.webView);
String proxyHost = "localhost"; // 代理服务器的主机名
int proxyPort = 8888; // 代理服务器的端口号
String proxyUrl = "http://" + proxyHost + ":" + proxyPort;
webView.getSettings().setProxyEnabled(true);
webView.getSettings().setProxyHost(proxyHost);
webView.getSettings().setProxyPort(proxyPort);
webView.loadUrl(proxyUrl);
添加请求拦截器：
为了捕获和分析网络请求和响应，你可以添加一个请求拦截器来处理每个网络请求。例如：

java
proxy.addRequestInterceptor((request, contents, messageInfo) -> {
    // 在这里处理请求，可以修改请求参数、添加头部等操作
});

proxy.addResponseInterceptor((response, contents, messageInfo) -> {
    // 在这里处理响应，可以修改响应内容、添加头部等操作
});
获取抓包数据：
你可以使用BrowserMob-Proxy提供的API来获取捕获到的请求和响应数据。例如：

java
// 获取所有的HarEntry对象（请求和响应对）
Har har = proxy.getHar();
List<HarEntry> entries = har.getLog().getEntries();

// 遍历HarEntry对象，获取请求和响应信息
for (HarEntry entry : entries) {
    HarRequest request = entry.getRequest();
    HarResponse response = entry.getResponse();

    // 分别获取请求和响应的URL、HTTP方法、状态码等信息
    String url = request.getUrl();
    String method = request.getMethod();
    int statusCode = response.getStatus();

    // 进一步获取请求和响应的头部、内容等信息
    HttpHeaders requestHeaders = request.getHeaders();
    HttpHeaders responseHeaders = response.getHeaders();
    String requestBody = request.getBody().getText();
    String responseBody = response.getContent().getText();
}
需要注意的是，在使用BrowserMob-Proxy进行抓包时，你需要在Android设备上设置代理，将网络流量导向到BrowserMob-Proxy服务器。另外，记得在使用完毕后停止和释放BrowserMob-Proxy服务器资源，以避免资源泄漏。

以上是使用BrowserMob-Proxy进行抓包的基本步骤，你可以根据具体需求进一步扩展和优化。