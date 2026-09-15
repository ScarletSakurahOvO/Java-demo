package 模拟线程池;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MyFixedThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final List<Worker> workers;
    private final int poolCapacity;
    private final int poolSize; // 保存核心线程数，用于后续重新启动
    
    // 状态标志，保证多线程可见性
    private volatile boolean isRunning = true;

    // 核心指标统计变量（线程安全）
    private final AtomicInteger activeThreads = new AtomicInteger(0);   
    private final AtomicInteger completedTasks = new AtomicInteger(0);  
    private final AtomicInteger rejectedTasks = new AtomicInteger(0);   
    private final ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>(); 

    public MyFixedThreadPool(int poolSize, int queueCapacity) {
        this.poolSize = poolSize;
        this.poolCapacity = queueCapacity;
        this.taskQueue = new LinkedBlockingQueue<>(queueCapacity);
        this.workers = new ArrayList<>(poolSize);

        // 初始化工作线程
        for (int i = 0; i < poolSize; i++) {
            Worker worker = new Worker("PoolWorker-" + (i + 1));
            worker.start();
            workers.add(worker);
        }

        // 启动内置 HTTP API 服务
        startHttpServer();
    }

    /**
     * 提交任务到线程池
     */
    public void submit(Runnable task) {
        if (isRunning) {
            if (!taskQueue.offer(task)) {
                rejectedTasks.incrementAndGet();
                addLog("[WARN] 队列已满，任务被拒绝！");
            }
        } else {
            rejectedTasks.incrementAndGet();
            // 降低被拒绝日志的打印频率，防止刷屏
            if (Math.random() < 0.1) { 
                addLog("[WARN] 线程池已关闭，拒绝新任务。");
            }
        }
    }

    /**
     * 追加终端日志
     */
    private void addLog(String log) {
        logQueue.add(log);
        while (logQueue.size() > 20) {
            logQueue.poll();
        }
    }

    /**
     * 平滑关闭线程池
     */
    public void shutdown() {
        this.isRunning = false;
        addLog("[SYSTEM] 接收到 shutdown 指令，准备平滑关闭...");
    }

    /**
     * 启动/恢复线程池
     */
    public synchronized void startPool() {
        if (this.isRunning) {
            addLog("[SYSTEM] 线程池已经在运行中。");
            return;
        }
        this.isRunning = true;
        addLog("[SYSTEM] 接收到 start 指令，线程池开启运行！");

        // 检查 Worker 线程状态，若已死亡则重新创建并启动
        for (int i = 0; i < poolSize; i++) {
            if (i >= workers.size() || !workers.get(i).isAlive()) {
                Worker worker = new Worker("PoolWorker-" + (i + 1));
                if (i < workers.size()) {
                    workers.set(i, worker);
                } else {
                    workers.add(worker);
                }
                worker.start();
                addLog("[SYSTEM] 重新拉起工作线程: " + worker.getName());
            }
        }
    }

    /**
     * 拼接 JSON 数据给前端
     */
    public String getMetricsJson() {
        StringBuilder logsJson = new StringBuilder("[");
        String[] logs = logQueue.toArray(new String[0]);
        for (int i = 0; i < logs.length; i++) {
            logsJson.append("\"").append(logs[i]).append("\"");
            if (i < logs.length - 1) logsJson.append(",");
        }
        logsJson.append("]");

        return String.format(
            "{\"active\":%d, \"total\":%d, \"queueSize\":%d, \"queueCapacity\":%d, \"completed\":%d, \"rejected\":%d, \"isRunning\":%b, \"logs\":%s}",
            activeThreads.get(), workers.size(), taskQueue.size(), poolCapacity, completedTasks.get(), rejectedTasks.get(), isRunning, logsJson
        );
    }

    /**
     * 启动内置 HttpServer
     */
    private void startHttpServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            
            // 1. 指标轮询接口
            server.createContext("/api/metrics", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

                    String response = getMetricsJson();
                    byte[] bytes = response.getBytes("UTF-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
            });

            // 2. 停止接口
            server.createContext("/api/shutdown", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
                    
                    shutdown(); 

                    String response = "线程池平滑关闭指令已下达！";
                    byte[] bytes = response.getBytes("UTF-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
            });

            // 3. 启动接口
            server.createContext("/api/start", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
                    
                    startPool(); 

                    String response = "线程池启动指令已下达！";
                    byte[] bytes = response.getBytes("UTF-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
            });

            server.start();
            System.out.println("监控 API 接口已启动: http://localhost:8080/api/metrics");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 工作线程内部类
     */
    private class Worker extends Thread {
        public Worker(String name) {
            super(name);
        }

        @Override
        public void run() {
            // 当 isRunning 为 false 且 taskQueue 为空时，跳出循环销毁线程
            while (isRunning || !taskQueue.isEmpty()) {
                try {
                    // poll(100ms) 让线程在阻塞时也能定期醒来检查 isRunning 状态
                    Runnable task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (task != null) {
                        activeThreads.incrementAndGet();
                        addLog("[" + getName() + "] 开始执行任务...");

                        task.run(); 

                        completedTasks.incrementAndGet();
                        activeThreads.decrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            addLog("[" + getName() + "] 退出完毕。");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建 3 个工作线程，队列容量为 5
        MyFixedThreadPool pool = new MyFixedThreadPool(3, 5);

        // 持续产生并发任务，模拟无限的业务请求流量
        while (true) {
            pool.submit(() -> {
                try {
                    Thread.sleep((long) (500 + Math.random() * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            Thread.sleep((long) (100 + Math.random() * 400));
        }
    }
}