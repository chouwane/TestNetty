#Protobuf编码解码

###官方的编码解码器（必须指定单一的protobuf类型才行）
ProtobufEncoder：用于对Probuf类型序列化。
ProtobufVarint32LengthFieldPrepender：用于在序列化的字节数组前加上一个简单的包头，只包含序列化的字节长度。
ProtobufVarint32FrameDecoder：用于decode前解决半包和粘包问题（利用包头中的包含数组长度来识别半包粘包）
ProtobufDecoder：反序列化指定的Probuf字节数组为protobuf类型。



#####CustomProtobufEncoder序列化传入的protobuf类型，并且为它创建了一个4个字节的包头，格式如下
![自定义protobuf类型格式](1.png)