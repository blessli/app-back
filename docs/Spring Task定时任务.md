```java
/**
 * 基于注解的定时器
 */
@Component
public class AdvanceNotice {
    /**
     * 启动时执行一次，之后每隔一个小时秒执行一次
     * 向相关用户发送短信
     */
    @Scheduled(fixedRate = 1000*3600) //从上一个任务开始到下一个任务开始的间隔，单位毫秒
    public void send() {
        System.out.println("print method 2");
    }
}
/**
* 在程序入口启动类添加@EnableScheduling，开启定时任务功能
*/
@SpringBootApplication
@EnableScheduling
@EnableEurekaClient
public class CacheServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheServiceApplication.class, args);
    }

}
```
# @Scheduled(fixedRate)保证固定速度执行
为了保证fixedRate任务真的可以按照设置的速度执行，无疑需要引入异步执行模式，确保schedule调度的任务不会被***单线程***执行阻塞。
这里引入注解@EnableAsync和@Async。
- 遇到的问题

@Async使用的是SimpleAsyncTaskExecutor，每次请求新开线程，没有最大线程数设置.不是真的线程池，这个类不重用线程，每次调用都会创建一个新的线程
- 解决方法

使用自定义线程池，设置好对应的参数

Spring 定时任务执行原理实际使用的是 JDK 自带的 ScheduledExecutorService
Spring 默认配置下，将会使用具有单线程的 ScheduledExecutorService
单线程执行定时任务，如果某一个定时任务执行时间较长，将会影响其他定时任务执行
如果存在多个定时任务，为了保证定时任务执行时间的准确性，可以修改默认配置，使其使用多线程执行定时任务