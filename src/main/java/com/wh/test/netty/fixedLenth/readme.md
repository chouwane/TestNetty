消息固定长度

客户端没有写

####测试时通过telnet命令来发送消息给服务器
1. 在命令行上输入telnet localhost 8081
2. 输入消息内容
3. 回车后，如果已经超过固定长度了，则会在控制台上看到打印的消息
没有超过固定长度，说明还没有收到一个完整的消息，是不会打印出来的