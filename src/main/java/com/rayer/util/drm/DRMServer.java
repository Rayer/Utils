package com.rayer.util.drm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.rayer.util.drm.DRMServer.IRequestReply.ReplyType;
import com.rayer.util.stream.StreamUtil;

public class DRMServer extends Decryptable {
	ServerSocket mServerSocket;
	int mBindingPort;
	WorkerThread mWorkerThread;
	

	//EncrypterBase mKey;
	
	//Book mHostBook;
	AESEncryptKeys mKeys;
	
	String mRootDir;
	
//	public HamiDRMServerThread(Book book, AESEncryptKeys keys, int bindingPort) {
//		//mHostBook = book;
//		mKeys = keys;
//		mBindingPort = bindingPort;
//		//mRootDir = book.getExtractPath();
//		mRootDir = book.getHostBookManager().getBookWorkingDirectory(book) + "/OEBPS";
//		
//	}
	
	public DRMServer(String hostBookPath, AESEncryptKeys keys, int bindingPort) {
		mKeys = keys;
		mBindingPort = bindingPort;
		//mRootDir = book.getExtractPath();
		mRootDir = hostBookPath + "/OEBPS";
	}
	
	public int getBindingPort() {
		return mBindingPort;
	}
	
	static HashMap<String, IRequestReply> mDesignatedReplyMap = new HashMap<String, IRequestReply >();
	
	interface IRequestReply {
		enum ReplyType {PlainText, JavaScript, JPG, PNG};
		
		Object getData();
		InputStream getDataStream();
		long getDataLength();
		ReplyType getType();
	}
	
	static private class RequestReplyFile implements IRequestReply {

		File mFile;

		RequestReplyFile(File f) {
			mFile = f;
		}
		
		
		@Override
		public File getData() {
			return mFile;
		}

		@Override
		public FileInputStream getDataStream() {
			try {
				return new FileInputStream(mFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Error creating FileStream");
			}
		}

		@Override
		public long getDataLength() {
			return mFile.length();
		}

		@Override
		public ReplyType getType() {
			String identifier = mFile.getName().toLowerCase();
			if(identifier.contains(".jpg") || identifier.contains(".jpeg"))
				return ReplyType.JPG;
			else if(identifier.contains(".png"))
				return ReplyType.PNG;
			else if(identifier.contains("epubjs"))
				return ReplyType.JavaScript;
			
			return ReplyType.PlainText;		
		}
		
	}
	
	static private class RequestReplyString implements IRequestReply {

		String mString;
		ReplyType mType;
		
		RequestReplyString(String s, ReplyType type) {
			mString = s;
			mType = type;
		}
		
		@Override
		public String getData() {
			return mString;
		}

		@Override
		public ByteArrayInputStream getDataStream() {
			try {
				return new ByteArrayInputStream(mString.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				throw new RuntimeException("error encoding, maybe caused by old platform that do not support utf-8");
			}
		}

		@Override
		public long getDataLength() {
			if(mString == null)
				return 0;
			return mString.length();
		}

		@Override
		public ReplyType getType() {
			return mType;
		}
		
	}
	
//ÈÄôÂØ´Ê≥ïÊØîËºÉÁ∞°ÊΩî ÂèØÊòØ...
//	static private class RequestReplyMethod<T> {
//
//		T mReplyData;
//		RequestReplyMethod(T data) {
//			mReplyData = data;
//		}
//	}
	
	/**
	 * Ë®≠ÂÆöÁâπÊÆäÂõûÂÇ≥Áõ¥„ÄÇÁï∂requestÂà∞ÈÄôÂÄãÁâπÊÆäÂõûÂÇ≥ÂÄºÊôÇÔºå‰ªñÊúÉÂèñ‰ª£ÊéâÂéüÊúâÁöÑÊñπÊ≥ïÔºåÊîπÂÇ≥ÂõûÊåáÂÆöÁöÑÂ≠ó‰∏≤
	 * @param specifiedRequest ÊåáÂÆöÂõûÂÇ≥Áõ¥ÁöÑÊ™îÂêç„ÄÇË´ãÁâπÂà•Ê≥®ÊÑèÈÄôÂÄã‰∏çÂàÜÂ§ßÂ∞èÂØ´ÔºÅ
	 * @param reply ÊåáÂÆöÂõûÂÇ≥Áõ¥ÁöÑÂÖßÂÆπ, null‰ª£Ë°®Áõ¥Êé•ÁúãÂà∞ÈÄôÊù±Ë•øÂ∞±ÂøΩÁï•Êéâ
	 */
	public void setDesignatedRequestReply(String specifiedRequest, String reply) {
		String request = specifiedRequest.toLowerCase();
		if(specifiedRequest.startsWith("/") == false)
			request = "/" + request;
		mDesignatedReplyMap.put(request, new RequestReplyString(reply, request.contains("epubjs") ? ReplyType.JavaScript : ReplyType.PlainText));	
	}
	
	/**
	 * Ë®≠ÂÆöÁâπÊÆäÂõûÂÇ≥Áõ¥„ÄÇÁï∂requestÂà∞ÈÄôÂÄãÁâπÊÆäÂõûÂÇ≥ÂÄºÊôÇÔºå‰ªñÊúÉÂèñ‰ª£ÊéâÂéüÊúâÁöÑÊñπÊ≥ïÔºåÊîπÂÇ≥ÂõûÊåáÂÆöÁöÑÊ™îÊ°àÂÖßÂÆπ
	 * @param specifiedRequest ÊåáÂÆöÂõûÂÇ≥Áõ¥ÁöÑÊ™îÂêç„ÄÇË´ãÁâπÂà•Ê≥®ÊÑèÈÄôÂÄã‰∏çÂàÜÂ§ßÂ∞èÂØ´ÔºÅ
	 * @param reply ÊåáÂÆöÂõûÂÇ≥Áõ¥ÁöÑÊ™îÊ°à, null‰ª£Ë°®Áõ¥Êé•ÁúãÂà∞ÈÄôÊù±Ë•øÂ∞±ÂøΩÁï•Êéâ
	 */
	public void setDesignatedRequestReply(String specifiedRequest, File replyFileHandle) {
		String request = specifiedRequest.toLowerCase();
		if(specifiedRequest.startsWith("/") == false)
			request = "/" + request;
		mDesignatedReplyMap.put(request, new RequestReplyFile(replyFileHandle));
	}
	
	public void setDesignatedRequestIgnore(String specifiedRequest) {
		String request = specifiedRequest.toLowerCase();
		if(specifiedRequest.startsWith("/") == false)
			request = "/" + request;
		mDesignatedReplyMap.put(request, null);
	}

//	//logÁõ∏Èóú
//	static boolean VERBOSE = false;
//	static String VERBOSE_TITLE = "DRMServer";
//	void logD(String log) {
//		if(VERBOSE)
//			Log.d(VERBOSE_TITLE, log);
//	}
//	
//	void logE(String log) {
//		if(VERBOSE)
//			Log.e(VERBOSE_TITLE, log);
//	}
//	
//	static void global_setVerbose(boolean verbose) {
//		VERBOSE = verbose;
//	}
//	
//	static void global_setVerboseTitle(String title) {
//		VERBOSE_TITLE = title;
//	}
	
	static final String CRLF = "\r\n";
	
	public int startServer() throws IOException {
		if(mWorkerThread != null)
			return mBindingPort;
		
		mWorkerThread = new WorkerThread();
		mWorkerThread.start();
		return mBindingPort;
	}
	
	public void terminateServer() {
		if(mWorkerThread == null)
			return;
		
		try {
			mWorkerThread.terminate();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mWorkerThread = null;
	}

	class WorkerThread extends Thread {

		boolean mIsRunning = true;
		//String mRootDir = "/sdcard/.hamibook/test/";
		
		WorkerThread() throws IOException {
			
			mServerSocket = new ServerSocket();
			mServerSocket.bind(new InetSocketAddress(mBindingPort));
			
		}
		
		public void terminate() throws IOException {
			mIsRunning = false;
			mServerSocket.close();
		}

		@Override
		public void run() {
			while(mIsRunning) {
				try {
					
					Socket acceptSocket = mServerSocket.accept();
					
					//logD("Socket Accepted.");
					
					InputStream is = acceptSocket.getInputStream();
					OutputStream os = acceptSocket.getOutputStream();
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					processReader(reader, os);
					
					is.close();
					os.close();
					reader.close();
					acceptSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
			}
		}
		

		private void parseGetCommand(String commandToken, OutputStream os) throws IOException {
			
			//logD("Command Token : " + commandToken);
			//ÂÖàÊ™¢Êü•ÊòØÂê¶Âú®mDesignatedReplyMapË£°Èù¢
			if(mDesignatedReplyMap.containsKey(commandToken.toLowerCase()) == true) {
				
				//logD("Exclusive token : " + commandToken);
				IRequestReply request = mDesignatedReplyMap.get(commandToken.toLowerCase()); 
				if(request == null)
					return;
				
				generateFileHeader(request, os);
				writeToOutput(request.getDataStream(), os);
				return;
			}

			
			File file = new File(mRootDir + commandToken);
			if(file.exists() == false) {
				
				//Log.e("DRMServer", "file " + file.getAbsolutePath() + " is not found!");

				invokeHttpError404(os);
				return;
			}
			
			generateFileHeader(file, os);
			writeToOutput(createDecryptStream(file), os);
			
			
		}

		private void writeToOutput(InputStream is, OutputStream os) throws IOException {
			//Ëß£ÂØÜ‰ª•ÂèäËº∏Âá∫
			//FileInputStream fis = new FileInputStream(file);
			//InputStream fis = createDecryptStream(file);
			
			//logD("Write to Output : ");
			byte[] buffer = new byte[8092];
			int len;
			while((len = is.read(buffer)) > 0){
				//logD(new String(buffer));
				os.write(buffer, 0, len);
			}
			
			
		}

		private void generateFileHeader(File file, OutputStream os) throws IOException {
			

			StringBuilder sb = new StringBuilder();
			//ÈÄô‰∏ÄÂÆöË¶ÅÁ¨¨‰∏ÄË°å
			
			sb.append("HTTP/1.1 200 OK" + CRLF);

			String contentTypeLine;
			String fileName = file.getName();
			
			if(fileName.toLowerCase().contains(".jpeg") || fileName.toLowerCase().contains(".jpg"))
				contentTypeLine = "image/jpeg" + CRLF ;
			else if(fileName.toLowerCase().contains(".png"))
				contentTypeLine = "image/png" + CRLF ;
			else
				contentTypeLine = "text/html" + CRLF ;
			
			sb.append("Keep-Alive: timeout=15, max=500" + CRLF);
			sb.append("Connection: Keep-Alive" + CRLF);
			sb.append("Content-Type: " + contentTypeLine);
			sb.append("Content-Length: " + file.length() + CRLF);
			
			//logD("server response : " + sb.toString());
			
			sb.append(CRLF);
			os.write(sb.toString().getBytes());
			
		}
		
		private void generateFileHeader(IRequestReply r, OutputStream os) throws IOException {
			StringBuilder sb = new StringBuilder();
			
			String contentTypeLine;
			sb.append("HTTP/1.1 200 OK" + CRLF);
			if(r.getType() == ReplyType.JPG)
				contentTypeLine = "image/jpeg" + CRLF ;
			else if (r.getType() == ReplyType.PNG)
				contentTypeLine = "image/png" + CRLF ;
			else if(r.getType() == ReplyType.JavaScript)
				contentTypeLine = "text/javascript" + CRLF ;
			else
				contentTypeLine = "text/html" + CRLF ;
			
		
			
			sb.append("Keep-Alive: timeout=15, max=500" + CRLF);
			sb.append("Connection: Keep-Alive" + CRLF);

			sb.append("Content-Type: " + contentTypeLine);
			sb.append("Content-Length: " + r.getDataLength() + CRLF);
			sb.append(CRLF);

			//logD("server response : " + sb.toString());
			
			os.write(sb.toString().getBytes());

		}
		
		public String getDecryptedContent(String contentName) throws FileNotFoundException, IOException {
			File file = new File(mRootDir + contentName);
			if(file.exists() == false) {
				return null;
			}
				
			return StreamUtil.InputStreamToString(createDecryptStream(file));
		}

		private void invokeHttpError404(OutputStream os) throws IOException {
			os.write(("HTTP/1.0 404 Not Found" + CRLF).getBytes());
		}

		public void processWriteToClient(OutputStream os) {
			
		}
		
		
		public void processReader(BufferedReader reader, OutputStream os) throws IOException {
			String command = "null command";
			while(command != null && command.equals("") == false && command.equals(CRLF) == false) {
				command = reader.readLine();
				
				if(command == null)
					return;
				
				StringTokenizer st = new StringTokenizer(command);
				String header = "";
				if(st.hasMoreTokens())
					header = st.nextToken();
				
				if(header.equalsIgnoreCase("GET"))
					parseGetCommand(st.nextToken(), os);				
			}
			
			
		}
		
	}

	@Override
	public AESEncryptKeys getAESKeys() {
		return mKeys;
	}


}