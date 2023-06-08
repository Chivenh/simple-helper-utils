package com.fhtiger.helper.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * fpt通用工具
 * @since 2020-04-26 14:38
 * @author LFH
 * */
@SuppressWarnings({"unused"})
public final class FtpHandler {

	private final String url;

	private final String userName;

	private final String password;

	private final int port;

	private FtpHandler(final String url,final String userName,final String password,final int port) {
		this.url = url;
		this.userName = userName;
		this.password = password;
		this.port = port < 1 ?21:port;
	}

	public SimpleResult<String> sendFile(String targetPath,String fileName, InputStream input){
		//初始化表示上传失败
		FTPClient ftp= null;
		SimpleResult.SimpleResultBuilder<String> simpleResultBuilder = SimpleResult.SimpleResultBuilder.of(false);
		try{
			//创建ftpclient对象
			ftp = new FTPClient();
			// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.connect(this.url,this.port);

			//登录ftp服务器
			ftp.login(this.userName, this.password);

			//看返回的值是不是230，如果是，表示登陆成功
			int reply=ftp.getReplyCode();

			//以2开头的返回值就会为真
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return simpleResultBuilder.code(reply+"").build();
			}

			// 转到指定上传目录
			ftp.changeWorkingDirectory(targetPath);

			// 将上传文件存储到指定目录
			ftp.enterLocalPassiveMode();
			if(ftp.storeFile(fileName, input)){
				simpleResultBuilder.success().message("文件保存到服务器成功");
			}

		}catch(Exception e){
			 simpleResultBuilder.fail().message(e.getMessage());
		}finally{
			try {
				//关闭输入流
				input.close();
			} catch (IOException e1) { e1.printStackTrace(); }

			try {
				//退出ftp
				if(ftp!=null){
					ftp.logout();
				}
			} catch (IOException e) { e.printStackTrace(); }

			if(ftp!=null&&ftp.isConnected()){
				try{
					ftp.disconnect();
				}catch(IOException ioe){ ioe.printStackTrace(); }
			}
		}

		return simpleResultBuilder.build();
	}

	/**
	 * FTP连接对象创建具
	 * @see FtpHandler
	 */
	public static class FtpHandlerBuilder{

		private final String url;

		private String userName;

		private String password;

		private int port;

		private FtpHandlerBuilder(String url) {
			this.url = url;
		}

		/**
		 * 初始化路径得到builder对象
		 * @param url ftp://ip
		 * @return {@link FtpHandlerBuilder}
		 */
		public static FtpHandlerBuilder of( final String url){
			return new FtpHandlerBuilder(url);
		}

		/**
		 * 设置端口
		 * @param port 端口，默认21
		 * @return {@link FtpHandlerBuilder}
		 */
		public FtpHandlerBuilder port( int port){
			this.port = port;
			return this;
		}

		/**
		 * 设置登录名
		 * @param userName 登录名
		 * @return {@link FtpHandlerBuilder}
		 */
		public FtpHandlerBuilder byUser( String userName){
			this.userName = userName;
			return this;
		}

		/**
		 * 设置登录密码
		 * @param password 登录密码
		 * @return {@link FtpHandlerBuilder}
		 */
		public FtpHandlerBuilder password( String password){
			this.password = password;
			return this;
		}

		/**
		 * 构建 FtpHandler 对象
		 * @return {@link FtpHandler}
		 */
		public FtpHandler build(){
			return new FtpHandler(this.url,this.userName,this.password,this.port);
		}
	}
}
