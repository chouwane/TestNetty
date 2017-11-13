package com.wh.test.netty.afis.codec;

import com.afis.net.*;
import com.afis.utils.Converter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Map;

/**
 *
 * Created by wanghui on 17-11-11.
 */
public class AfisMessagePacket{

    /**
     * 最大允许包长
     */
    public static final int MAX_BODY_LENGTH = 99999999;
    /**
     * 默认字符集
     */
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final DecimalFormat CHECK_SUM_FORMAT = new DecimalFormat("000");
    private static final byte EQUAL_SIGN = 61;
    private static final byte END_SIGN = 1;

    public static final String BEGIN_STRING = "8";
    protected static final String DEFAULT_BEGIN_STRING = "FIXT.5.0-AFIS.1.0";

    protected static final String BODY_LENGTH = "9";

    public static final String MESSAGE_TYPE = "35";
    public static final String SEQUENCE = "34";
    public static final String MARKET_ID = "207";
    public static final String BROKER_ID = "48";
    public static final String MEMBER_ID = "49";
    public static final String CLIENT_ID = "50";
    public static final String OPERATOR_ID = "51";

    public static final String CONTRACT_ID = "55";
    public static final String FORWORD = "189";

    public static final String RECEIVE_TIME = "122";
    public static final String RESPONSE_TIME = "52";
    public static final String RESPONSE_CODE = "749";

    public static final String ROWS = "8008";
    public static final String COLUMNS = "8009";

    public static final String CHECK_SUM = "10";

    public static final String[] HEADER_ORDERS = new String[]{
            BEGIN_STRING, BODY_LENGTH,
            MESSAGE_TYPE, SEQUENCE, MARKET_ID, BROKER_ID, MEMBER_ID,
            CLIENT_ID, OPERATOR_ID,  CONTRACT_ID, FORWORD,
            RECEIVE_TIME, RESPONSE_TIME, RESPONSE_CODE};

    protected final int TAIL_BYTE_SIZE;
    public AfisMessagePacket()
    {
        TAIL_BYTE_SIZE = this.getBytes(CHECK_SUM).length + this.getBytes("000").length + 2;
    }

    /**
     * 编码消息
     * @param message
     * @return
     * @throws MessageFormatException
     */
    public ByteBuf packet(Message message) throws MessageFormatException {
        ByteBuf buffer = Unpooled.buffer();

        DataHeader header = message.getHeader();
        if(!isRequest(message)){
            header.setLong(RESPONSE_TIME, System.currentTimeMillis());
        }

        //编码消息头
        this.packet(header, buffer, BEGIN_STRING, BODY_LENGTH, CHECK_SUM);

        //编码消息体
        if(!message.isEmpty()){
            this.packet(message, null, buffer);
        }

        int bodyLength = buffer.readableBytes();

        String checkSum = this.getCheckSum(buffer, 0, bodyLength);
        this.packet(CHECK_SUM, checkSum, buffer);

        ByteBuf b = Unpooled.buffer();
        this.packet(BEGIN_STRING, DEFAULT_BEGIN_STRING, b);
        this.packet(BODY_LENGTH, String.valueOf(bodyLength), b);
        b.writeBytes(buffer);//TODO 关注这个方法抛出的异常
        return b;
    }

    /**
     * 解码消息
     * @param in
     * @return
     * @throws MessageFormatException
     */
    public Message unPack(ByteBuf in) throws MessageFormatException {
        Message message = this.prefixedDataAvailable(in);
        if(message == null){
            return null;
        }
        int rows = this.parseHeader(message, in);
        this.parseBody(message, in, rows);
        return message;
    }


    /**
     * 编码DataRow数据
     * @param row
     * @param buffer
     * @param excepts 不参加编码的tag
     */
    protected void packet(DataRow row, ByteBuf buffer, String...excepts)
    {
        for(Map.Entry<String, Object> e : row)
        {
            String tag = e.getKey();
            if(this.exceptTag(tag, excepts)){
                continue;
            }
            Object value = e.getValue();
            if(value instanceof DataGroup)
            {
                this.packet((DataGroup)value, tag, buffer);
            }
            else {
                this.packet(tag, (String)value, buffer);
            }
        }
    }

    /**
     * 编码DataGroup数据
     * @param group
     * @param tag
     * @param buffer
     */
    protected void packet(DataGroup group, String tag, ByteBuf buffer)
    {
        String v = String.valueOf(group.size());
        if(tag != null){
            v += " " + tag;
        }
        this.packet(ROWS, v, buffer);
        for(DataRow row : group)
        {
            this.packet(COLUMNS, String.valueOf(row.size()), buffer);
            this.packet(row, buffer);
        }
    }

    private boolean exceptTag(String tag, String...excepts)
    {
        if(excepts == null){
            return false;
        }
        for(int i = 0; i<excepts.length; i++)
        {
            if(excepts[i].equals(tag)){
                return true;
            }
        }
        return false;
    }

    protected String getCheckSum(ByteBuf in, int offset, int length)
    {
        byte[] b = new byte[length];
        in.getBytes(offset, b);

        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += b[i] & 0xFF;
        }
        return CHECK_SUM_FORMAT.format(Math.abs(sum + 1) % 256);
    }

    /**
     * 编码<tag, value>
     * @param tag
     * @param value
     * @param buffer
     */
    protected void packet(String tag, String value, ByteBuf buffer)
    {
        this.packet(tag, this.getBytes(value), buffer);
    }

    /**
     * 编码<tag, value>
     * @param tag
     * @param value
     * @param buffer
     */
    private void packet(String tag, byte[] value, ByteBuf buffer)
    {
        buffer.writeBytes(this.getBytes(tag));
        buffer.writeByte(EQUAL_SIGN);
        buffer.writeBytes(value);
        buffer.writeByte(END_SIGN);
    }

    protected byte[] getBytes(String value)
    {
        return value.getBytes(DEFAULT_CHARSET);
    }

    /**
     * 解析消息头
     * @param message
     * @param in
     * @return
     */
    protected int parseHeader(Message message, ByteBuf in)
    {
        String[] kv;
        int rows = 0;
        //Header
        DataHeader header = message.getHeader();
        while(true)
        {
            kv = this.parse(in);
            if(isHeaderTag(kv[0])) {
                header.setString(kv[0], kv[1]);
            }
            else
            {
                if(isBodyBegin(kv[0])){
                    rows = Converter.getIntPrimitive(kv[1]);
                }
                break;
            }
        }
        if(isRequest(message)){
            header.setLong(RECEIVE_TIME, System.currentTimeMillis());
        }
        return rows;
    }

    /**
     * 解析消息体
     * @param message
     * @param in
     * @param rows
     */
    protected void parseBody(Message message, ByteBuf in, int rows)
    {
        //body
        if(rows > 0)
        {
            this.parseGroup(in, message, rows);
            in.readerIndex(in.readerIndex() + TAIL_BYTE_SIZE);
        }
    }

    /**
     * 校验消息有效性
     * @param in
     * @return
     */
    protected Message prefixedDataAvailable(ByteBuf in)
    {
        Message message = new Message(HEADER_ORDERS);
        int position = in.readerIndex();
        String[] kv;
        int bodyLength;

        try
        {
            kv = this.parse(in);
        }
        catch(Exception e)
        {
            in.readerIndex(position);
            return null;
        }
        if(kv[0].equals(BEGIN_STRING)) {
            message.getHeader().setString(BEGIN_STRING, kv[1]);
        }else{
            throw new MessageFormatException("No BEGIN_STRING or not at the right position.");
        }

        try
        {
            kv = this.parse(in);
        }
        catch(Exception e)
        {
            in.readerIndex(position);
            return null;
        }
        if(kv[0].equals(BODY_LENGTH)) {
            message.getHeader().setInt(BODY_LENGTH, bodyLength = Converter.getIntPrimitive(kv[1]));
        }
        else{
            throw new MessageFormatException("No BEGIN_STRING or not at the right position.");
        }

        if(bodyLength > MAX_BODY_LENGTH || bodyLength < 0) {
            throw new MessageFormatException("No BEGIN_STRING or not at the right position.");
        }
        if(in.readableBytes() < bodyLength + TAIL_BYTE_SIZE)
        {
            in.readerIndex(position);
            return null;
        }

        int index = in.readerIndex();
        String checkSum = this.getCheckSum(in, index, bodyLength);
        in.readerIndex(index + bodyLength);
        kv = this.parse(in);
        if(kv[0].equals(CHECK_SUM))
        {
            if(kv[1].equals(checkSum)){
                message.getHeader().setString(CHECK_SUM, checkSum);
            }
            else{
                throw new MessageFormatException("Check mesasge fail.");
            }
            in.readerIndex(index);
        }
        else {
            throw new MessageFormatException("No CHECK_SUM or not at the right position.");
        }

        return message;
    }

    /**
     * 解析出 tag=value
     * @param in
     * @return
     */
    protected String[] parse(ByteBuf in)
    {
        int index = in.bytesBefore(EQUAL_SIGN);
        byte[] bytes = new byte[index];
        in.readBytes(bytes);
        String tag = new String(bytes, DEFAULT_CHARSET);
        in.readByte();//EQUAL_SIGN


        index = in.bytesBefore(END_SIGN);
        bytes = new  byte[index];
        in.readBytes(bytes);
        String value =  new String(bytes, DEFAULT_CHARSET);
        in.readByte();//END_SIGN
        return new String[]{tag, value};
    }

    /**
     * 解析DataGroup
     * @param in
     * @param group
     * @param rows
     */
    private void parseGroup(ByteBuf in, DataGroup group, int rows)
    {
        for(int i = 0; i<rows; i++)
        {
            String[] kv = this.parse(in);
            int cols = Converter.getIntPrimitive(kv[1]);
            DataRow row = new DataRow();
            group.add(row);
            for(int j = 0; j<cols; j++)
            {
                kv = this.parse(in);
                if(ROWS.equals(kv[0]))
                {
                    kv = kv[1].split(" ");
                    DataGroup g = new DataGroup();
                    row.setGroup(kv[1], g);
                    this.parseGroup(in, g, Converter.getIntPrimitive(kv[0]));
                }
                else {
                    row.setString(kv[0], kv[1]);
                }
            }
        }
    }

    private static boolean isHeaderTag(String tag)
    {
        for(int i = 0; i<HEADER_ORDERS.length; i++)
        {
            if(HEADER_ORDERS[i].equals(tag)) return true;
        }
        return false;
    }
    private static boolean isBodyBegin(String tag)
    {
        return ROWS.equals(tag);
    }

    public static boolean isRequest(Message message)
    {
        return message.getHeader().get(RESPONSE_CODE) == null;
    }


    /**
     * 创建请求消息
     * @param messageType
     * @param sequence
     * @param marketId
     * @return
     */
    public static Message createRequestMessage(String messageType, long sequence, String marketId){
        Message message = new Message(HEADER_ORDERS);
        message.getHeader()
                .setString(MESSAGE_TYPE, messageType)
                .setLong(SEQUENCE, sequence)
                .setString(MARKET_ID, marketId);
        return message;
    }

    /**
     * 创建应答包
     * @param request
     * @param responseCode
     * @return
     */
    public static Message createResponseMessage(Message request, int responseCode)
    {
        Message message = new Message(HEADER_ORDERS);
        DataHeader header = request.getHeader();
        message.getHeader()
                .setString(MESSAGE_TYPE, header.getString(MESSAGE_TYPE))
                .setLong(SEQUENCE, header.getLong(SEQUENCE))
                .setString(MARKET_ID, header.getString(MARKET_ID))
                .setString(BROKER_ID, header.getString(BROKER_ID))
                .setString(MEMBER_ID, header.getString(MEMBER_ID))
                .setDate(RECEIVE_TIME, header.getDate(RECEIVE_TIME))
                .setString(OPERATOR_ID, header.getString(OPERATOR_ID))
                .setInt(RESPONSE_CODE, responseCode);
        return message;
    }

}
