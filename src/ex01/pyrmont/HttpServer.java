package ex01.pyrmont;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

public class HttpServer {

    /**
     * WEB_ROOT是我们的 HTML 和其他文件所在的目录。对于此软件包，
     * WEB_ROOT是工作目录下的“webroot”目录。
     * 工作目录是文件系统中调用 java 命令的位置。
     * WEB_ROOT is the directory where our HTML and other files reside.
     * For this package, WEB_ROOT is the "webroot" directory under the working
     * directory.
     * The working directory is the location in the file system
     * from where the java command was invoked.
     */
    public static final String WEB_ROOT =
            System.getProperty("user.dir") + File.separator + "webroot";

    // shutdown command
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    // the shutdown command received
    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Loop waiting for a request
        //循环等待请求
        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                //循环接收
                socket = serverSocket.accept();
                //获取输入输出流
                input = socket.getInputStream();
                output = socket.getOutputStream();

                // create Request object and parse
                Request request = new Request(input);
                //解析请求（这里还是由Request解析）
                request.parse();

                // create Response object
                Response response = new Response(output);
                response.setRequest(request);
                //根据请求资源进行写回操作
                response.sendStaticResource();

                // Close the socket
                socket.close();

                //check if the previous URI is a shutdown command
                //当用户访问/SHUTDOWN时，终止服务器
                shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
