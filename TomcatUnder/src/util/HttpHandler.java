package util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class HttpHandler implements Runnable{

	private int buffersize = 1024;// 设置缓冲区大小

	private String localCharSet = "UTF-8"; // 设置编码格式

	private SelectionKey key;// 注册号的通道

	public HttpHandler(SelectionKey key) {// 把选择键构造进去

		this.key = key;
	}

	public void handleAccept() throws IOException {

		SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();

		socketChannel.configureBlocking(false);// 设置非阻塞模式

		socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(buffersize));
		// buffer分配一个缓冲区 大小为1024

	}

	public void handleRead() {

		SocketChannel sc = (SocketChannel) key.channel();// SocketChannel 是一个连接到 TCP 网络套接字的通道

		ByteBuffer buffer = (ByteBuffer) key.attachment();// 从 SocketChannel读取到的数据将会放到这个 buffer中

		buffer.clear();

		try {
			if ((sc.read(buffer)) != -1) {

				buffer.flip();// flip方法将Buffer从写模式切换到读模式

				String receive = Charset.forName(localCharSet).newDecoder().decode(buffer).toString();
				// 将此 charset 中的字节解码成 Unicode 字符

				String[] requestMessage = receive.split("\r\n");// 接受请求的信息

				for (String message : requestMessage) {

					if (message.isEmpty()) {// 如果是空行说明信息已经结束了

						break;
					}
				}
				// 控制台打印
				String[] firsetLine = requestMessage[0].split(" ");

				System.out.println("----控制台输出：-------");

				System.out.println("Method:t" + firsetLine[0]);

				System.out.println("url是:\t" + firsetLine[1]);

				System.out.println("Httpversion是:\t" + firsetLine[2]);

				System.out.println("-----输出结束-------------");

				// 返回客户端
				StringBuilder sendStr = new StringBuilder();

				sendStr.append("Http/1.1 200 Ok\r\n");

				sendStr.append("Content-Type:text/html;charset=" + localCharSet + "\r\n");

				sendStr.append("\r\n");

				sendStr.append("<html><head><title>显示报文</title></head><body>");

				sendStr.append("接受到请求的报文是：+<br>");

				for (String s : requestMessage) {

					sendStr.append(s + "<br/>");

				}
				sendStr.append("</body></html>");

				buffer = ByteBuffer.wrap(sendStr.toString().getBytes(localCharSet));

				sc.write(buffer);

				sc.close();
			} else {
				sc.close();

			}
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	@Override
	public void run() {

		try {
			if (key.isAcceptable()) {// 接受

				handleAccept();

			}
			if (key.isReadable()) {// 开始读

				handleRead();

			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
